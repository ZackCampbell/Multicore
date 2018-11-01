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

__global__ void comparevals(int* src, int* dest, int len) {
	int s1, s2, d, val1, val2, zerolen;
	zerolen = len - 1;
	d = blockIdx.x;
	s1 = d * 2;
	s2 = s1 + 1;
	if (s1 > zerolen) {
		return;
	}
	val1 = src[s1];
	if (s2 > zerolen) {
		dest[d] = val1;
		return;
	}
	val2 = src[s2];
	dest[d] = ((val1 < val2) ? val1 : val2);
}

int getmin() {
	int count, min;
	int* arr = getarr(&count);
	int* csrc; 
	int* cdest;
	cudaMalloc((void**) &csrc, count * sizeof(int));
	cudaMalloc((void**) &cdest, count * sizeof(int));
	cudaMemcpy((void*) csrc, (void*) arr, count * sizeof(int), cudaMemcpyHostToDevice);
	cudaMemcpy((void*) cdest, (void*) arr, count * sizeof(int), cudaMemcpyHostToDevice);
	for(int i = count; i != 1; i = (i / 2) + (i % 2)) {
		comparevals<<<((count/2) + (count%2)), 1>>>(csrc, cdest, i);
		cudaThreadSynchronize();
		cudaMemcpy((void*) csrc, (void*) cdest, count * sizeof(int), cudaMemcpyDeviceToDevice);
	}
	cudaMemcpy((void*) arr, (void*) cdest, count * sizeof(int), cudaMemcpyDeviceToHost);
	min = arr[0];	
	cudaFree(cdest);
	cudaFree(csrc);
	free(arr);
	return min;
}

__global__ void lastdigit(int* arr) {
	arr[blockIdx.x] = arr[blockIdx.x] % 10;
}

int* getlastdigits(int* len) {
	int* arr = getarr(len);
	int mylen = *len;
	int* carr;
	cudaMalloc((void**) &carr, mylen * sizeof(int));
	cudaMemcpy((void*) carr, (void*) arr, mylen * sizeof(int), cudaMemcpyHostToDevice);
	lastdigit<<<mylen, 1>>>(carr);
	cudaThreadSynchronize();
	cudaMemcpy((void*) arr, (void*) carr, mylen * sizeof(int), cudaMemcpyDeviceToHost);
	cudaFree(carr);
	return arr;
}

int main(int argc, char** argv) {
	int min;
	min = getmin();
	FILE* f = fopen("./q1a.txt", "w");
	fprintf(f, "%d", min);
	fclose(f);
	
	int len;	
	int* ldarr = getlastdigits(&len);
	f = fopen("./q1b.txt", "w");
	for(int i = 0; i < len; i++) {
		if(i == len - 1) fprintf(f, "%d", ldarr[i]);	
		else fprintf(f, "%d, ", ldarr[i]);
	}
	free(ldarr);	
	return 0;
}

