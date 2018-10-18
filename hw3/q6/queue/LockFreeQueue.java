package q6.queue;

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue implements MyQueue {
    AtomicReference<Node> head, tail;

    public LockFreeQueue() {
        Node n = new Node(null);
        head = new AtomicReference<>(n);
        tail = head;
    }

    public boolean enq(Integer value) {
        Node node = new Node(value);
        while (true) {
            Node currTail = tail.get();
            Node currNext = currTail.next.get();
            if (currTail == tail.get()) {
                if (currNext.next == null) {
                    if (currTail.next.compareAndSet(null, node)) {
                        tail.compareAndSet(currTail, node);
                        return true;
                    }
                } else {
                    tail.compareAndSet(currTail, currNext);
                }
            }
        }
    }

    public Integer deq() {
        while (true) {
            Node currHead = head.get();
            Node currTail = tail.get();
            Node headNext = currHead.next.get();
            if (currHead == head.get()) {
                if (currHead == currTail) {
                    if (headNext == null) {
                        return null;
                    }
                    tail.compareAndSet(currTail, headNext);
                } else {
                    Integer val = headNext.value;
                    if (head.compareAndSet(currHead, headNext)) {
                        return currHead.value;
                    }
                }
            }
        }
    }

    protected class Node {
        public Integer value;
        public AtomicReference<Node> next;

        public Node(Integer x) {
            value = x;
            next = new AtomicReference<>(null);
        }
    }
}