#include <stdio.h>
#include <stdlib.h>
#include <omp.h>
#include <stdint.h>
#include <math.h>
#include <time.h>

#define RADIUS 10000

double MonteCarlo(int s) {
    double circlePoints = 0.0, rand_x, rand_y;
//    printf("Max Threads = %d\n", omp_get_max_threads());
    #pragma omp parallel for
    for (int i = 0; i < s; i++) {
        rand_x = (double)(rand() % (RADIUS + 1));
        rand_y = (double)(rand() % (RADIUS + 1));
        if (pow(rand_x, 2.0) + pow(rand_y, 2.0) <= pow(RADIUS, 2)) {       // If the point is in the circle
            #pragma omp atomic
            circlePoints++;                                   // Increment circlePoints
        }
        printf("Num threads: %d\n", omp_get_num_threads());
    }


//    #pragma omp barrier
    // Block threads here
    return (circlePoints * 4.0) / s;
}

void main() {
    srand(time(NULL));
    double pi;
    pi = MonteCarlo(100000000);
    printf("Value of pi is: %lf\n", pi);
}



