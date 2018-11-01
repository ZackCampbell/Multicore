#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <cuda.h>
#include "cuda_runtime.h"
#include "device_launch_parameters.h"
#include "parse.h"
#include <sm_35_atomic_functions.h>

int* partA(int * A, int * B, int count);
int* partB(int * A, int * B, int count);

#define OUTPUT_SIZE 10

__global__ void partAKernel(int* A, int* B, int len) {
	int index = threadIdx.x + blockIdx.x * blockDim.x;
	
	for (int i = index; i < len; i += blockDim.x * gridDim.x) {
		atomicAdd(&B[A[i]/100], 1);
	}
}

__global__ void partBKernel(int* A, int* B, int len) {
	__shared__ int localA[OUTPUT_SIZE];
	__shared__ int localB[OUTPUT_SIZE];
	int gindex = threadIdx.x + blockIdx.x * blockDim.x;
	int lindex = threadIdx.x;
	localA[lindex] = A[gindex] / 100;
	localB[lindex] = 0;
	__syncthreads();

	if (gindex < len) {
		atomicAdd(&localB[localA[lindex]], 1);
	}
	
	__syncthreads();

	atomicAdd(&B[lindex], localB[lindex]);
}

int main() {
	int count;
	int* A = getarr(&count);
	
	int* partAOutput = (int*)malloc(sizeof(int) * 10);
	for (int i = 0; i < 10; i++) {
		partAOutput[i] = 0;
	}
	int* partBOutput = (int*)malloc(sizeof(int) * 10);
	for (int i = 0; i < 10; i++) {
		partBOutput[i] = 0;
	}

	int* partAAnswer = partA(A, partAOutput, count);
	int* partBAnswer = partB(A, partBOutput, count);
	for (int i = 0; i < 10; i++) {
		printf("%d, ", partBAnswer[i]);
	}
	
	free(partAOutput);
	free(partBOutput);
	free(A);
	free(partAAnswer);
	free(partBAnswer);
	return 0;
}

int* partA(int* A, int* B, int count) {
	int* d_A;
	int* d_B;

	cudaMalloc((void**)&d_A, count * sizeof(int));
	cudaMalloc((void**)&d_B, OUTPUT_SIZE * sizeof(int));

	cudaMemcpy(d_A, A, count * sizeof(int), cudaMemcpyHostToDevice);

	dim3 dimGrid((count / 2) + (count % 2), 1);
	dim3 dimBlock(1, 1, 1);

	partAKernel<<< dimGrid, dimBlock >>>(d_A, d_B, count);

	cudaThreadSynchronize();
	cudaMemcpy(B, d_B, OUTPUT_SIZE * sizeof(int), cudaMemcpyDeviceToHost);

	cudaFree(d_B);
	cudaFree(d_A);

	int* result = (int*)malloc(sizeof(int) * 10);
	memcpy(result, B, OUTPUT_SIZE * sizeof(int));
	return result;
}

int* partB(int* A, int* B, int count) {
	int* d_A;
	int* d_B;

	cudaMalloc((void**)&d_A, count * sizeof(int));
	cudaMalloc((void**)&d_B, OUTPUT_SIZE * sizeof(int));

	dim3 dimGrid(1024, 1);
	dim3 dimBlock(10, 1, 1);

	cudaMemcpy(d_A, A, count * sizeof(int), cudaMemcpyHostToDevice);

	partBKernel <<< dimGrid, dimBlock >>> (d_A, d_B, count);

	cudaThreadSynchronize();
	cudaMemcpy(B, d_B, OUTPUT_SIZE * sizeof(int), cudaMemcpyDeviceToHost);

	cudaFree(d_B);
	cudaFree(d_A);

	int* result = (int*)malloc(sizeof(int) * 10);
	memcpy(result, B, OUTPUT_SIZE * sizeof(int));
	return result;
}