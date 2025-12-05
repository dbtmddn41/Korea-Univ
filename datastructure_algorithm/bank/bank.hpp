#include <iostream>
#include <cstring>
using	std::cin;
using	std::cout;
using	std::endl;


void	menu(void);

class Account
{
private:
	unsigned int	id;
	int				balance;
	bool			enabled;
	Account			*link;
public:
	Account(int id) :id(id), balance(100), link(NULL), enabled(true)	{};
	Account(const Account& acc) :id(acc.id), balance(acc.balance)	{};
	Account	*find_account(int id);
	void	make_account(int id);
	void	delete_account(int id);
	void	transfer(Account *to, int amount);
	void	print_balance(void);
	void	print_balance_all(void);
	bool	is_enabled(void);
	void	suspend_account(void);
	void	active_account(void);
};

class Person
{
private:
	Account*	head_account;
	char		name[20];
	Person		*link;
public:
	Person(char *name, int id)	:link(NULL)
	{
		strcpy(this->name, name);
		head_account = new Account(id);
	}
public:
	Person	*find_person(char *name);
	Account	*find_account(int id);
	void	make_account(void);
	void	delete_account(void);
	void	transfer(void);
	void	print_balance(void);
	void	print_balance_all(void);
	void	suspend_account(void);
	void	active_account(void);
};

Person*	create_one(void);