#include <stdio.h>
#include <omp.h>
#include <stdlib.h>
#include <string.h>

void printMatrix(double matrix[], int matrixLength) {
    for (int i = 0; i < matrixLength; i++) {
        printf("%f ", matrix[i]);
    }
}

void MatrixMult(char file1[],char file2[],int T) {
    FILE *inputFile1 = fopen(file1, "r");
    FILE *inputFile2 = fopen(file2, "r");
    int width1, height1, width2, height2;
    if (inputFile1 == NULL || inputFile2 == NULL) {
        printf("No such file");
        return;
    }
    fscanf(inputFile1, "%d %d", &width1, &height1);
    fscanf(inputFile2, "%d %d", &width2, &height2);
    double matrix[width1][height2];

    double** matrix2 = malloc(width2 * sizeof(double*));
    for (int i = 0; i < height2; ++i)
        matrix2[i] = malloc(sizeof(double));

    for (int i = 0; i < height2; i++) {
        for (int j = 0; j < width2; j++) {
            fscanf(inputFile2, "%lf", &matrix2[i][j]);
        }
    }

    for (int i = 0; i < height2; i++) {
        for (int j = 0; j < width1; j++) {
            double fromFile1, fromFile2 = matrix2[j][i];
            fscanf(inputFile1, "%lf", &fromFile1);
            matrix[i][j] = fromFile1 * fromFile2;
        }
        printMatrix(matrix[i], width1);
        printf("\n");
    }

    for (int i = 0; i < height2; i++) {
        free(matrix2[i]);
    }
    free(matrix2);

    fclose(inputFile2);
    fclose(inputFile1);
    //Write your code here
}



void main(int argc, char *argv[]) {
    char *file1, *file2;
    file1=argv[1];
    file2=argv[2];
    int T=atoi(argv[3]);
    MatrixMult(file1,file2,T);
}


