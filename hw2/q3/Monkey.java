package q3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Monkey {
	// declare the variables here
	final static Lock lock = new ReentrantLock();
	final static Condition safeToClimb = lock.newCondition();
	
	volatile static int numMonkeys = 0;
	volatile static int dirMonkeys = 0;
	volatile static boolean kongCrossing = false;
	volatile static long kongID = -1;	
	
    public Monkey() {
    }
    
	// A monkey calls the method when it arrives at the river bank and wants to climb
	// the rope in the specified direction (0 or 1); Kong’s direction is -1.
	// The method blocks a monkey until it is allowed to climb the rope.
    public void ClimbRope(int direction) throws InterruptedException {
    	lock.lock();
    	boolean isKong = false;
    	if(direction == -1)
    		isKong = true;
    	if(isKong) {
    		kongCrossing = true;
    		kongID = Thread.currentThread().getId();
    		while(numMonkeys != 0) safeToClimb.await();
    		++numMonkeys;
    	} else {
    		while((dirMonkeys != direction && numMonkeys != 0) || (dirMonkeys == direction && numMonkeys == 3) || kongCrossing) safeToClimb.await();
    		++numMonkeys;
    		if(dirMonkeys != direction) {
    			dirMonkeys = direction;
    		}
    	}
    	lock.unlock();
    }

	// After crossing the river, every monkey calls this method which
	// allows other monkeys to climb the rope.
    public void LeaveRope() {
    	lock.lock();
    	if(kongCrossing) {
    		if(kongID == Thread.currentThread().getId()) {
	    		kongCrossing = false;
	    		kongID = -1;
    		}
    	}
    	--numMonkeys;
    	safeToClimb.signalAll();
    	lock.unlock();
    }

    /**
     * Returns the number of monkeys on the rope currently for test purpose.
     *
     * @return the number of monkeys on the rope
     *
     * Positive Test Cases:
     * case 1: when normal monkey (0 and 1) is on the rope, this value should <= 3, >= 0
     * case 2: when Kong is on the rope, this value should be 1
     */
    public int getNumMonkeysOnRope() {
        return numMonkeys;
    }

}
