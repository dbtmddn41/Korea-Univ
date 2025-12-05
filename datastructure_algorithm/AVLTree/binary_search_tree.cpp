#include "AVL.hpp"

TreeNode	*binary_search_tree::insert(element data)
{
	TreeNode	*root_node;
	TreeNode	*parent_node;
	TreeNode	*new_node;
	
	root_node = binary_tree::get_root();
	if (!root_node)
	{
		new_node = create_node(data);
		return (binary_tree::set_root(new_node));
	}
	parent_node = search_recursive(root_node, data);
	if (data >= parent_node->data)
		new_node = binary_tree::insert_right_child(parent_node, data);
	else
		new_node = binary_tree::insert_left_child(parent_node, data);
	return (new_node);
}


TreeNode	*binary_search_tree::search_recursive(TreeNode *parent, int data)
{
	if (data > parent->data)
	{
		if (parent->rightchild == NULL)
			return (parent);
		else
			return search_recursive(parent->rightchild, data);
	}
	else if (data < parent->data)
	{
		if (parent->leftchild == NULL)
			return (parent);
		else
			return search_recursive(parent->leftchild, data);
	}
	else
		return (parent);	
}