package q2.a;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class PIncrement {
    // Delta = 10 microseconds
    static volatile AtomicInteger result;
    static Fischer f;
    public static int parallelIncrement(int c, int numThreads) {
        result = new AtomicInteger(c);
        f = new Fischer(numThreads);
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            final int pid = i;
            Thread t = new Thread(() -> {
                f.requestCS(pid);
                for (int j = 0; j < (120000/numThreads); j++) {
                    result.set(result.get() + 1);
                }
                f.releaseCS(pid);
            });
            threads.add(t);
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

}

interface Lock {
    void requestCS(int pid);
    void releaseCS(int pid);
}

class Fischer implements Lock {
    int N;
    int turn;
    int delta;
    public Fischer(int numProc) {
        N = numProc;
        turn = -1;
        delta = 10000;
    }
    public void requestCS(int i) {
        while (true) {
            while (turn != -1) {
                turn = i;
                try {
                    Thread.sleep(0, delta);
                } catch (InterruptedException e) {}
                if (turn == i) return;
            }
        }
    }
    public void releaseCS(int i) {
        turn = -1;
    }
}