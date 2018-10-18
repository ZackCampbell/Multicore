package q5;

import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedListSet implements ListSet {

	private Node head = new Node(null);
	private Node tail = new Node(null);

    public FineGrainedListSet() {
    	head.next = tail;
    	head.head = true;
    	tail.head = false;
    }

    public boolean add(int value) {
        Node toAdd = new Node(value);
        while(true) {
	        Node prev = head;
	        Node succ = head.next;
	        while(succ.compareTo(toAdd) < 0) {
	        	prev = succ;
	        	succ = prev.next;
	        }
	        prev.lock();
	        succ.lock();
	        if(prev.isDeleted || succ.isDeleted || (prev.next != succ)) {
	        	prev.unlock();
	        	succ.unlock();
	        	continue;
	        }
	        if(succ.compareTo(toAdd) == 0) {
	        	prev.unlock();
	        	succ.unlock();
	        	return false;
	        }
	        toAdd.next = succ;
	        prev.next = toAdd;
	        prev.unlock();
	        succ.unlock();
	        return true;
        }
    }

    public boolean remove(int value) {
        Node toRemove = new Node(value);
        while(true) {
	        Node prev = head;
	        Node succ = head.next;
	        while(succ.compareTo(toRemove) < 0) {
	        	prev = succ;
	        	succ = prev.next;
	        }
	        prev.lock();
	        succ.lock();
	        if(prev.isDeleted || succ.isDeleted || (prev.next != succ)) {
	        	prev.unlock();
	        	succ.unlock();
	        	continue;
	        }
	        if(succ.compareTo(toRemove) == 0) {
	        	succ.isDeleted = true;
	        	prev.next = succ.next;
	        	prev.unlock();
	        	succ.unlock();
	        	return true;
	        }
	        prev.unlock();
	        succ.unlock();
	        return false;
        }
    }

    public boolean contains(int value) {
    	Node toCompare = new Node(value);
        Node curr = head;
        while(curr.compareTo(tail) != 0) {
        	if(curr.compareTo(toCompare) == 0) {
        		if(curr.isDeleted) {
        			return false;
        		} else {
        			return true;
        		}
        	}
        	if(curr.compareTo(toCompare) > 0) {
        		return false;
        	}
        	curr = curr.next;
        }
        return false;
    }

    protected class Node implements Comparable<Node> {
        public Integer value;
        public Node next;
        public ReentrantLock lock;
        public boolean isDeleted;
        public boolean head;

        public Node(Integer x) {
            value = x;
            next = null;
            lock = new ReentrantLock();
            isDeleted = false;
            head = false;
        }
        
        public void lock() {
        	this.lock.lock();
        }
        
        public void unlock() {
        	this.lock.unlock();
        }

		@Override
		public int compareTo(Node o) {
			boolean firsthead = false;
			boolean firsttail = false;
			boolean secondhead = false;
			boolean secondtail = false;
			if(this.value == null) {
				firsthead = this.head;
				firsttail = !this.head;
			}
			if(o.value == null) {
				secondhead = o.head;
				secondtail = !o.head;
			}
			if(firsttail && secondtail) {
				return 0;
			}
			if(firsthead && secondhead) {
				return 0;
			}
			if(firsthead && secondtail) {
				return -1;
			}
			if(firsttail && secondhead) {
				return 1;
			}
			if(firsthead || secondtail) {
				return -1;
			}
			if(firsttail || secondhead) {
				return 1;
			}
			if(this.value == o.value) {
				return 0;
			}
			if(this.value < o.value) {
				return -1;
			}
			if(this.value > o.value) {
				return 1;
			}
			return 0;
		}
    }
    
    /*
   return the string of list, if: 1 -> 2 -> 3, then return "1,2,3,"
   check simpleTest for more info
   */
    public String toString() {
        String ret = "";
        Node curr = head.next;
        while(curr.compareTo(tail) != 0) {
        	if(!curr.isDeleted) {
	        	ret += Integer.toString(curr.value) + ",";
	        	curr = curr.next;
        	}
        }
        return ret;
    }
}