#include <stdio.h>
#include <stdlib.h>

#define MAX_QUEUE_SIZE 8

typedef struct
{
	int queue[MAX_QUEUE_SIZE];
	int rear;
	int front;
} linear_queue;

void linear_queue_full(linear_queue *q)
{
	for (int i = 0; i <= q->rear; i++)
		q->queue[i] = q->queue[q->front + 1 + i];
	q->rear -= q->front + 1;
	q->front = -1;
}

void linear_enqueue(linear_queue *q, int item)
{
	if (q->rear == MAX_QUEUE_SIZE - 1)
	{
		if (q->front == -1)
			return
		linear_queue_full(q);
	}
	else
		q->queue[++(q->rear)] = item;
}

int linear_dequeue(linear_queue *q)
{
	if (q->front == q->rear)
		exit(0);
	return (q->queue[++(q->front)]);
}

