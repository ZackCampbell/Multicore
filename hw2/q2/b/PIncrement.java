package q2.b;

import java.util.ArrayList;

public class PIncrement implements Runnable {
    private int pid;
    private int inc;
    private static final int num = 120000;
    private static volatile int result;
    private static volatile FastMutex fm;

    private PIncrement(int pid, int numInc) {
        this.pid = pid;
        this.inc = numInc;
    }

    public static int parallelIncrement(int c, int numThreads) {
        result = c;
        fm = new FastMutex(numThreads);
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            threads.add(new Thread(new PIncrement(i, num/numThreads)));
        }
        for (Thread t : threads) {
            t.start();
        }
        boolean busy = true;
        while(busy) {
            busy = false;
            for(Thread t : threads) {
                if(t.isAlive()) {
                    busy = true;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public void run() {
        for (int i = 0; i < inc; i++) {
            fm.acquire(pid);
            result++;
            fm.release(pid);
        }
    }
}

class FastMutex {
    private int X, Y;
    private boolean[] flag;
    public FastMutex(int nSRMWRegisters) {
        X = -1;
        Y = -1;
        flag = new boolean[nSRMWRegisters];
        for (int i = 0; i < flag.length; i++) {
            flag[i] = false;
        }
    }

    @SuppressWarnings("all")
    public void acquire(int i) {
        while (true) {
            flag[i] = true;
            X = i;
            if (Y != -1) {      // Splitter left
                flag[i] = false;
                while (Y != -1) {}
                continue;
            } else {
                Y = i;
                if (X == i) {   // Success with splitter
                    return;     // Fast Path
                } else {        // Splitter Right
                    flag[i] = false;
                    for (boolean j : flag) {
                        while (j) {}
                    }
                    if (Y == i)
                        return; // Slow Path
                    else {
                        while (Y != -1) {}
                        continue;
                    }
                }
            }
        }
    }

    public void release(int i) {
        Y = -1;
        flag[i] = false;
    }
}