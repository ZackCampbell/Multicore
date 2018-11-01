#include <stdio.h>
#include <stdlib.h>
#include <cuda.h>
#include "parse.h"

int getnextnum(FILE* f, int* val){
        char num[6];
        int idx = 0;
        char c;
        int ret = 0;
        while(1) {
                num[idx] = '\0';
                c = getc(f);
                if(c == EOF) {
                        ret = 1;
                        break;
                }
                if(c == ',') {
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
        FILE* inp = fopen("./inp.txt", "r");
        int val;
        int count = 0;
        int len = 0;
        int* arr = (int*)malloc(1 * sizeof(int));
        int* transfer;
        int end = 0;
        while(!end) {
                if(count == len) {
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

__global__ void markodds(int* src, int* dest) {
	if(src[blockIdx.x] % 2 == 0) dest[blockIdx.x] = 0;
	else dest[blockIdx.x] = 1;
}

__global__ void presum(int* src, int* dest, int len, int offset, int i) {
	int id = blockIdx.x;
	int idx1, idx2;
	if(id < i) {
		idx1 = offset * (2 * id + 1) - 1;
		idx2 = offset * (2 * id + 2) - 1;
		dest[idx2] += dest[idx1];
	}
}

__global__ void midsum(int* dest, int len) {
	int id = blockIdx.x;
	if(id == 0) dest[len - 1] = 0;
}

__global__ void postsum(int* src, int* dest, int len, int offset, int i){
	int hold, idx1, idx2;
	int id = blockIdx.x;
	if(id < i) {
		idx1 = offset * (2 * id + 1) - 1;
		idx2 = offset * (2 * id + 2) - 1;
		hold = dest[idx1];
		dest[idx1] = dest[idx2];
		dest[idx2] += hold;
	}
}

__global__ void genresult(int* src1, int* src2, int* src3, int* dest) {
	if(src2[blockIdx.x] != 1) return;	
	int idx = src3[blockIdx.x];
	int val = src1[blockIdx.x];
	dest[idx] = val;
}

int main(int argc, char** argv) {
	int len;
	int* arr = getarr(&len);

	// zero extend arr to first largest power of 2
	for(int i = 2; ; i *= 2) {
		if(i > len) {
			int* tmp = (int*) malloc(i * sizeof(int));
			for(int j = 0; j < i; j++) {
				if(j < len) tmp[j] = arr[j];
				else tmp[j] = 0;
			}
			len = i;
			free(arr);
			arr = tmp;
			break;
		}	
	}

	int* carr; int* cisodd; int* cpresum; int* cresult;
	cudaMalloc((void**) &carr, len*sizeof(int));
	cudaMalloc((void**) &cisodd, len*sizeof(int));
	cudaMalloc((void**) &cpresum, len*sizeof(int));
	cudaMalloc((void**) &cresult, len*sizeof(int));
	cudaMemcpy((void*) carr, (void*) arr, len*sizeof(int), cudaMemcpyHostToDevice);

	markodds<<<len, 1>>>(carr, cisodd);
	cudaThreadSynchronize();

	cudaMemcpy((void*) cpresum, (void*) cisodd, len*sizeof(int), cudaMemcpyDeviceToDevice);
	int offset = 1;
	for(int i = len >> 1; i > 0; i = i >> 1) {
		cudaThreadSynchronize();
		presum<<<len, 1>>>(cisodd, cpresum, len, offset, i);
		cudaThreadSynchronize();
		offset *= 2;
	}

	midsum<<<len, 1>>>(cpresum, len);	
	cudaThreadSynchronize();

	for(int i = 1; i < len; i *= 2) {
		offset >>= 1;
		cudaThreadSynchronize();
		postsum<<<len, 1>>>(cisodd, cpresum, len, offset, i);	
		cudaThreadSynchronize();
	}

	genresult<<<len, 1>>>(carr, cisodd, cpresum, cresult);
	cudaThreadSynchronize();
	
	int reslen;
	cudaMemcpy((void*) &reslen, (void*) &cpresum[len - 1], sizeof(int), cudaMemcpyDeviceToHost);
	reslen += arr[len - 1];
	int* result = (int*) malloc(reslen * sizeof(int));
	cudaMemcpy((void*) result, (void*) cresult, reslen * sizeof(int), cudaMemcpyDeviceToHost);

	FILE* f = fopen("./q3.txt", "w");
	for(int i = 0; i < reslen; i++) {
		if(i == reslen - 1) fprintf(f, "%d", result[i]);
		else fprintf(f, "%d, ", result[i]);
	}

	cudaFree(carr);
	cudaFree(cisodd);
	cudaFree(cpresum);
	cudaFree(cresult);
	free(arr);
	free(result);
	return 0;
}

