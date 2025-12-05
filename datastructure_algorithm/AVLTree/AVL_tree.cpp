#include "AVL.hpp"

void	AVL_tree::AVL_insert(element data)
{
	TreeNode	*leaf_node;
	TreeNode	*unbalance_node;

	leaf_node = binary_search_tree::insert(data);
	if (!leaf_node)
		return ;
	unbalance_node = search_unbalance_tree(leaf_node->parent);
	if (!unbalance_node)
		return ;
	resolve_unbalance(unbalance_node);
}

void	AVL_tree::resolve_unbalance(TreeNode *unbalance_node)
{
	char	unbalance;

	unbalance = check_unbalance_form(unbalance_node);
	switch (unbalance)
	{
		case left_right::LL:
			this->LL(unbalance_node);
			break;
		case left_right::LR:
			this->LR(unbalance_node);
			break;
		case left_right::RL:
			this->RL(unbalance_node);
			break;
		case left_right::RR:
			this->RR(unbalance_node);
			break;
	}
}

char	AVL_tree::check_unbalance_form(TreeNode *unbalance_node)
{
	char	check = 0;

	if (compare_subtrees_height(unbalance_node) >= 0)
		unbalance_node = unbalance_node->leftchild;
	else
	{
		unbalance_node = unbalance_node->rightchild;
		check += 2;
	}
	if (compare_subtrees_height(unbalance_node) < 0)
		check += 1;
	return (check);
}

TreeNode	*AVL_tree::search_unbalance_tree(TreeNode *node)
{
	if (!node)
		return (NULL);
	int	balance;

	balance = compare_subtrees_height(node);
	if (abs(balance) >= 2)
		return (node);
	else
		return (search_unbalance_tree(node->parent));
}

int	AVL_tree::get_height(TreeNode	*node)
{
	if (!node)
		return (0);
	return (max(get_height(node->leftchild), get_height(node->rightchild)) + 1);
}

int		AVL_tree::compare_subtrees_height(TreeNode *parent)
{
	return get_height(parent->leftchild) - get_height(parent->rightchild);
}

void	AVL_tree::LL(TreeNode *node)
{
	TreeNode	*temp;

	temp = node->leftchild;
	if (binary_tree::get_root() == node)
		set_root(temp);
	else	/*subtree의 root 변경(parents와 관계만)*/
	{
	if (node->parent->data > node->data)
		node->parent->leftchild = temp;
	else
		node->parent->rightchild = temp;
	}
	temp->parent = node->parent;
	
	/*L의 R을 R의 L로 변경*/
	node->leftchild = temp->rightchild;
	if (node->leftchild)
		node->leftchild->parent = node;
	/*root를 L의 R로 변경*/
	temp->rightchild = node;
	node->parent = temp;
}

void	AVL_tree::RR(TreeNode *node)
{
	TreeNode	*temp;

	temp = node->rightchild;
	if (binary_tree::get_root() == node)
		set_root(temp);
	else
	{
	if (node->parent->data > node->data)
		node->parent->leftchild = temp;
	else
		node->parent->rightchild = temp;
	}
	temp->parent = node->parent;

	node->rightchild = temp->leftchild;
	if (node->rightchild)
		node->rightchild->parent = node;

	temp->leftchild = node;
	node->parent = temp;


}
void	AVL_tree::RL(TreeNode *node)
{
	LL(node->rightchild);
	RR(node);
}
void	AVL_tree::LR(TreeNode *node)
{
	RR(node->leftchild);
	LL(node);
}
