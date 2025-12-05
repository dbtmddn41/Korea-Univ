#include "prefix.hpp"

int	main(void)
{
	prefixHandler	p;
	std::ifstream	input;
	std::ofstream	output("prefix.out");
	std::string		line;
	int				res;

	input.open("prefix.in");
	if (input.fail())
	{
		std::cout << "no input file" << std::endl;
		throw;
	}
	while (!input.eof())
	{
		getline(input, line);
		res = p.calculate(line.c_str());
		output << res << std::endl;
	}
	input.close();
	output.close();
}