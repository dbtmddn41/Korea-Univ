#include <iostream>

typedef int element;

typedef struct DNode
{
	element			data;
	struct DNode	*rlink;
	struct DNode	*llink;
}DNode;

class deque
{
private:
	DNode	*front;
	DNode	*rear;
public:
	deque()	:front(NULL), rear(NULL)	{};
	~deque();
	void	add_front(element	data);
	void	add_back(element	data);
	element	delete_front();
	element	delete_back();
};

deque::~deque()
{
	if (!front || !rear)
		return ;
	while (front != rear)
	{
		front = front->rlink;
		delete front->llink;
	}
	delete front;
}

void	deque::add_front(element	data)
{
	DNode	*new_node;

	new_node = new DNode;
	new_node->data = data;
	new_node->llink = NULL;
	new_node->rlink = front;
	if (front && rear)
		front->llink = new_node;
	else
		rear = new_node;
	front = new_node;
}


void	deque::add_back(element	data)
{
	DNode	*new_node;

	new_node = new DNode;
	new_node->data = data;
	new_node->llink = rear;
	new_node->rlink = NULL;
	if (rear && front)
		rear->rlink = new_node;
	else
		front = new_node;
	rear = new_node;
}

element	deque::delete_back()
{
	element	temp;
	DNode	*temp_node;

	if (!front || !rear)
	{
		cout << "empty deque!" << endl;
		exit(0);
	}
	temp = rear->data;
	temp_node = rear->llink;
	delete rear;
	rear = temp_node;
	return (temp);
}

element	deque::delete_front()
{
	element	temp;
	DNode	*temp_node;

	if (!front || !rear)
	{
		cout << "empty deque!" << endl;
		exit(0);
	}
	temp = front->data;
	temp_node = front->rlink;
	delete front;
	front = temp_node;
	return (temp);
}