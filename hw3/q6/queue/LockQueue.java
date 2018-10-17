package q6.queue;

import java.util.concurrent.locks.ReentrantLock;

public class LockQueue implements MyQueue {
    ReentrantLock lock1, lock2;

    public LockQueue() {
        lock1 = new ReentrantLock();
        lock2 = new ReentrantLock();
    }

    public boolean enq(Integer value) {
        // implement your enq method here
        return false;
    }

    public Integer deq() {
        // implement your deq method here
        return null;
    }

    protected class Node {
        public Integer value;
        public Node next;

        public Node(Integer x) {
            value = x;
            next = null;
        }
    }
}
