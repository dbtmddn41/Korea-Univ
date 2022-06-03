#include <iostream>

typedef int element;

typedef struct DNode
{
	element data;
	struct DNode* llink;
	struct DNode* rlink;
}DNode;

class DLinkedList
{
private:
	DNode	*head;
public:
	DLinkedList()	:head(NULL)	{};
	~DLinkedList();
	void	insert_node(DNode *before, DNode *new_node);
	void	delete_node(DNode *removed);
	DNode	*create_node(element data);
	int		get_length();
	void	add_last(element data);
	void	add_first(element data);
};


void	DLinkedList::insert_node(DNode *before, DNode *new_node)
{
	if (!before || !head)
	{
		new_node->llink = head;
		head = new_node;
		head->rlink = NULL;
		return ;
	}
	new_node->llink = before;
	new_node->rlink = before->rlink;
	before->rlink = new_node;
}

void	DLinkedList::delete_node(DNode *removed)
{
	DNode	*curr;

	removed->llink->rlink = removed->rlink;
	removed->rlink->llink = removed->llink;
}

DNode*	DLinkedList::create_node(element data)
{
	DNode	*new_node;

	new_node = new DNode;
	new_node->data = data;
	new_node->llink = NULL;
	new_node->rlink = NULL;
	return (new_node);
}

int	DLinkedList::get_length()
{
	DNode	*curr;
	int		len;

	len = 0;
	curr = head;
	while (curr != NULL)
	{
		curr = curr->rlink;
		len++;
	}
	return (len);
}

void	DLinkedList::add_last(element data)
{
	DNode	*curr;
	DNode	*new_node;

	new_node = new DNode;
	new_node->data = data;
	head->rlink = NULL;
	if (!head)
	{
		head = new_node;
		head->rlink = NULL;
		return ;
	}
	curr = head;
	while (curr->rlink != NULL)
		curr = curr->rlink;
	curr->llink = new_node;
	new_node->llink = curr;
}

void	DLinkedList::add_first(element data)
{
	DNode *new_node;

	new_node = new DNode;
	new_node->data = data;
	new_node->rlink = head;
	new_node->llink = NULL;
	if (head)
		head->llink = new_node;
	head = new_node;
}

DLinkedList::~DLinkedList()
{
	while (head != NULL)
	{
		head = head->rlink;
		delete (head->llink);
	}
}