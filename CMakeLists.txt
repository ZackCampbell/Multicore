cmake_minimum_required(VERSION 3.6)
project(Multicore)

#set(CMAKE_CXX_STANDARD 11)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fopenmp")

set(SOURCE_FILES
        hw2/q4/hw2_q4_a.c
        hw2/q4/hw2_q4_b.c
        )

add_executable(Multicore ${SOURCE_FILES})