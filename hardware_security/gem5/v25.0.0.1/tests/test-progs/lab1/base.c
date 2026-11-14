#include<stdio.h>

#define	N 100

double x[N][N], y[N][N], z[N][N];

void main(void)
{
	double r;
	int i, j, k, jj, kk;

	for(i=0; i<N; i++)
		for(j=0; j<N; j++) {
			y[i][j] = 0.1*i+0.002*j;
			z[i][j] = 0.2*i+0.001*j;
		}

	for(i=0; i<N; i++)
		for(j=0; j<N; j++) {
			r = 0.0;
			for(k=0; k<N; k++)
				r=r+y[i][k]*z[k][j];
			x[i][j]=r;
		}

	/*
	for(i=0; i<N; i++)
		for(j=0; j<N; j++)
			printf("x[%d][%d]=%f\n", i, j, x[i][j]);
	*/
}
