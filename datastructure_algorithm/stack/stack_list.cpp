typedef int element;

typedef struct Node
{
	element		data;
	struct Node	*link;
}Node;

class stack
{
private:
	Node	*head;
public:
	stack()	:head(NULL) {};
	~stack();
	void	push(element data);
	element	pop();
	element	peek();
};

void	stack::push(element data)
{
	Node	*new_node;

	new_node = new Node;
	new_node->data = data;
	new_node->link = head;
	head = new_node;
}

element	stack::pop()
{
	element	temp;
	Node	*temp_node;

	if (!head)
	{
		cout << "empty stack!" << endl;
		exit(0);
	}
	temp = head->data;
	temp_node = head->link;
	delete	head;
	head = temp_node;
	return (temp);
}

element	stack::peek()
{
	return (head->data);
}

stack::~stack()
{
	Node	*temp;

	while (head != NULL)
	{
		temp = head->link;
		delete head;
		head = temp;
	}
}