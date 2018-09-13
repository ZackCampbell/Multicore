import matplotlib.patches as mpatches
import matplotlib.pyplot as plt

threads = list(range(1, 9))
t = [0.076463369, 0.248757719, 0.326645114, 0.537800414, 
     0.6728785, 1.213914832, 1.520742888, 1.797777151]
a = [0.029012504, 0.048385248, 0.04299533, 0.091851833,
     0.077994731, 0.080233403, 0.10093866, 0.080150014]
s = [0.042573681, 0.06438908, 0.042660064, 0.064097432,
     0.063287491, 0.064328784, 0.064621286,0.062956073]
r = [0.069135406, 0.065653171, 0.073068371, 0.057654248,
     0.056775884, 0.060213219, 0.072539813, 0.060715263]

plt.scatter(threads, t, color='g')
plt.scatter(threads, a, color='r')
plt.scatter(threads, s, color='y')
plt.scatter(threads, r, color='k')
plt.xlabel("# threads")
plt.ylabel("time (s)")
plt.title("The Affect of Thread Density on Run Time")
red_patch = mpatches.Patch(color='r', label='atomic int')
green_patch = mpatches.Patch(color='g', label='tournament')
yellow_patch = mpatches.Patch(color='y', label='synchronized')
black_patch = mpatches.Patch(color='k', label='reentrant lock')
plt.legend(handles=[red_patch, green_patch, yellow_patch, black_patch])
plt.show()