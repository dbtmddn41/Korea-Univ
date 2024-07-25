#include <iostream>
#include <string>
#include <algorithm>
#include <climits>

using std::max;
using std::min;
using std::string;

class	histogram
{
private:
	unsigned int	*histo;
public:
	histogram(int size);
	~histogram()	{delete histo;};
	void			set_histogram(unsigned int size, const char *distribute);
	unsigned int	biggest_square(int start, int end);
	unsigned int	mid_area(int start, int end);
	void			push(unsigned int height, int i);
};

histogram::histogram(int size)
{
	histo = new unsigned int[size];
}

unsigned int	histogram::biggest_square(int start, int end)
{
	if (start == end)
		return (histo[start]);

	int	mid = (start + end) / 2;
	return (max({mid_area(start, end), biggest_square(start, mid), biggest_square(mid + 1, end)}));
}

unsigned int	histogram::mid_area(int start, int end)
{
	int				left, right;
	unsigned int	max_area = 0;
	unsigned int	height, length;

	left = (start + end) / 2;
	right = left + 1;
	length = 2;
	height = UINT_MAX;
	while (left != start || right != end)
	{
		height = min({height, histo[left], histo[right]});
		max_area = max({max_area, length * height});
		if (left == start)
			right++;
		else if (right == end)
			left--;
		else
		{
			if (histo[left - 1] >= histo[right + 1])
				left--;
			else
				right++;
		}
		length++;
	}
	height = min({height, histo[left], histo[right]});
	max_area = max({max_area, length * height});
	return (max_area);
}

void	histogram::push(unsigned int height, int i)
{
	histo[i] = height;
}

int main(void)
{
	int size;
	unsigned int height;

	scanf("%d", &size);
	if (size <= 0)
		return (0);
	histogram	h(size);
	for (int i = 0; i < size; i++)
	{
		scanf("%u", &height);
		h.push(height, i);
	}
	std::cout << h.biggest_square(0, size - 1);
}