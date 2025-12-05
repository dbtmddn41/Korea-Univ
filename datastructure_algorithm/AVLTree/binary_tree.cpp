#include "AVL.hpp"

binary_tree::binary_tree(element data)
{
	root = create_node(data);
	root->parent = NULL;
	root->rightchild = NULL;
	root->leftchild = NULL;
}

binary_tree::~binary_tree()
{
	delete_tree(root);
}

void	binary_tree::delete_tree(TreeNode *node)
{
	if (node == NULL)
		return ;
	delete_tree(node->leftchild);
	delete_tree(node->rightchild);
	delete node;
}

TreeNode	*binary_tree::create_node(element data)
{
	TreeNode	*new_node = new TreeNode;

	new_node->data = data;
	new_node->leftchild = NULL;
	new_node->rightchild = NULL;
	new_node->parent = NULL;
	return (new_node);
}

TreeNode	*binary_tree::insert_left_child(TreeNode *parent_node, element data)
{
	if (!parent_node)
		return (0);

	TreeNode	*new_node;
	
	new_node = create_node(data);
	new_node->parent = parent_node;
	new_node->rightchild = NULL;
	new_node->leftchild = NULL;
	parent_node->leftchild = new_node;
	return (new_node);
}

TreeNode	*binary_tree::insert_right_child(TreeNode *parent_node, element data)
{
	if (!parent_node)
		return (0);

	TreeNode	*new_node;
	
	new_node = create_node(data);
	new_node->parent = parent_node;
	new_node->rightchild = NULL;
	new_node->leftchild = NULL;
	parent_node->rightchild = new_node;
	return (new_node);
}

element	binary_tree::get_left_child(TreeNode *node)	const
{
	return (node->leftchild->data);
}

element	binary_tree::get_right_child(TreeNode *node)	const
{
	return (node->rightchild->data);
}

TreeNode*	binary_tree::get_root()	const
{
	return (this->root);
}

TreeNode	*binary_tree::set_root(TreeNode *node)
{
	root = node;
	return (node);
}

void	binary_tree::preorder_traversal(TreeNode *node, ofstream	&outputfile)
{
	if (node == NULL)
		return ;
	outputfile << node->data << ' ';
	preorder_traversal(node->leftchild, outputfile);
	preorder_traversal(node->rightchild, outputfile);
}

void	binary_tree::inorder_traversal(TreeNode *node, ofstream	&outputfile)
{
	if (node == NULL)
		return ;
	inorder_traversal(node->leftchild, outputfile);
	outputfile << node->data << ' ';
	inorder_traversal(node->rightchild, outputfile);
}

void	binary_tree::postorder_traversal(TreeNode *node, ofstream &outputfile)
{
	if (node == NULL)
		return ;
	postorder_traversal(node->leftchild, outputfile);
	postorder_traversal(node->rightchild, outputfile);
	outputfile << node->data << ' ';
}