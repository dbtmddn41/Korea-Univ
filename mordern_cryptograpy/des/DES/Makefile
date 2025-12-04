all:
	gcc -O3 -s -w main.c des.c -I check_speed.h des.h -o DES

uselib:
	gcc -O3 -s -w main.c des2.c -I check_speed.h des.h -o DES -lssl -lcrypto

clean:
	rm -rf DES