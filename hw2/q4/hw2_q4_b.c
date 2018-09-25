#include <stdio.h>
#include <stdlib.h>
#include <omp.h>
#include <stdint.h>

struct point {
    int x;
    int y;
};

double MonteCarlo(int s) {
    double radius = 100.0;
    int circlePoints;

    for (int i = 0; i < s; i++) {       // For every point
        if () {                         // If the point is in the circle
            // Increment circlePoints
        }
    }

    // Block threads here
    return (double)circlePoints * 4 / s;
    //Write your code here
}

void main() {
    double pi;
    pi=MonteCarlo(100000000);
    printf("Value of pi is: %lf\n",pi);
}



