#include "bank.hpp"

Person*	Person::find_person(char *name)
{
	Person	*curr;

	curr = this;
	while (curr != NULL)
	{
		if (!strcmp(name, curr->name))
			break ;
		curr = curr->link;
	}
	return (curr);
}

void	Person::make_account(void)
{
	char	name[20];
	Person	*curr;
	int		id;

	cout << "이름: ";
	cin >> name;
	cout << "계좌번호: ";
	cin >> id;
	if (find_account(id))
	{
		cout << "이미 있는 계좌번호입니다." << endl;
		return ;
	}
	curr = find_person(name);
	if (curr == NULL)
	{
		curr = this;
		while (curr->link != NULL)
			curr = curr->link;
		curr->link = new Person(name, id);
		curr = curr->link;
		cout << "그런 이름은 없어서 새로 계정을 만들었습니다." << endl;
	}
	else
	{
		curr->head_account->make_account(id);
		cout << "success" << endl;
	}
}

void	Person::delete_account(void)
{
	char	name[20];
	Person	*curr;
	int		id;

	cout << "[계좌 삭제]" << endl;
	cout << "이름: ";
	cin >> name;
	cout << "계좌번호: ";
	cin >> id;
	curr = find_person(name);
	if (curr == NULL)
	{
		cout << "그런 사람 또는 계좌번호는 없는데요" << endl;
		return ;
	}
	curr->head_account->delete_account(id);
	cout << "success" << endl;
}

void	Person::transfer(void)
{
	int		amount;
	int		from_id;
	int		to_id;
	Account	*from_account;
	Account	*to_account;
	
	
	cout << "출금 계좌: ";
	cin >> from_id;
	from_account = find_account(from_id);
	cout << "입금 계좌: ";
	cin >> to_id;
	to_account = find_account(to_id);
	if (!from_account || !to_account)
	{
		cout << "그런 사람 또는 계좌번호는 없는데요" << endl;
		return ;
	}
	else if (from_account->is_enabled() == false || to_account->is_enabled() == false)
	{
		cout << "정지된 계정입니다." << endl;
		return ;
	}
	cout << "보낼 금액: ";
	cin >> amount;
	from_account->transfer(to_account, amount);
	cout << "success" << endl;
}

Account* Person::find_account(int id)
{
	Person	*curr;
	Account	*acc;

	curr = this;
	while (curr != NULL)
	{
		acc = curr->head_account->find_account(id);
		if (acc)
			break ;
		curr = curr->link;
	}
	return (acc);
}

void	Person::print_balance(void)
{
	int		id;
	Account	*acc;

	cout << "계좌 번호: ";
	cin >> id;
	acc = find_account(id);
	if (!acc)
	{
		cout << "그런 사람 또는 계좌번호는 없는데요" << endl;
		return ;
	}
	else if (acc->is_enabled() == false)
	{
		cout << "정지된 계정입니다." << endl;
		return ;		
	}
	acc->print_balance();
	cout << "success" << endl;
}

void	Person::print_balance_all(void)
{
	char	name[20];
	Person	*person;

	cout << "이름: ";
	cin >> name;
	person = find_person(name);
	if (!person)
	{
		cout << "그런 사람 또는 계좌번호는 없는데요" << endl;
		return ;
	}
	person->head_account->print_balance_all();
	cout << "success" << endl;
}

void	Person::suspend_account()
{
	int		id;
	Account	*acc;

	cout << "정지시킬 계정: ";
	cin >> id;
	acc = find_account(id);
	if (!acc)
	{
		cout << "그런 사람 또는 계좌번호는 없는데요" << endl;
		return ;
	}
	acc->suspend_account();
}

void	Person::active_account()
{
	int		id;
	Account	*acc;

	cout << "활성화시킬 계정: ";
	cin >> id;
	acc = find_account(id);
	if (!acc)
	{
		cout << "그런 사람 또는 계좌번호는 없는데요" << endl;
		return ;
	}
	acc->active_account();
}
