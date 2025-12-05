#define MAX_STACK_SIZE 100
typedef int element;

class	stack
{
private:
	element stackArr[MAX_STACK_SIZE];
	int		top;
public:
	stack()	:top(-1)	{};
	bool	is_empty();
	bool	is_full();
	void	push(element data);
	element	pop();
	element	peek();
};

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
		stackArr[++top] = data;
}

element	stack::pop()
{
	if (!is_empty())
		return (stackArr[top--]);
	else
	{
		cout << "스택 공백 에러" << endl;
		exit(-1);
	}
}

element	stack::peek()
{
	return (stackArr[top]);
}