#include <stdio.h>
#include <stdlib.h>
#include <cuda.h>

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
