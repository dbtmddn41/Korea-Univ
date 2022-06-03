#include <fstream>
#include <iostream>
#include <string>

int	sum(int n)
{
	if (n == 1)
		return (1);
	if (n == 0)
		return (0);

	if (n % 2)
	{
		n /= 2;
		return (4 * sum(n) + n + 1);
	}
	else
	{
		n /= 2;
		return (2 * sum(n) + n * n);
	}
}

int	main(void)
{
	std::ifstream	input;
	std::ofstream	output("sum.out");
	std::string		line;
	int				res;

	input.open("sum.in");
	if (input.fail())
	{
		std::cout << "no input file" << std::endl;
		throw;
	}
	while (!input.eof())
	{
		getline(input, line);
		res = sum(atoi(line.c_str()));
		output << res;
		if (!input.eof())
			output << std::endl;
	}
	input.close();
	output.close();
}