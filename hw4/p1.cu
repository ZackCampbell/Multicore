#include <stdio.h>
#include <stdlib.h>
#include <cuda.h>
#include "parse.h"

__global__ void comparevals(int* src, int* dest, int len) {
	int s1, s2, d, val1, val2, zerolen;
	zerolen = len - 1;
	d = blockIdx.x;
	s1 = d * 2;
	s2 = s1 + 1;
	if (s1 + 1 > zerolen) {
		return;
	}
	val1 = src[s1];
	if (s2 + 1 > zerolen) {
		dest[d] = val1;
		return;
	}
	val2 = src[s2];
	dest[d] = ((val1 < val2) ? val1 : val2);
}

int main(int argc, char** argv) {
	bool DEBUG = true;
	int count;
	int* arr = getarr(&count);
        for(int i = 0; i < count; i++){
                printf("%d, ", arr[i]);
        }
	printf("\n\n");
	int* csrc; 
	int* cdest;
	cudaMalloc(&csrc, count * sizeof(int));
	cudaMalloc(&cdest, count * sizeof(int));
	cudaMemcpy(arr, csrc, count, cudaMemcpyHostToDevice);
	dim3 dimGrid((count / 2) + (count % 2), 1);
	dim3 dimBlock(1, 1, 1);	
	for(int i = count; i == 0; i = (i / 2) + (i % 2)) {
		comparevals<<<dimGrid, dimBlock>>>(csrc, cdest, i);
		cudaMemcpy(cdest, csrc, count, cudaMemcpyDeviceToDevice);
		cudaThreadSynchronize();
	}
	cudaMemcpy(cdest, arr, count, cudaMemcpyDeviceToHost);
	cudaFree(cdest);
	cudaFree(csrc);
	printf("%d, ", arr[0]);
	free(arr);
	return 0;
}

