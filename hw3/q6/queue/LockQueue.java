package q6.queue;

import java.util.concurrent.locks.ReentrantLock;

public class LockQueue implements MyQueue {
    ReentrantLock enqLock, deqLock;
    Node head, tail;
    public LockQueue() {
        enqLock = new ReentrantLock();
        deqLock = new ReentrantLock();
        head = new Node(null);
        tail = new Node(null);
    }

    public boolean enq(Integer value) {
        if (value == null) throw new NullPointerException();
        enqLock.lock();
        try {
            Node e = new Node(value);
            tail.next = e;
            tail = e;
        } finally {
            enqLock.unlock();
        }
        return false;
    }

    public Integer deq() {
        Integer result;
        deqLock.lock();
        try {
            if (head.next == null) {
                return null;
            }
            result = head.next.value;
            head = head.next;
        } finally {
            deqLock.unlock();
        }
        return result;
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