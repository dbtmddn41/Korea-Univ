#include <iostream>
using namespace std;

typedef int element;

typedef struct TreeNode
{
	element	data;
	struct TreeNode *left;
	struct TreeNode	*right;
}TreeNode;

class binary_tree
{
private:
	TreeNode	*root;
public:
	binary_tree()	:root(NULL)	{};
	binary_tree(element data);
	~binary_tree();
	void	delete_tree(TreeNode *node);
	void	insert_left_child(TreeNode *node, element data);
	void	insert_right_child(TreeNode *node, element data);
	element	get_left_child(TreeNode *node);
	element	get_right_child(TreeNode *node);
	TreeNode	*get_root();
	void	preorder_traversal(TreeNode *node);
	void	inorder_traversal(TreeNode *node);
	void	postorder_traversal(TreeNode *node);
	void	level_traversal();
};

binary_tree::binary_tree(element data)
{
	root = new TreeNode;
	root->data = data;
	root->right = NULL;
	root->left = NULL;
}

binary_tree::~binary_tree()
{
	delete_tree(root);
}

void	binary_tree::delete_tree(TreeNode *node)
{
	if (node == NULL)
		return ;
	delete_tree(node->right);
	delete_tree(node->left);
	delete node;
}

void	binary_tree::insert_left_child(TreeNode *node, element data)
{
	if (!node)
		return ;

	TreeNode	*new_node;
	
	new_node = new TreeNode;
	new_node->data = data;
	new_node->right = NULL;
	new_node->left = NULL;
	node->left = new_node;
}

void	binary_tree::insert_right_child(TreeNode *node, element data)
{
	if (!node)
		return ;

	TreeNode	*new_node;
	
	new_node = new TreeNode;
	new_node->data = data;
	new_node->right = NULL;
	new_node->left = NULL;
	node->right = new_node;
}

element	binary_tree::get_left_child(TreeNode *node)
{
	return (node->left->data);
}

element	binary_tree::get_right_child(TreeNode *node)
{
	return (node->right->data);
}

TreeNode*	binary_tree::get_root()
{
	return (root);
}

void	binary_tree::preorder_traversal(TreeNode *node)
{
	if (node == NULL)
		return ;
	cout << node->data << endl;
	preorder_traversal(node->left);
	preorder_traversal(node->right);
}

void	binary_tree::inorder_traversal(TreeNode *node)
{
	if (node == NULL)
		return ;
	preorder_traversal(node->left);
	cout << node->data << endl;
	preorder_traversal(node->right);
}

void	binary_tree::postorder_traversal(TreeNode *node)
{
	if (node == NULL)
		return ;
	preorder_traversal(node->left);
	preorder_traversal(node->right);
	cout << node->data << endl;
}

void	binary_tree::level_traversal()
{
	queue	q;
	TreeNode	*curr;

	q.enque(root)
	while (!q.is_empty())
	{
		curr = q.deque();
		cout << curr->data << endl;
		if (curr->left)
			q.enque(curr->left);
		if (curr->right)
			q.enque(curr->right);
	}
}