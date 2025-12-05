#include <iostream>
#include <cstring>

class matrix
{
private:
	int	col;
	int	row;
	int	**map;
public:
	matrix(int col, int row);
	~matrix();
	void	concat(int	*&num_list, int start, int end);
};

matrix::matrix(int col, int row)
{
	map = new int*[row + 1];
	for (int i = 0; i < row; i++)
	{
		map[i] = new int[col];
		bzero(map[i], col * sizeof(int));
	}
	map[row] = NULL;
	this->col = col;
	this->row = row;
}

matrix::~matrix()
{
	for (int i = 0; i < row; i++)
	{
		delete []map[i];
	}
	delete []map;
}

void	matrix::concat(int	*&num_list, int start, int end)
{
	static int	max = -2147483648;
	if (end >= row)
	{
		std::cout << max;
		return ;
	}
	
	if (start == end)
		map[start][end] = num_list[start];
	else if (map[start][end - 1] >= 0)
		map[start][end] = map[start][end - 1] + num_list[end];
	else
	{
		start = end;
		map[start][end] = num_list[end];
	}
	if (map[start][end] > max)
		max = map[start][end];
	return (concat(num_list, start, end + 1));
}

int main(void)
{
	int size;
	int *num_list;

	scanf("%d", &size);
	num_list = new int[size];
	for (int i = 0; i < size; i++)
		scanf("%d", num_list + i);

	matrix	m(size, size);
	m.concat(num_list, 0, 0);
	return (0);
}