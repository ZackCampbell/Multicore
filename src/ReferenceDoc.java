import java.util.concurrent.*;
import java.util.Random;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ReferenceDoc {
    public static void main(String[] args) {
        System.out.println("Hello World");
        //  Test various algorithms here

        // For HelloWorldThread
        // HelloWorldThread t = new HelloWorldThread();
        // t.start();

        // For RunnableExampleBar
        // RunnableExampleBar r1 = new RunnableExampleBar("Romeo");
        // Thread t1 = new Thread(r1);
        // t1.start();
        // RunnableExampleBar r2 = new RunnableExampleBar("Romeo");
        // Thread t2 = new Thread(r2);
        // t2.start();

        // For Fibonacci using join
//        Fibonacci f1 = new Fibonacci(8);
//        f1.start();
//        try {
//            f1.join();
//        } catch (InterruptedException e) {}
//        System.out.println("Answer is " + f1.getResult());

        // For Fibonacci using Callable
//        try {
//            ExecutorService es = Executors.newSingleThreadExecutor();
//            Fibonacci2 f = new Fibonacci2(8);
//            Future<Integer> f1 = es.submit(f);
//            System.out.println("Answer is: " + f1.get());
//            es.shutdown();
//            f.threadPool.shutdown();
//        } catch (Exception e) {
//            System.err.println(e);
//        }

        // For Fibonacci using Fork
//        int processors = Runtime.getRuntime().availableProcessors();
//        System.out.println("Number of processors: " + processors);
//        Fibonacci3 f = new Fibonacci3(8);
//        ForkJoinPool pool = new ForkJoinPool(processors);
//        int result = pool.invoke(f);
//        System.out.println("Result: " + result);

        // For TestMutualExclusion
//        TestMutualExclusion t[];
//        int N = 8;
//        t = new TestMutualExclusion[N];
//        Lock lock = new Bakery(N); // Or any other mutex algorithm
//        for (int i = 0; i < N; i++) {
//            t[i] = new TestMutualExclusion(i, lock);
//            t[i].start();
//        }
    }
}

/**
 * Fibonacci using "join"
 */
class Fibonacci extends Thread{
    int n;
    int result;
    public Fibonacci(int n) {
        this.n = n;
    }
    public void run() {
        if (n == 0 || n == 1) result = 1;
        else {
            Fibonacci f1 = new Fibonacci(n-1);
            Fibonacci f2 = new Fibonacci(n-2);
            f1.start();
            f2.start();
            try {
                f1.join();
                f2.join();
            } catch (InterruptedException e) {}
            result = f1.getResult() + f2.getResult();
        }
    }
    public int getResult() {
        return result;
    }
}

/**
 * Fibonacci using Callable
 */
class Fibonacci2 implements Callable<Integer> {
    public static ExecutorService threadPool = Executors.newCachedThreadPool();
    int n;
    public Fibonacci2(int n) {
        this.n = n;
    }
    public Integer call() {
        try {
            if((n == 0)||(n == 1 )) return 1;
            else {
                Future<Integer> f1 = threadPool.submit(new Fibonacci2(n-1));
                Future<Integer> f2 = threadPool.submit(new Fibonacci2(n-2));
                return f1.get() + f2.get();
            }
        } catch (Exception e) {
            System.err.println(e);
            return 1;
        }
    }
}

/**
 * Fibonacci using Fork
 */
class Fibonacci3 extends RecursiveTask<Integer> {
    final int n;
    public Fibonacci3(int n) {
        this.n = n;
    }
    protected Integer compute() {
        if (n == 0 || n == 1) return 1;
        Fibonacci3 f1 = new Fibonacci3(n-1);
        f1.fork();
        Fibonacci3 f2 = new Fibonacci3(n-2);
        return f2.compute() + f1.join();
    }
}

class HelloWorldThread extends Thread {
    public void run() {
        System.out.println("Hello World");
    }
}

