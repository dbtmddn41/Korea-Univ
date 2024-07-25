#include <stdio.h>
#include <stdlib.h>

#define MAX_STACK_SIZE 100

typedef int element;

typedef struct
{
	element stack[MAX_STACK_SIZE];
		int top;
} StackType;

typedef struct ListNode
{
	element data;
	struct ListNode* link;
}ListNode;

void push_linked(ListNode** s, element data)
{
	ListNode* new_stack;

	new_stack = (ListNode*)malloc(sizeof(ListNode));
	if (!new_stack)
		return;
	new_stack->data = data;
	new_stack->link = *s;
	*s = new_stack;
}

element pop_linked(ListNode** s)
{
	element temp;
	ListNode* temp_s;

	temp = (*s)->data;
	temp_s = (*s)->link;
	free(*s);
	*s = temp_s;
	return (temp);
}

int is_empty(StackType* s)
{
	return (s->top == -1);
}

int is_full(StackType* s)
{
	return (s->top == MAX_STACK_SIZE - 1);
}

void push(StackType* s, element data)
{
	if (is_full(s))
		return;
	s->stack[++(s->top)] = data;
}

element pop(StackType* s)
{
	if (is_empty(s))
		return (-1);
	return (s->stack[(s->top)--]);
}

int braket_check(char* s)
{
	ListNode* pbra;
	int	i = 0;

	pbra = NULL;
	while (s[i] != '\0')
	{
		if (s[i] == '(')
			push_linked(&pbra, s[i]);
		else if (s[i] == ')')
		{
			if (pbra == NULL)
				break;
			pop_linked(&pbra);
		}
		i++;
	}
	return (pbra == NULL && s[i] == '\0');
}

char* postfix(char* s)
{

}

int main(void)
{
	char express[20];

	express[read(0, express, 19)] = 0;
	if (braket_check(express))
		return (0);
	printf("%s", postfix(express));
}