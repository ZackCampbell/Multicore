#include <stdio.h>
#include <stdlib.h>
#include <cuda.h>

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
	FILE* inp = fopen("./inp.txt", "r");
	
