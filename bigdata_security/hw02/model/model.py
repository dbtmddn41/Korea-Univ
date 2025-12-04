import torch
import torch.nn as nn
import torch.nn.functional as F

class FeatureExtractor(nn.Module):
    def __init__(self,
                 input_dim: int = 120,
                 mcl_filters: int = 10,
                 conv1_out: int = 32,
                 conv2_out: int = 16,
                 lstm_hidden: int = 64,
                 attn_dim: int = 32,
                 num_classes: int = 5):
        """
        input_dim: number of input features per sample (e.g. 120)
        num_classes: number of target classes for classification
        """
        super().__init__()

        # Mean Convolutional Layer (MCL)
        self.mcl = nn.Conv1d(in_channels=1,
                             out_channels=mcl_filters,
                             kernel_size=1)

        # Two-stage CNN
        self.conv1 = nn.Conv2d(in_channels=1,
                               out_channels=conv1_out,
                               kernel_size=(1, input_dim // mcl_filters),
                               bias=True)
        self.conv2 = nn.Conv2d(in_channels=conv1_out,
                               out_channels=conv2_out,
                               kernel_size=(5, 5),
                               padding=0,
                               bias=True)
        self.pool = nn.MaxPool2d(kernel_size=3, stride=2)

        # Bidirectional LSTM
        self.bilstm = nn.LSTM(input_size=mcl_filters,
                              hidden_size=lstm_hidden,
                              num_layers=1,
                              batch_first=True,
                              bidirectional=True)

        # Self-attention
        self.attn_proj = nn.Linear(2 * lstm_hidden, attn_dim, bias=True)
        self.attn_score = nn.Linear(attn_dim, 1, bias=False)

        # Compute CNN feature dimension dynamically
        with torch.no_grad():
            dummy = torch.zeros(1, 1, mcl_filters, input_dim)
            c1 = F.relu(self.conv1(dummy))
            c2 = F.relu(self.conv2(c1))
            p = self.pool(c2)
            cnn_feat_dim = p.view(1, -1).size(1)

        # Classification head for num_classes
        self.classifier = nn.Linear(cnn_feat_dim + 2 * lstm_hidden,
                                    num_classes)

    def forward(self, x: torch.Tensor) -> torch.Tensor:
        B = x.size(0)

        # MCL
        x_mcl = x.unsqueeze(1)
        mcl_out = F.relu(self.mcl(x_mcl))  # (B, mcl_filters, input_dim)

        # CNN path
        cnn_in = mcl_out.unsqueeze(1)
        c1 = F.relu(self.conv1(cnn_in))
        c2 = F.relu(self.conv2(c1))
        p = self.pool(c2)
        cnn_feat = p.view(B, -1)

        # LSTM + Attention path
        seq = mcl_out.permute(0, 2, 1)
        lstm_out, _ = self.bilstm(seq)
        u = torch.tanh(self.attn_proj(lstm_out))
        scores = self.attn_score(u).squeeze(-1)
        alpha = F.softmax(scores, dim=1)
        attn_feat = torch.sum(lstm_out * alpha.unsqueeze(-1), dim=1)

        # Concatenate features
        features = torch.cat([cnn_feat, attn_feat], dim=1)
        # Classifier
        logits = self.classifier(features)
        return logits
