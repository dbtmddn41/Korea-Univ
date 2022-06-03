#include <stdio.h>

typedef struct circleQueueNode
{
	int item;
	struct circleQueueNode *queueLink;
}QueueNode;

typedef struct circleQueueType
{
	QueueNode *front;
	QueueNode *rear;
}QueueType;

void dequeue(QueueType *qt)
{
	int temp_item;
	QueueNode *temp_q;

	if (qt->front == NULL)
	{
		printf("underflow");
		exit(0);
	}
	temp_item = qt->front->item;
	temp_q = qt->front;
	qt->front = qt->front->queueLink;
	free(temp_q);
	return (temp_item);
}

void enqueue(QueueType* qt, int item)
{
	QueueNode *new;

	new = malloc(sizeof(QueueNode));
	if (!new)
	{
		exit(0);
	}
	new->item = item;
	if (qt->front == NULL || qt->rear == NULL)
	{
		qt->front = new;
		qt->rear = new;
		new->queueLink = NULL;
	}
	qt->rear->queueLink = new;
	qt->rear = new;
}

void clear(QueueType *qt)
{
	QueueNode *temp;
	while (qt->front != NULL)
	{
		temp = qt->front->queueLink;
		qt->front->queueLink = NULL;
		free(qt->front);
		qt->front = temp;
	}
	qt->rear = NULL;
}