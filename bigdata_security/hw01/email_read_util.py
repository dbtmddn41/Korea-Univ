import string
import email
import nltk
# 향상된 텍스트 전처리
from nltk.stem import WordNetLemmatizer
from bs4 import BeautifulSoup
import re

punctuations = list(string.punctuation)
stopwords = set(nltk.corpus.stopwords.words('english'))
stemmer = nltk.PorterStemmer()

# Combine the different parts of the email into a flat list of strings
def flatten_to_string(parts):
    ret = []
    if type(parts) == str:
        ret.append(parts)
    elif type(parts) == list:
        for part in parts:
            ret += flatten_to_string(part)
    elif parts.get_content_type == 'text/plain':
        ret += parts.get_payload()
    return ret

# Extract subject and body text from a single email file
def extract_email_text(path):
    # Load a single email from an input file
    with open(path, errors='ignore') as f:
        msg = email.message_from_file(f)
    if not msg:
        return ""

    # Read the email subject
    subject = msg['Subject']
    if not subject:
        subject = ""

    # Read the email body
    body = ' '.join(m for m in flatten_to_string(msg.get_payload()) if type(m) == str)
    if not body:
        body = ""

    email_text = subject + ' ' + body

    # URL 및 이메일 주소를 특수 토큰으로 대체
    email_text = re.sub(r'https?://\S+', 'URL_TOKEN', email_text)
    email_text = re.sub(r'\S+@\S+', 'EMAIL_TOKEN', email_text)
    # 특수 문자 처리
    email_text = re.sub(r'[^\w\s]', ' ', email_text)
    return email_text

# Process a single email file into stemmed tokens
def load(path):
    email_text = extract_email_text(path)
    if not email_text:
        return []

    # Tokenize the message
    tokens = nltk.word_tokenize(email_text)

    # Remove punctuation from tokens
    tokens = [i.strip("".join(punctuations)) for i in tokens if i not in punctuations]

    # Remove stopwords and stem tokens
    if len(tokens) > 2:
        return [stemmer.stem(w) for w in tokens if w not in stopwords]
    return []



def improved_extract_email_text(path):
    email_text = extract_email_text(path)
    # HTML 태그 제거
    email_text = BeautifulSoup(email_text, "html.parser").get_text()
    # URL 및 이메일 주소를 특수 토큰으로 대체
    email_text = re.sub(r'https?://\S+', 'URL_TOKEN', email_text)
    email_text = re.sub(r'\S+@\S+', 'EMAIL_TOKEN', email_text)
    # 특수 문자 처리
    email_text = re.sub(r'[^\w\s]', ' ', email_text)
    return email_text