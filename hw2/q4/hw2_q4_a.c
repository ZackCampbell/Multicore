#include <stdio.h>
#include <omp.h>
#include <stdlib.h>
#include <string.h>

#include <unistd.h>
#include <time.h>

void printMatrix(int rs, int cs, double** matrix) {
    printf("%d %d \n", rs, cs);
    for(int i = 0; i < rs; i++) {
	for(int j = 0; j < cs; j++) {
	    printf("%lf  ", matrix[i][j]);
            if(j == cs - 1) printf("\n");
        }
    }
}

void MatrixMult(char file1[], char file2[], int T) {
    FILE* inputFile1 = fopen(file1, "r");
    FILE* inputFile2 = fopen(file2, "r");
    if (inputFile1 == NULL || inputFile2 == NULL) {
        printf("No such file");
        return;
    }
    int row1, col1, row2, col2;
    fscanf(inputFile1, "%d %d", &row1, &col1);
    fscanf(inputFile2, "%d %d", &row2, &col2);
    double** matrix1 = malloc(row1 * sizeof(double*));
    double** matrix2 = malloc(row2 * sizeof(double*));
    for (int i = 0; i < row1; i++) {
	matrix1[i] = malloc(col1 * sizeof(double));
        for (int j = 0; j < col1; j++) {
            fscanf(inputFile1, "%lf", &matrix1[i][j]);
        }
    }
    for(int i = 0; i < row2; i++) {
	matrix2[i] = malloc(col2 * sizeof(double));
        for(int j = 0; j < col2; j++) {
	    fscanf(inputFile2, "%lf", &matrix2[i][j]);
	}
    }
    fclose(inputFile2);
    fclose(inputFile1);
    int row3 = row1;
    int col3 = col2;
    double** result = malloc(row3 * sizeof(double*));
    for(int i = 0; i < row3; i++) {
	result[i] = malloc(col3 * sizeof(double));
	for(int j = 0; j < col3; j++){
	    result[i][j] = 0;
 	}
    }
    int i, j, k, res;
    #pragma omp parallel for schedule(dynamic) private(res, i, j, k) num_threads(T) 
	for(i = 0; i < row3; i++) {
	    for(j = 0; j < col3; j++) {
		res = 0;
	        for(k = 0; k < col1; k++) {
		    res += (matrix1[i][k] * matrix2[k][j]);
	        }
		result[i][j] = res;
	    }
        }
    printMatrix(row3, col3, result);
    for(int i = 0; i < row1; i++) {
        free(matrix1[i]);
    }
    for(int i = 0; i < row2; i++) {
	free(matrix2[i]);
    }
    for(int i = 0; i < row3; i++) {
	free(result[i]);
    }
    free(matrix1);
    free(matrix2);
    free(result);
}

void main(int argc, char *argv[]) {
    char *file1, *file2;
    file1=argv[1];
    file2=argv[2];
    int T=atoi(argv[3]);
    MatrixMult(file1,file2,T);
}

