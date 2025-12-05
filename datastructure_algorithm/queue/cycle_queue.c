typedef struct
{
	int *queue;
	int rear;
	int front;
} cycle_queue;

void cycle_enqueue(cycle_queue *q, int item, int size)
{
	q->rear = (q->rear + 1) % size;
	if (q->front == q->rear)
	{
		printf("overflow");
		exit(0);
	}
	q->queue[(q->rear)] = item;
}

int cycle_dequeue(cycle_queue *q, int size)
{
	if (q->rear == q->front)
	{
		printf("underflow");
		exit(0);
	}
	q->front = (q->front + 1) % size;
	return (q->queue[q->front]);
}

void print_queue(cycle_queue *q)
{
	for (int i = q->front + 1; i <= q->rear; i++)
		printf("%d ", q->queue[i]);
	printf("\n");
}

void process(cycle_queue* q, int size)
{
	int n;
	char oper[10];
	
	scanf("%d", &n);
	for (int i = 0; i < n; i++)
	{
		getchar();
		scanf("%[^\n]s", oper);
		switch (oper[0])
		{
		case 'D':
			cycle_dequeue(q, size);
			break;
		case 'I':
			cycle_enqueue(q, atoi(oper + 2), size);
			break;
		case 'P':
			print_queue(q);
			break;
		}
	}
}

int main(void)
{
	int size;
	cycle_queue *q;

	scanf("%d", &size);
	q->queue = (int*)malloc(sizeof(int) * size);
	q->rear = 0;
	q->front = 0;
	process(q, size);
}