/**
 * Example using Runnable
 */
class RunnableExample {
    String name;
    public RunnableExample(String s) {
        name = s;
    }
    public void setName(String s) {
        name = s;
    }
    public String getName() {
        return name;
    }
}
class RunnableExampleBar extends RunnableExample implements Runnable {
    public RunnableExampleBar(String s) {
        super(s);
    }
    public void run() {
        for (int i = 0; i < 10; i++)
            System.out.println(getName() + " Hello World");
    }
}

class TestMutualExclusion extends Thread {
    int myId;
    Lock lock;
    Random r = new Random();
    public TestMutualExclusion(int id, Lock lock) {
        myId = id;
        this.lock = lock;
    }
    void nonCriticalSection() {
        System.out.println(myId + " is not in CS");
        Util.mySleep(r.nextInt(1000));
    }
    void CriticalSection() {
        System.out.println(myId + " is in CS***");
        Util.mySleep(r.nextInt(1000));
    }
    public void run() {
        while (true) {
            lock.requestCS(myId);
            CriticalSection();
            lock.releaseCS(myId);
            nonCriticalSection();
        }
    }
}

interface Lock {
    void requestCS(int pid);
    void releaseCS(int pid);
}
interface myLock {
    void lock();
    void unlock();
}

/**
 * An attempt that violates mutual exclusion
 */
class Attempt1 implements Lock {
    boolean openDoor = true;
    public void requestCS(int i) {
        while (!openDoor)
            openDoor = false;
    }
    public void releaseCS(int i) {
        openDoor = true;
    }
}

/**
 * An attempt that can deadlock
 */
class Attempt2 implements Lock {
    boolean wantCS[] = {false, false};
    public void requestCS(int i) {      // Entry Protocol
        wantCS[i] = true;   // Declare Intent
        while (wantCS[1-i]);    // Busy Wait
    }
    public void releaseCS(int i) {
        wantCS[i] = false;
    }

}

/**
 * An attempt with strict alternation
 */
class Attempt3 implements Lock {
    int turn = 0;
    public void requestCS(int i) {
        while (turn == 1-i);
    }
    public void releaseCS(int i) {
        turn = 1-i;
    }
}

class PetersonAlgorithm implements Lock {
    boolean wantCS[] = {false, false};
    int turn = 1;
    public void requestCS(int i) {
        int j = 1- i;
        wantCS[i] = true;
        turn = j;
        while (wantCS[j] && (turn == j));
    }
    public void releaseCS(int i) {
        wantCS[i] = false;
    }
}

/**
 * Utilizes multiple Peterson's algorithms to get the answer
 */
class FilterAlgorithm implements Lock {
    int N;
    int[] gate;
    int[] last;
    public FilterAlgorithm(int numProc) {
        N = numProc;
        gate = new int[N];  // We only use gate[1]... gate[N-1]; gate[0] is unused
        Arrays.fill(gate, 0);
        last = new int[N];
        Arrays.fill(last, 0);
    }
    public void requestCS(int i) {
        for (int k = 1; k < N; k++) {
            gate[i] = k;
            last[k] = i;
            for (int j = 0; j < N; j++) {
                while ((j != i) &&          // There is some other process
                        (gate[j] >= k) &&   // that is ahead or at the same level
                        (last[k] == i)      // and I am the last to update last[k]
                ) {}    // Busy Wait
            }
        }
    }
    public void releaseCS(int i) {
        gate[i] = 0;
    }
}

/**
 * Lamport's Bakery Algorithm
 */
