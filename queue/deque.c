#include <stdio.h>

typedef struct DlistNode
{
	int data;
	struct DlistNode *rlink;
	struct DlistNode *llink;
}DlistNode;

typedef struct DequeType
{
	DlistNode *head;
	DlistNode *rear;
}DequeType;

DlistNode *create_node(DlistNode *rlink, int item, DlistNode *llink)
{
	DlistNode *new_node;

	new_node = malloc(sizeof(DlistNode));
	if (!new_node)
		return (0);
	new_node->rlink = rlink;
	new_node->llink = llink;
	new_node->data = item;
	return (new_node);
}

void add_front(DequeType *dq, int item)
{
	DlistNode *new;

	new = create_node(NULL, item, dq->head);
	if (!new)
		exit(0);
	if (dq->head != NULL)
		dq->head->llink = new;
	else
		dq->rear = new;
	dq->head = new;
}

void add_back(DequeType *dq, int item)
{
	DlistNode *new;

	new = create_node(dq->rear, item, NULL);
	if (!new)
		exit(0);
	if (dq->rear != NULL)
		dq->rear->rlink = new;
	else
		dq->head = new;
	dq->rear = new;
}

int delete_front(DequeType *dq)
{
	int temp_data;
	DlistNode* temp_node;

	if (dq->head == NULL)
	{
		printf("underflow");
		exit(0);
	}
	temp_data = dq->head->data;
	temp_node = dq->head->rlink;
	dq->head->rlink = NULL;
	free(dq->head);
	dq->head = temp_node;
	if (dq->head == NULL)
		dq->rear = NULL;
	else
		dq->head->llink = NULL;
	return (temp_data);
}

int delete_back(DequeType *dq)
{
	int temp_data;
	DlistNode* temp_node;

	if (dq->rear == NULL)
	{
		printf("underflow");
		exit(0);
	}
	temp_data = dq->rear->data;
	temp_node = dq->rear->llink;
	dq->rear->llink = NULL;
	free(dq->rear);
	dq->rear = temp_node;
	if (dq->rear == NULL)
		dq->head = NULL;
	else
		dq->head->rlink = NULL;
	return (temp_data);
}

int get_front(DequeType *dq)
{
	return (dq->head->data);
}

int get_back(DequeType *dq)
{
	return (dq->rear->data);
}