#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <cuda.h>
#include "cuda_runtime.h"
#include "device_launch_parameters.h"
#include "parse.h"
#include <sm_35_atomic_functions.h>

int* partA(int * A, int * B, int count);

__global__ void partAKernel(int* A, int* B, int len) {
	int index = threadIdx.x + blockIdx.x * blockDim.x;
	
	for (int i = index; i < len; i += blockDim.x * gridDim.x) {
		atomicAdd(&B[A[i]/100], 1);
	}
}


int main() {
	int count;
	int* A = getarr(&count);
	
	int* B = (int*)malloc(sizeof(int) * 10);
	for (int i = 0; i < 10; i++) {
		B[i] = 0;
	}

	int* partAAnswer = partA(A, B, count);
	
	free(B);
	free(A);
	free(partAAnswer);
	return 0;
}

int* partA(int* A, int* B, int count) {
	int* d_A;
	int* d_B;

	cudaMalloc((void**)&d_A, count * sizeof(int));
	cudaMalloc((void**)&d_B, 10 * sizeof(int));

	cudaMemcpy(d_A, A, count * sizeof(int), cudaMemcpyHostToDevice);

	partAKernel<<< 128, 128 >>>(d_A, d_B, count);

	cudaThreadSynchronize();
	cudaMemcpy(B, d_B, 10 * sizeof(int), cudaMemcpyDeviceToHost);

	cudaFree(d_B);
	cudaFree(d_A);

	int* result = (int*)malloc(sizeof(int) * 10);
	memcpy(result, B, 10 * sizeof(int));
	return result;
}