#include<stdio.h>

#define	N 100
#define B 20

double x[N][N], y[N][N], z[N][N];

void main(void)
{
	double r;
	int i, j, k, jj, kk;
	int j_limit, k_limit;

	for(i=0; i<N; i++)
		for(j=0; j<N; j++) {
			y[i][j] = 0.1*i+0.002*j;
			z[i][j] = 0.2*i+0.001*j;
		}

	for(i=0; i<N; i++)
		for(j=0; j<N; j++) {
            x[i][j] = 0.0;
		}

    for (kk = 0; kk < N; kk += B) {
        k_limit = (kk + B < N) ? (kk + B) : N;
        for (jj = 0; jj < N; jj += B) {
            j_limit = (jj + B < N) ? (jj + B) : N;
            for (i = 0; i < N; i++) {
                for (j = jj; j < j_limit; j++) {
                    r = x[i][j];
                    for (k = kk; k < k_limit; k++) {
                        r = r + y[i][k] * z[k][j];
                    }
                    x[i][j] = r;
                }
            }
        }
    }
	/*
	for(i=0; i<N; i++)
		for(j=0; j<N; j++)
			printf("x[%d][%d]=%f\n", i, j, x[i][j]);
	*/
}
