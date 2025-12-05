#include <iostream>

typedef int element;

typedef struct Node
{
	element		data;
	struct Node	*link;
}Node;

class	queue
{
private:
	Node	*head;
	Node	*rear;
public:
	queue()	:rear(NULL)	{};
	void	enqueue(element data);
	element	dequeue();
};

void	queue::enqueue(element data)
{
	Node	*new_node;

	new_node = new Node;
	new_node->data = data;
	new_node->link = NULL;
	if (!rear)
		rear->link = new_node;
	else
		head = new_node;
	rear = new_node;
}

element	queue::dequeue()
{
	element	temp;
	Node	*temp_node;

	if (!head)
	{
		cout << "empty queue" << endl;
		exit(0);
	}
	temp = head->data;
	temp_node = head->link;
	delete	head;
	head = temp_node;
	return (temp);
}
