#include <stdio.h>
#include <stdlib.h>
#include <cuda.h>
#include "parse.h"

__global__ markodds(int* src, int* dest) {
	if(src[blockIdx.x] % 2 == 0) dest[blockIdx.x] = 0;
	else dest[blockIdx.x] = 1;
}

__global__ prefixsum(int* src, int* dest) {
	

int main(int argc, char** argv) {
	int len;
	int* arr = getarr(&len);

	int* carr; int* cisodd; int* cpresum;
	cudaMalloc((void**) &carr, len*sizeof(int));
	cudaMalloc((void**) &cisodd, len*sizeof(int));
	cudaMalloc((void**) &cpresum, len*sizeof(int));
	cudaMemcpy((void*) carr, (void*) arr, len*sizeof(int));

	markodds<<<len, 1>>>(carr, cisodd);
	markodds<<<len, 1>>>(cisodd, cpresum);

	cudaFree(carr);
	cudaFree(cisodd);
	cudaFree(cpresum);
	free(arr);
	free(isodd);
	return 0;
}

