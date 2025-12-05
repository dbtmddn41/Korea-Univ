typedef int element;

typedef struct Node
{
	element		data;
	struct Node	*link;
}Node;

class LinkedList
{
private:
	Node	*head;
public:
	LinkedList()	:head(NULL)	{};
	~LinkedList();
	void	insert_node(Node *before, Node *new_node);
	void	remove_node(Node *removed);
	Node	*create_node(element data);
	int		get_length();
	void	add_last(element data);
	void	add_first(element data);
};

void	LinkedList::insert_node(Node *before, Node *new_node)
{
	Node	*temp;

	if (!before || !head)
	{
		new_node->link = head;
		head = new_node;
	}
	else
	{
	temp = before->link;
	before->link = new_node;
	new_node->link = temp;
	}
}

void	LinkedList::remove_node(Node *removed)
{
	Node	*curr;

	if (!removed || !head)
		return ;
	while (curr->link == removed)
		curr = curr->link;
	curr->link = removed->link;
	delete removed;
}

Node	*create_node(element data)
{
	Node	*new_node;

	new_node = new Node;
	new_node->data = data;
	return (new_node);
}

int	LinkedList::get_length()
{
	Node	*curr;
	int		len;

	len = 0;
	curr = head;
	while (curr != NULL)
	{
		curr = curr->link;
		len++;
	}
	return (len);
}

void	LinkedList::add_last(element data)
{
	Node	*curr;
	Node	*new_node;

	new_node = new Node;
	new_node->data = data;
	head->link = NULL;
	if (!head)
	{
		head = new_node;
		return ;
	}
	curr = head;
	while (curr->link != NULL)
		curr = curr->link;
	curr->link = new_node;
}

void	LinkedList::add_first(element data)
{
	Node *new_node;

	new_node = new Node;
	new_node->data = data;
	new_node->link = head;
	head = new_node;
}

LinkedList::~LinkedList()
{
	Node	*temp;

	while (head != NULL)
	{
		temp = head->link;
		delete head;
		head = temp;
	}
}