#define MAX_LIST_SIZE 100
typedef int element;

class ArrayList
{
private:
	element list[MAX_LIST_SIZE];
	int		length;
public:
	ArrayList();
	int		is_empty();
	int		is_full();
	void	add(int position, element item);
	element	deleteF(int position);
};

ArrayList::ArrayList()	:length(0){}

int	ArrayList::is_empty()
{
	return (length == 0);
}

int ArrayList::is_full()
{
	return (length == MAX_LIST_SIZE);
}

void	ArrayList::add(int position, element item)
{
	if (is_full() || position < 0 || position > length)
		return ;
	for (int i = length - 1; i >= position; i--)
		list[i + 1] = list[i];
	list[position] = item;
	length++;
}

element	ArrayList::deleteF(int position)
{
	int temp;

	if (is_empty() || position < 0 || position > length)
		return ;
	temp = list[position];
	for (int i = position; i < length - 1; i++)
		list[i] = list[i + 1];
	length--;
	return (temp);
}