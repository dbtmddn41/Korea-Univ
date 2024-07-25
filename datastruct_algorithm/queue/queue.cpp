#define MAX_QUEUE_SIZE 8

typedef int element;

class queue
{
private:
	element queueArray[MAX_QUEUE_SIZE];
	int 	rear;
	int 	front;
public:
	queue()	:rear(-1), front(-1)	{};
	bool	is_full();
	bool	is_empty();
	void	queue_full();
	void	enqueue(element data);
	element	dequeue();
};

bool	queue::is_full()
{
	return (rear == MAX_QUEUE_SIZE - 1);
}

bool	queue::is_empty()
{
	return (rear == front);
}

void	queue::enqueue(element data)
{
	if (is_full())
	{
		if (front == -1)
			return ;
		queue_full();
	}
	queueArray[++rear] = data;
}

element	queue::dequeue()
{
	if (is_empty())
	{
		cout << "empty" << endl;
		return ;
	}
	return (queueArray[front--]);
}

void	queue::queue_full()
{
	for (int i = 0; i <= rear - front; i++)
		queueArray[i] = queueArray[i + front + 1];
	rear -= front + 1;
	front = -1;
}

class cycle_queue
{
private:
	int queueArray[MAX_QUEUE_SIZE];
	int rear;
	int front;
public:
	cycle_queue()	:rear(0), front(0)	{};
	bool	is_full();
	bool	is_empty();
	void	enqueue(element data);
	element	dequeue();
};

void	cycle_queue::enqueue(element data)
{
	if (front % MAX_QUEUE_SIZE == (rear + 1) % MAX_QUEUE_SIZE)
	{
		cout << "over flow" << endl;
		return ;
	}
	rear = (rear + 1) % MAX_QUEUE_SIZE;
	queueArray[++rear] = data;
}

element	cycle_queue::dequeue()
{
	if (front == rear)
	{
		cout << "under flow!" << endl;
		return ;
	}
	front = (front + 1) % MAX_QUEUE_SIZE;
	return (queueArray[front])
}