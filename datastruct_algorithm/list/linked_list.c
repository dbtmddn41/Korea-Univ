#include <stdlib.h>
#include <stdio.h>
typedef int	element;

typedef struct ListNode
{
	element data;
	struct ListNode* link;
}ListNode;

typedef struct DlistNode
{
	element data;
	struct DlistNode* llink;
	struct DlistNode* rlink;
}DlistNode;

void dinsert_node(DlistNode* before, DlistNode* new_node)
{
	new_node->rlink = before->rlink;
	new_node->rlink->llink = new_node;
	new_node->llink = before;
	before->rlink = new_node;
}

void dremove_node(DlistNode** phead_node, DlistNode* removed)
{
	if (phead_node == removed)
	{
		*phead_node = removed->rlink;
		(*phead_node)->llink = NULL;
	}
	else
	{
		removed->llink->rlink = removed->rlink;
		removed->rlink->llink = removed->llink;
	}
	free(removed);
}

void insert_node(ListNode** phead, ListNode* p, ListNode* new_node)
{

	if (!*phead)
	{
		*phead = new_node;
		new_node->link = NULL;
	}
	else if (!p)
	{
		new_node->link = *phead;
		*phead = new_node;
	}
	else
	{
		new_node->link = p->link;
		p->link = new_node;
	}
}

void remove_node(ListNode** phead, ListNode* p, ListNode* remove)
{
	if (!p)
		*phead = (*phead)->link;
	else
	{
		if (p->link != remove)
			return;
		p->link = remove->link;
	}
	free(remove);
}

ListNode* create_node(element data, ListNode* Link)
{
	ListNode* new_node;

	new_node = (ListNode*)malloc(sizeof(ListNode));
	if (!new_node)
		return (0);
	new_node->data = data;
	new_node->link = Link;
	return (new_node);
}

void display(ListNode* lst)
{
	for (int i = 0;; i++)
	{
		if (!lst)
			break;
		printf("%d번째 data는 %d입니다.\n", i, lst->data);
		lst = lst->link;
	}
}