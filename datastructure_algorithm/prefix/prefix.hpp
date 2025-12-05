#include <iostream>
#include <fstream>
#include <cstring>

#define MAX_STACK_SIZE 100
typedef int element;

class	stack
{
private:
	element	stackarr[MAX_STACK_SIZE];
	int	top;
public:
	stack()	:top(-1)	{};
	bool	is_empty();
	bool	is_full();
	void	push(element data);
	element	pop();
	element	peek();
	element	get_top();
};

class prefixHandler
{
private:
	stack		mem;
public:
	int	calculate(const char *express);
	int	operate();
};