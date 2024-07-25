#include "AVL.hpp"

int main()
{
	AVL_tree	t;
	char		operate;
	char		key[11];

	std::ifstream	inputfile;
	std::ofstream	outputfile("AVL.out");

	inputfile.open("AVL.in");
	if (inputfile.fail())
	{
		cout << "no input file" << endl;
		return (0);
	}
	while (!inputfile.eof())
	{
		inputfile >> operate;
		if (operate == 'I')
		{
			inputfile >> key;
			t.AVL_insert(atoi(key));
		}
		outputfile << "I ";
		t.inorder_traversal(t.get_root(), outputfile);
		outputfile << endl << "P ";
		t.preorder_traversal(t.get_root(), outputfile);
		if (!inputfile.eof())
			outputfile << endl << endl;
	}
	inputfile.close();
	outputfile.close();
}