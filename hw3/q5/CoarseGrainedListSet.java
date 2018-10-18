package q5;

import java.util.concurrent.locks.ReentrantLock;

public class CoarseGrainedListSet implements ListSet {
	private Node head = null;
	private final ReentrantLock lock = new ReentrantLock();

    public CoarseGrainedListSet() {
    }

    public boolean add(int value) {
    	lock.lock();
    	try {
	    	Node toAdd = new Node(value);
	    	if(head == null) {
	    		head = new Node(value);
	    		return true;
	    	}
	    	if(head.value > value) {
	    		toAdd.next = head;
	    		head = toAdd;
	    		return true;
	    	}
	    	Node curr = head;
	    	Node nxt = head.next;
	    	while(true) {
	    		if(curr.value == value) {
	    			return false;
	    		}
	    		if(nxt == null && curr.value < value) {
	    			curr.next = new Node(value);
	    			return true;
	    		}
	    		if(curr.value < value && value < nxt.value) {
	    			toAdd.next = nxt;
	    			curr.next = toAdd;
	    			return true;
	    		}
	    		nxt = nxt.next;
	    		curr = curr.next;
	    	}
    	} finally {
    		lock.unlock();
    	}
    }

    public boolean remove(int value) {
    	lock.lock();
    	try {
	    	if(head == null) {
	    		return false;
	    	}
	    	if(head.value == value) {
	    		head = head.next;
	    		return true;
	    	}
	    	Node curr = head;
	    	while(curr.next != null) {
	    		if(curr.next.value > value) {
	    			return false;
	    		}
	    		if(curr.next.value == value) {
	    			curr.next = curr.next.next;
	    			return true;
	    		}
	    		curr = curr.next;
	    	}
	    	return false;
    	} finally {
    		lock.unlock();
    	}
    	
    }

    public boolean contains(int value) {
    	lock.lock();
    	try {
	    	Node curr = head;
	    	while(true) {
	    		if(curr == null) {
	    			return false;
	    		}
	    		if(curr.value == value) {
	    			return true;
	    		}
	    		if(curr.value > value) {
	    			return false;
	    		}
	    		curr = curr.next;
	    	}
    	} finally {
    		lock.unlock();
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
    
    /*
   return the string of list, if: 1 -> 2 -> 3, then return "1,2,3,"
   check simpleTest for more info
   */
    public String toString() {
	        String ret = "";
	        Node curr = head;
	        while(curr != null) {
	        	ret += Integer.toString(curr.value) + ",";
	        	curr = curr.next;
	        }
	        return ret;
    }
}
