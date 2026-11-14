#include <stdio.h> 
#include <string.h> 
#include <stdlib.h> 

void func(){ 
	char overflowme[1]; 
	int key = 0xdeadbeef;
	printf("overflow me : "); 
	
	gets(overflowme);
} 
int main(int argc, char* argv[]){ 
	func(); 
	return 0; 
}
