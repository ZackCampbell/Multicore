package q2.b;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class PIncrement implements Runnable {
    private final int pid;
    private int inc;
    private static final int NUM = 120000;
    private static volatile AtomicInteger result;
    private static FastMutex fm;

    private PIncrement(int pid, int numInc) {
        this.pid = pid;
        this.inc = numInc;
    }

    public static int parallelIncrement(int c, int numThreads) {
        result = new AtomicInteger(c);
        fm = new FastMutex(numThreads);
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            threads.add(new Thread(new PIncrement(i, NUM/numThreads)));
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
        return result.get();
    }

    @Override
    public void run() {
        while (this.inc > 0) {
            fm.acquire(this.pid);
            result.getAndIncrement();
            fm.release(this.pid);
            this.inc--;
        }
    }
}

class FastMutex {
    private volatile int X, Y;
    private volatile boolean[] flag;
    public FastMutex(int nSRMWRegisters) {
        X = -1;
        Y = -1;
        flag = new boolean[nSRMWRegisters];
        Arrays.fill(flag, false);
    }

    @SuppressWarnings("all")
    public void acquire(int i) {
        while (true) {
            flag[i] = true;
            X = i;
            if (Y != -1) {      // Splitter left
                flag[i] = false;
                while (Y != -1) {}
            } else {
                Y = i;
                if (X == i) {   // Success with splitter
                    return;     // Fast Path
                } else {        // Splitter Right
                    flag[i] = false;
                    for (int j = 1; j < flag.length; j++) {
                        while (flag[j]) {}
                    }
                    if (Y == i)
                        return; // Slow Path
                    else {
                        while (Y != -1) {}
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