class Bakery implements Lock {
    int N;
    boolean[] choosing;     // Inside Doorway
    int[] number;
    public Bakery(int numProc) {
        N = numProc;
        choosing = new boolean[N];
        number = new int[N];
        for (int j = 0; j < N; j++) {
            choosing[j] = false;
            number[j] = 0;
        }
    }
    public void requestCS(int i) {
        // Step 1: Doorway: Choose a number
        choosing[i] = true;
        for (int j = 0; j < N; j++) {
            if (number[j] > number[i])
                number[i] = number[j];
        }
        number[i]++;
        choosing[i] = false;

        // Step 2: Check if my number is the smallest
        for (int j = 0; j < N; j++) {
            while (choosing[j]);    // Process j in doorway
            while ((number[j] != 0) &&
                    ((number[j] < number[i]) ||
                            ((number[j] == number[i]) && j < i))
            );      // Busy Wait
        }
    }
    public void releaseCS(int i) {
        number[i] = 0;
    }
}

/**
 * Fischer's mutual exclusion algorithm
 */
class Fischer implements Lock {
    int N;
    int turn;
    int delta;
    public Fischer(int numProc) {
        N = numProc;
        turn = -1;
        delta = 5;
    }
    public void requestCS(int i) {
        while (true) {
            while (turn != -1) {
                turn = i;
                try {
                    Thread.sleep(delta);
                } catch (InterruptedException e) {}
                if (turn == i) return;
            }
        }
    }
    public void releaseCS(int i) {
        turn = -1;
    }
}

/**
 * Splitter algorithm
 */
class Splitter {
    private boolean door;
    private int last;
    public Splitter() {
        door = true;
        last = -1;
    }
    public String split(int i) {
        if (!door) {
            return "Left";
        } else {
            door = false;
            if (last == i)
                return "Down";
            else
                return "Right";
        }
    }
}

/**
 * Lamport's Fast Mutex Algorithm
 */
class FastMutex {
    private int X, Y;
    private boolean[] flag;
    public FastMutex(int nSRMWRegisters) {
        X = -1;
        Y = -1;
        flag = new boolean[nSRMWRegisters];
    }

