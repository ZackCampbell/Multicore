package q2.a;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class PIncrement implements Runnable{
    // Delta = 10 microseconds

    private int pid;
    private int inc;
    private static final int NUM = 120000;
    private static volatile AtomicInteger result;
    private static Fischer f;

    public PIncrement(int pid, int numInc) {
        this.pid = pid;
        this.inc = numInc;
    }

    public static int parallelIncrement(int c, int numThreads) {
        result = new AtomicInteger(c);
        f = new Fischer(numThreads);
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
            f.requestCS(this.pid);
            result.getAndIncrement();
            f.releaseCS(this.pid);
            this.inc--;
        }
    }
}

interface Lock {
    void requestCS(int pid);
    void releaseCS(int pid);
}

class Fischer implements Lock {
    int N;
    private volatile int turn;
    private final int delta = 10000;
    public Fischer(int numProc) {
        N = numProc;
        turn = -1;
    }
    public void requestCS(int i) {
        while (true) {
            while (turn != -1) {}
            turn = i;
            try {
                Thread.sleep(0, delta);
            } catch (InterruptedException e) {}
            if (turn == i) return;
        }
    }
    public void releaseCS(int i) {
        turn = -1;
    }
}