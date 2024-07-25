#include "bank.hpp"

void	Account::make_account(int id)
{
	Account	*new_account = new Account(id);
	Account	*curr;

	curr = this;
	while (curr->link != NULL)
		curr = curr->link;
	curr->link = new_account;
}

void	Account::delete_account(int id)
{
	Account	*temp;
	Account	*curr;

	curr = this;
	if (curr->link == NULL)
	{
		if (curr->id == id)
			delete curr;
		return ;
	}
	while (curr->link->id == id)
		curr = curr->link;
	temp = curr->link->link;
	delete curr->link;
	curr->link = temp;
}

Account*	Account::find_account(int id)
{
	Account	*curr;

	curr = this;
	while(curr != NULL)
	{
		if (curr->id == id)
			break;
		curr = curr->link;
	}
	return (curr);
}

void	Account::transfer(Account *to, int amount)
{
	if (amount > balance)
	{
		cout << "잔액 부족!! 열심히 일합시다" << endl;
		return ;
	}
	balance -= amount;
	to->balance += amount;
}

void	Account::print_balance(void)
{
	cout << "이 계좌의 잔액: " << balance << endl;
}

void	Account::print_balance_all(void)
{
	Account			*curr;
	unsigned int	sum = 0;
	
	curr = this;
	while (curr != NULL)
	{
		if (curr->enabled)
			sum += curr->balance;
		curr = curr->link;
	}
	cout << "전체 계좌의 총액: " << sum << endl;
}

bool	Account::is_enabled(void)
{
	return enabled;
}

void	Account::suspend_account(void)
{
	if (enabled)
	{
		enabled = false;
		cout << "success" << endl;
	}
	else
	{
		cout << "이미 정지된 계정입니다." << endl;
	}
}

void	Account::active_account(void)
{
	if (enabled == false)
	{
		enabled = true;
		cout << "success" << endl;
	}
	else
	{
		cout << "이미 활성화된 계정입니다." << endl;
	}
}
