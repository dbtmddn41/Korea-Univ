#include "prefix.hpp"

bool	stack::is_empty()
{
	return (top == -1);
}

bool	stack::is_full()
{
	return (top == MAX_STACK_SIZE - 1);
}

void	stack::push(element data)
{
	if (!is_full())
		stackarr[++top] = data;
}

element	stack::pop()
{
	if (!is_empty())
		return (stackarr[top--]);
	else
	{
		std::cout << "스택 공백 에러" << std::endl;
		exit(-1);
	}
}

element	stack::peek()
{
	return (stackarr[top]);
}

element	stack::get_top()
{
	return (top);
}