package q2.c;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class PIncrement implements Runnable{
    private int inc;
    private static final int NUM = 120000;
    private static volatile AtomicInteger result;
    private static AndersonLock aLock;

    public PIncrement(int numInc) {
        this.inc = numInc;
    }

    public static int parallelIncrement(int c, int numThreads) {
        result = new AtomicInteger(c);
        ArrayList<Thread> threads = new ArrayList<>();
        aLock = new AndersonLock(numThreads);
        for (int i = 0; i < numThreads; i++) {
            threads.add(new Thread(new PIncrement(NUM/numThreads)));
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
            aLock.lock();
            result.getAndIncrement();
            aLock.unlock();
            this.inc--;
        }
    }
}

interface myLock {
    void lock();
    void unlock();
}

class AndersonLock implements myLock {
    private AtomicInteger tailSlot = new AtomicInteger(0);
    private int n;
    private volatile boolean[] available;
    private ThreadLocal<Integer> mySlot = new ThreadLocal<>(); //     Initialize to 0
    public AndersonLock(int n) {
        this.n = n;
        available = new boolean[n];
        Arrays.fill(available, false);
        available[0] = true;
        try {
            mySlot.set(0);
        } catch (Exception e) {
            System.err.println("Error: " + e);
        }
    }
    public void lock() {
        mySlot.set(tailSlot.getAndIncrement() % n);
        while (!available[mySlot.get()]) {}
    }
    public void unlock() {
        available[mySlot.get()] = false;
        available[(mySlot.get() + 1) % n] = true;
    }
}