#ifndef PARSE_H_
#define PARSE_H_

/*
 * Command to compile: 
 *
 * nvcc -arch=compute_35 -code=sm_35 -o myfile.out myfile.cu parse.cu
 *
 * Import like:
 *
 * #import "parse.h"
 *
 * Call like:
 *
 * int arrlen;
 * int* arr = getarr(&arrlen);
 *
 * arr will be the pointer to the array of length arrlen
 *
 * arr needs to be freed with free(arr) before the program exits
 *
 * parse.c assumes inp.txt is in the same directory
 */

int* getarr(int* arrlen);

#endif
