package q6.stack;

public class LockStack implements MyStack {
    Node top;
    public LockStack() {
        top = null;
    }

    public synchronized boolean push(Integer value) {
        Node node = new Node(value);
        node.next = top;
        top = node;
        return true;
    }

    public Integer pop() throws EmptyStack {
        if (top == null) {
            throw new EmptyStack();
        } else {
            Node oldTop = top;
            top = top.next;
            return oldTop.value;
        }
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