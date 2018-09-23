import matplotlib.patches as mpatches
import matplotlib.pyplot as plt

threads = list((1, 2, 4, 8))
anderson = [0.023735584, 0.017731037, 0.014806327, 0.011248594]
lamport = [0.009341668, 0.012728266, 0.018866985, 0.025974302]

plt.scatter(threads, anderson, color='r')
plt.scatter(threads, lamport, color='b')
plt.xlabel("# threads")
plt.ylabel("time (s)")
plt.title("The Affect of Thread Density on Run Time")
red_patch = mpatches.Patch(color='r', label='Anderson')
blue_patch = mpatches.Patch(color='b', label='Lamport')
plt.legend(handles=[red_patch, blue_patch])
plt.show()
