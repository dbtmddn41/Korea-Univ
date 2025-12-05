#define MAX_TREE_SIZE 100

typedef int	element;

class binary_tree
{
private:
	element	tree[MAX_TREE_SIZE];
public:
	void	insert_left_child(int n, element data);
	void	insert_right_child(int n, element data);
	element	get_left_child(int n);
	element	get_right_child(int n);
};

void	binary_tree::insert_left_child(int n, element data)
{
	tree[n * 2] = data;
}

void	binary_tree::insert_right_child(int n, element data)
{
	tree[n * 2 + 1] = data;
}

element	binary_tree::get_left_child(int n)
{
	return (tree[n * 2]);
}

element	binary_tree::get_right_child(int n)
{
	return (tree[n * 2 + 1]);
}