    @SuppressWarnings("all")
    public void acquire(int i) {
        while (true) {
            flag[i] = true;
            X = i;
            if (Y != -1) {      // Splitter left
                flag[i] = false;
                while (Y != -1) {}
                // continue;
            } else {
                Y = i;
                if (X == i) {   // Success with splitter
                    return;     // Fast Path
                } else {        // Splitter Right
                    flag[i] = false;
                    for (boolean j : flag) {
                        while (j){}
                    }
                    if (Y == i)
                        return; // Slow Path
                    else {
                        while (Y != -1){}
                        // continue;
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

/**
 * Building Locks
 */
class GetAndSet implements myLock {
    AtomicBoolean isOccupied = new AtomicBoolean(false);
    public void lock() {
        while (isOccupied.getAndSet(true)) {
            Thread.yield();
            // Skip
        }
    }
    public void unlock() {
        isOccupied.set(false);
    }
}

/**
 * If it doesn't succeed in getAndSet, it spins on the 'get' operation
 * Faster than just GetAndSet
 */
class GetAndGetAndSet implements myLock {
    AtomicBoolean isOccupied = new AtomicBoolean(false);
    public void lock() {
        while (true) {
            while (isOccupied.get()) {}
            if (!isOccupied.getAndSet(true)) return;
        }
    }
    public void unlock() {
        isOccupied.set(false);
    }
}

class MutexWithBackoff implements myLock {
    AtomicBoolean isOccupied = new AtomicBoolean(false);
    Random r = new Random();
    public void lock() {
        while (true) {
            while (isOccupied.get()) {}
            if (!isOccupied.getAndSet(true)) return;
            else {
                int timeToSleep = r.nextInt(1000);
                try {
                    Thread.sleep(timeToSleep);
                } catch (InterruptedException e) {}
            }
        }
    }
    public void unlock() {
        isOccupied.set(false);
    }
}

/**
 * NOT COMPLETE
 */
class TicketMutex implements myLock {
    AtomicInteger nextTicket = new AtomicInteger(0);
    AtomicInteger currentTicket = new AtomicInteger(0);
    public void lock(){
        int myticket = nextTicket.getAndIncrement();
        while(myticket != currentTicket.get()){}    // skip
    }
    public void unlock(){
        int temp = currentTicket.getAndIncrement();
    }
}

/**
 * Uses O(mn) space
 */
class AndersonLock implements myLock {
    AtomicInteger tailSlot = new AtomicInteger(0);
    int n;
    boolean[] available;
    ThreadLocal<Integer> mySlot; //     Initialize to 0
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

/**
 * CLHLock for mutex
 */
class CLHLock implements myLock {
    class Node {
        boolean locked;
    }
    AtomicReference<Node> tailNode;
    ThreadLocal<Node> myNode;
    ThreadLocal<Node> pred;

    public CLHLock() {
        tailNode = new AtomicReference<>(new Node());
        tailNode.get().locked = false;
        myNode = new ThreadLocal<>() {
            protected Node initialValue() {
                return new Node();
            }
        };
        pred = new ThreadLocal<>();
    }
    public void lock() {
        myNode.get().locked = true;
        pred.set(tailNode.getAndSet(myNode.get()));
        while (pred.get().locked) {
            Thread.yield();
        }
    }
    public void unlock() {
        myNode.get().locked = false;
        myNode.set(pred.get());     // Reusing predecessor node for future use
    }
}

class MCSLock implements myLock {
    class QNode {
        boolean locked;
        QNode next;
        QNode() {
            locked = true;
            next = null;
        }
    }
    AtomicReference<QNode> tailNode = new AtomicReference<>(null);
    ThreadLocal<QNode> myNode;

    public MCSLock() {
        myNode = new ThreadLocal<>() {
            protected QNode initialValue() {
                return new QNode();
            }
        };
    }
    public void lock() {
        QNode pred = tailNode.getAndSet(myNode.get());
        if (pred != null) {
            myNode.get().locked = true;
            pred.next = myNode.get();
            while (myNode.get().locked) {
                Thread.yield();
            }
        }
    }
    public void unlock() {
        if (myNode.get().next == null) {
            if (tailNode.compareAndSet(myNode.get(), null)) return;
            while (myNode.get().next == null) {
                Thread.yield();
            }
        }
        myNode.get().next.locked = false;
        myNode.get().next = null;
    }
}

/**
 * Reads and returns old value in memory and replaces it with a new value
 */
class TestAndSet {
    int myValue = -1;
    public synchronized int testAndSet(int newValue) {
        int oldValue = myValue;
        myValue = newValue;
        return oldValue;
    }
}

/**
 * Mutual exclusion using testAndSet
 */
class HWMutex implements Lock {
    TestAndSet lockFlag;
    public void requestCS(int i) {      // Entry Protocol
        while (lockFlag.testAndSet(1) == 1);
    }
    public void releaseCS(int i) {      // Exit Protocol
        lockFlag.testAndSet(0);
    }
}

/**
 * Swaps two memory locations in one atomic step
 */
class Synch {
    public static synchronized void swap(Boolean m1, Boolean m2) {
        Boolean temp = m1;
        m1 = m2;
        m2 = temp;
    }
}

class LFilter implements Lock {
    int n;
    int l;
    int[] gate;
    int[] last;
    public LFilter(int n, int l) {
        this.n = n;
        this.l = l;
        if (0 >= n - l + 1) {
            gate = new int[0];
            last = new int[0];
        } else {
            gate = new int[n - l + 1];
            last = new int[n - l + 1];
        }
        Arrays.fill(gate, 0);
        Arrays.fill(last, 0);
    }

    public void requestCS(int i) {
        for (int k = 1; k < n - l + 1; k++) {
            gate[i] = k;
            last[k] = i;
            int higher = l + 1;
            while (higher > l && last[k] == i) {
                higher = 0;
                for (int j = 0; j < n; j++) {
                    if (gate[k] >= i)
                        higher++;
                }
            }
        }
    }

    public void releaseCS(int i) {
        gate[i] = 0;
    }
}