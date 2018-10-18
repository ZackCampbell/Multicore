package q5;

import java.util.concurrent.atomic.AtomicMarkableReference;

import q5.FineGrainedListSet.Node;

public class LockFreeListSet implements ListSet {

	private Node head = new Node(null);
	private Node tail = new Node(null);

    public LockFreeListSet() {
    	head.atom.set(tail, head.atom.isMarked());
    	head.head = true;
    	tail.head = false;
    }

    public boolean add(int value) {
        Node toAdd = new Node(value);
        while(true) {
	        Node prev = head;
	        Node succ = head.atom.getReference();
	        while(succ.compareTo(toAdd) < 0) {
	        	prev = succ;
	        	succ = prev.atom.getReference();
	        }
	        if(succ.compareTo(toAdd) == 0) {
	        	if(succ.atom.isMarked()) {
	        		continue;
	        	}
	        	return false;
	        }
	        toAdd.atom.set(succ, toAdd.atom.isMarked());
	        if(prev.atom.compareAndSet(succ, toAdd, false, false)) {
	        	return true;
	        }
        }
    }

    public boolean remove(int value) {
        Node toRemove = new Node(value);
        while(true) {
	        Node prev = head;
	        Node succ = head.atom.getReference();
	        while(succ.compareTo(toRemove) < 0) {
	        	prev = succ;
	        	succ = prev.atom.getReference();
	        }
	        if(succ.compareTo(toRemove) != 0) {
	        	return false;
	        }
	        if(!succ.atom.compareAndSet(succ.atom.getReference(), succ.atom.getReference(), false, true)) {
	        	return false;
	        }
	        if(prev.atom.compareAndSet(succ, succ.atom.getReference(), false, false)) {
	        	return true;
	        }
        }
    }

    public boolean contains(int value) {
    	Node toCompare = new Node(value);
        Node curr = head;
        while(curr.compareTo(tail) != 0) {
        	if(curr.compareTo(toCompare) == 0) {
        		if(curr.atom.isMarked()) {
        			return false;
        		} else {
        			return true;
        		}
        	}
        	if(curr.compareTo(toCompare) > 0) {
        		return false;
        	}
        	curr = curr.atom.getReference();
        }
        return false;
    }

    protected class Node implements Comparable<Node>{
        public Integer value;
        public boolean head;
        public AtomicMarkableReference<Node> atom;

        public Node(Integer x) {
            value = x;
            head = false;
            atom = new AtomicMarkableReference<Node>(null, false);
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
        Node curr = head.atom.getReference();
        while(curr.compareTo(tail) != 0) {
        	if(!curr.atom.isMarked()) {
	        	ret += Integer.toString(curr.value) + ",";
	        	curr = curr.atom.getReference();
        	}
        }
        return ret;
    }
}
