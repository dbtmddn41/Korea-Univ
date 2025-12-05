#include "bank.hpp"

void	menu(void)
{
	cout << "-----Menu-----" << endl;
	cout << "1. 계좌 개설\n2. 계좌 삭제\n3. 이체\n4. 계좌 잔고 출력\n5. 전체 계좌 총액 출력\n6. 계정 정지\n7. 계정 활성화\n8. 종료" << endl;
	cout << "선택: ";
}

Person*	create_one(void)
{
	char	name[20];
	int		id;
	Person	*person;

	cout << "이름: ";
	cin >> name;
	cout << "계좌번호: ";
	cin >> id;
	person = new Person(name, id);
	return (person);
}

int main(void)
{
	int 	input;
	Person	*person;

	person = create_one();
	while (true)
	{
		menu();
		cin >> input;
		switch (input)
		{
			case 1:
				person->make_account();
				break;
			case 2:
				person->delete_account();
				break;
			case 3:
				person->transfer();
				break;
			case 4:
				person->print_balance();
				break;
			case 5:
				person->print_balance_all();
				break;
			case 6:
				person->suspend_account();
				break;
			case 7:
				person->active_account();
				break;
			case 8:
				return (0);
			default:
				cout << "다시 입력하시오." << endl;
				continue;
				break;
		}
	}
}