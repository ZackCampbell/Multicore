#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <cuda.h>
#include "cuda_runtime.h"
#include "device_launch_parameters.h"
#include <sm_35_atomic_functions.h>

int* partA(int * A, int * B, int count);
int* partB(int * A, int * B, int count);
int* partC(int * B, int * C, int count);

#define OUTPUT_SIZE 10

int getnextnum(FILE* f, int* val) {
	char num[6];
	int idx = 0;
	char c;
	int ret = 0;
	while (1) {
		num[idx] = '\0';
		c = getc(f);
		if (c == EOF) {
			ret = 1;
			break;
		}
		if (c == ',') {
			c = getc(f);
			break;
		}
		num[idx] = c;
		idx++;
	}
	*val = atoi(num);
	return ret;
}

int* getarr(int* arrlen) {
	FILE* inp = fopen("../MulticoreHW4CUDA/inp.txt", "r");
	int val;
	int count = 0;
	int len = 0;
	int* arr = (int*)malloc(1 * sizeof(int));
	int* transfer;
	int end = 0;
	while (!end) {
		if (count == len) {
			len += 10;
			transfer = (int*)malloc(len * sizeof(int));
			memcpy(transfer, arr, count * sizeof(int));
			free(arr);
			arr = transfer;
		}
		end = getnextnum(inp, &val);
		arr[count] = val;
		count++;
	}
	fclose(inp);
	transfer = (int*)malloc(count * sizeof(int));
	memcpy(transfer, arr, count * sizeof(int));
	free(arr);
	arr = transfer;
	*arrlen = count;
	return arr;
}

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

__global__ void partCKernel(int* B, int* C, int len) {
	int thid = threadIdx.x;
	C[thid] = B[thid];
	__syncthreads();
	for (int offset = 1; offset < len; offset *= 2) {
		if (thid - offset >= 0)
			C[thid] += C[thid - offset];
		__syncthreads();
	}
}

int main() {
	int count;
	int* A = getarr(&count);
	
	int* partAOutput = (int*)malloc(sizeof(int) * 10);
	int* partBOutput = (int*)malloc(sizeof(int) * 10);
	int* partCOutput = (int*)malloc(sizeof(int) * 16);
	for (int i = 0; i < 10; i++) {
		partAOutput[i] = 0;
		partBOutput[i] = 0;
	}
	for (int i = 0; i < 16; i++) {
		partCOutput[i] = 0;
	}

	int* partAAnswer = partA(A, partAOutput, count);
	int* partBAnswer = partB(A, partBOutput, count);
	int* partCAnswer = partC(partAOutput, partCOutput, 16);
	
	FILE * aOut = fopen("q2a.txt", "w");
	FILE * bOut = fopen("q2b.txt", "w");
	FILE * cOut = fopen("q2c.txt", "w");
	for (int i = 0; i < 10; i++) {
		fprintf(aOut, "%d, ", partAAnswer[i]);
		fprintf(bOut, "%d, ", partBAnswer[i]);
		fprintf(cOut, "%d, ", partCAnswer[i]);
	}
	fclose(aOut);
	fclose(bOut);
	fclose(cOut);

	free(partAOutput);
	free(partBOutput);
	free(partCOutput);
	free(A);
	free(partAAnswer);
	free(partBAnswer);
	free(partCAnswer);
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

int* partC(int* B, int* C, int count) {
	int* d_B;
	int* d_C;

	int* paddedB = (int*)malloc(sizeof(int) * 16);
	for (int i = 0; i < 16; i++) {
		paddedB[i] = 0;
	}
	for (int i = 0; i < 10; i++) {
		paddedB[i + 6] = B[i];
	}

	cudaMalloc((void**)&d_B, 16 * sizeof(int));
	cudaMalloc((void**)&d_C, 16 * sizeof(int));

	dim3 dimGrid(1, 1);
	dim3 dimBlock(16, 1, 1);

	cudaMemcpy(d_B, paddedB, 16 * sizeof(int), cudaMemcpyHostToDevice);

	partCKernel <<< dimGrid, dimBlock >>> (d_B, d_C, 16);

	cudaThreadSynchronize();
	cudaMemcpy(C, d_C, 16 * sizeof(int), cudaMemcpyDeviceToHost);

	cudaFree(d_C);
	cudaFree(d_B);

	free(paddedB);

	int* output = (int*)malloc(sizeof(int) * 10);
	for (int i = 0; i < 10; i++) {
		output[i] = C[i + 6];
	}

	int* result = (int*)malloc(sizeof(int) * 10);
	memcpy(result, output, 10 * sizeof(int));
	free(output);
	return result;
}