import matplotlib.patches as mpatches
import matplotlib.pyplot as plt

threads = list((1, 2, 3, 4, 5, 6, 7, 8))
times = [0.05475, 0.05195, 0.05296, 0.05378, 0.05324, 
           0.05318, 0.05541, 0.05250]
plt.scatter(threads, times, color='r')
plt.xlabel("# threads")
plt.ylabel("time (s)")
plt.title("The Affect of Thread Density on Run Time")
red_patch = mpatches.Patch(color='r', label='matrixMult')
plt.legend(handles=[red_patch])
plt.show()
