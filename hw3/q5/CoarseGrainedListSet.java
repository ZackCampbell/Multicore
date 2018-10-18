package q5;

import java.util.concurrent.locks.ReentrantLock;

public class CoarseGrainedListSet implements ListSet {
	private volatile Node head;
	private final ReentrantLock lock;

    public CoarseGrainedListSet() {
    	lock = new ReentrantLock();
    	head = null;
    }

    public boolean add(int value) {
    	lock.lock();
    	try {
	    	Node toAdd = new Node(value);
	    	// only node
	    	if(head == null) {
	    		head = toAdd;
	    		return true;
	    	}
	    	// first node
	    	if(head.value > toAdd.value) {
	    		toAdd.next = head;
	    		head = toAdd;
	    		return true;
	    	}
	    	Node curr = head;
	    	while(true) {
	    		// already exists
	    		if(curr.value == toAdd.value) {
	    			return false;
	    		} 
	    		if(curr.value < toAdd.value) {
	    			// last node
	    			if(curr.next == null) {
	    				curr.next = toAdd;
	    				return true;
	    			}
	    			// somewhere in the middle
	    			if(curr.next.value > toAdd.value) {
	    				toAdd.next = curr.next;
	    				curr.next = toAdd;
	    				return true;
	    			}
	    		}
	    		if(curr.next == null) {
	    			return false;
	    		}
	    		curr = curr.next;
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
    		System.out.println(value);
    		System.out.println(toString());
    		return false;
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
        public volatile Integer value;
        public volatile Node next;

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
        try {
	        String ret = "";
	        Node curr = head;
	        while(curr != null) {
	        	ret += Integer.toString(curr.value) + ",";
	        	curr = curr.next;
	        }
	        return ret;
        } finally {
        }
    }
}
