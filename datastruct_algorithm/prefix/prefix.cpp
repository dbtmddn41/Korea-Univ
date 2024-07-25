#include "prefix.hpp"

bool	is_oper(char c)
{
	if (c == '+' || c == '-' || c == '*' || c == '/')
		return (true);
	else
		return (false);
}

int	prefixHandler::calculate(const char *express)
{
	int				i = 0;
	char			c;
	unsigned int	ex_len;
	int				temp;

	ex_len = strlen(express);
	while (i < ex_len)
	{
		c = express[i];
		if (is_oper(c))
		{
			mem.push(c * (-1));
			i++;
		}
		else if (isdigit(c))
		{
			mem.push(atoi(express + i));
			while (isdigit(express[i]))
				i++;
			while (mem.get_top() > 1)
			{
				temp = mem.pop();
				if (mem.peek() >= 0)
				{
					mem.push(temp);
					operate();
				}
				else
				{
					mem.push(temp);
					break;
				}
			}
		}
		i++;
	}
	temp = mem.pop();
	if (!mem.is_empty())
		throw;
	return (temp);
}

int	prefixHandler::operate()
{
	int		num1;
	int		num2;
	char	oper;
	int		res;

	num1 = mem.pop();
	num2 = mem.pop();
	oper = (char)(mem.pop() * (-1));
	switch (oper)
	{
		case '+':
			res = num2 + num1;
			break;
		case '-':
			res = num2 - num1;
			break;
		case '*':
			res = num2 * num1;
			break;
		case '/':
			res = num2 / num1;
			break;
	}
	mem.push(res);
	return (res);
}