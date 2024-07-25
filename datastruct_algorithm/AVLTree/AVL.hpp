#ifndef AVL_HPP
#define AVL_HPP

#include <iostream>
#include <fstream>
using namespace std;

typedef int element;

typedef struct TreeNode
{
	element	data;
	struct TreeNode *parent;
	struct TreeNode *leftchild;
	struct TreeNode	*rightchild;
}TreeNode;

class binary_tree
{
private:
	TreeNode	*root;
public:
	binary_tree()	:root(NULL)	{};
	binary_tree(element data);
	~binary_tree();
	TreeNode	*create_node(element data);
	void		delete_tree(TreeNode *node);
	TreeNode	*insert_left_child(TreeNode *parent_node, element data);
	TreeNode	*insert_right_child(TreeNode *parent_node, element data);
	element		get_left_child(TreeNode *node)	const;
	element		get_right_child(TreeNode *node)	const;
	TreeNode	*get_root()	const;
	TreeNode	*set_root(TreeNode *node);
	void		preorder_traversal(TreeNode *node, ofstream &outputfile);
	void		inorder_traversal(TreeNode *node, ofstream &outputfile);
	void		postorder_traversal(TreeNode *node, ofstream &outputfile);
};

class binary_search_tree	:public binary_tree
{
public:
	binary_search_tree():	binary_tree()	{};
	binary_search_tree(element data):	binary_tree(data)	{};
	~binary_search_tree()	{};
	TreeNode	*insert(element data);
	TreeNode	*search_recursive(TreeNode *parent, int data);
};

class	AVL_tree	:public binary_search_tree
{
public:
	AVL_tree():	binary_search_tree()	{};
	AVL_tree(element data):	binary_search_tree(data)	{};
	~AVL_tree()	{};
	void		AVL_insert(element data);
	int			compare_subtrees_height(TreeNode *parent);
	TreeNode	*search_unbalance_tree(TreeNode *node);
	void		resolve_unbalance(TreeNode *unbalance_node);
	char		check_unbalance_form(TreeNode *unbalnce_node);
	int			get_height(TreeNode	*node);
	void		RR(TreeNode *node);
	void		LL(TreeNode *node);
	void		LR(TreeNode *node);
	void		RL(TreeNode *node);
};

enum left_right
{
	LL,
	LR,
	RL,
	RR
};

#endif