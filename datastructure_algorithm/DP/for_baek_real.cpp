#include <iostream>

void concat(int *num_list, int size)
{
	int	*max_list = new int[size];
	int	max;
	int	start = 0, max_end = 0, max_start = 0;

	max_list[0] = num_list[0];
	max = num_list[0];
	for (int i = 1; i < size; i++)
	{
		if (max_list[i - 1] > 0)
			max_list[i] = max_list[i - 1] + num_list[i];
		else
		{
			max_list[i] = num_list[i];
			start = i;
		}
		if (max < max_list[i])
		{
			max = max_list[i];
			max_end = i;
			max_start = start;
		}
	}
	std::cout << max_start << ' ' << max_end << std::endl;
	for (int i = max_start; i <= max_end; i++)
	{
		std::cout << num_list[i] << ' ';
	}
	return ;
};

int main(void)
{
	int size;
	int *num_list;

	scanf("%d", &size);
	num_list = new int[size];
	for (int i = 0; i < size; i++)
		scanf("%d", num_list + i);
	
	concat(num_list, size);
	return (0);
}