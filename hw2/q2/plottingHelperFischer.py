import matplotlib.patches as mpatches
import matplotlib.pyplot as plt

threads = list((1, 2, 4, 8))
f = [226.002062636, 223.413780569, 212.242842915, 647.635516616]

plt.scatter(threads, f, color='r')
plt.xlabel("# threads")
plt.ylabel("time (s)")
plt.title("The Affect of Thread Density on Run Time")
red_patch = mpatches.Patch(color='r', label='Fischer')
plt.legend(handles=[red_patch])
plt.show()
