package hw1.q6.ReentrantLock;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class PIncrement implements Runnable{
	
	static volatile int shared;
	static volatile ReentrantLock lock;
	
    public static int parallelIncrement(int c, int numThreads){
    	shared = c;
    	lock = new ReentrantLock();
    	
        ArrayList<Thread> Ts = new ArrayList<Thread>();
        
        for(int i = 0; i < numThreads; i++) {
        	if(numThreads != 7) {
        		Ts.add(new Thread(new PIncrement(i, 1200000 / numThreads)));
        	} else {
        		if(i < 4) {
        			Ts.add(new Thread(new PIncrement(i, (1200000 / numThreads) + 1)));
        		} else {
        			Ts.add(new Thread(new PIncrement(i, 1200000 / numThreads)));
        		}
        	}
        }
        for(Thread t : Ts) {
        	t.start();
        } 
        boolean busy = true;
        while(busy) {
        	busy = false;
        	for(Thread t : Ts) {
        		if(t.isAlive()) {
        			busy = true;
        			break;
        		}
        	}
        }
        return shared;
    }
    
    int pid;
	int tcounter;
    
    public PIncrement(int pid, int numincrements) {
    	this.tcounter = numincrements; 
    	this.pid = pid;
    }

	@Override
	public void run() {
		while(tcounter > 0) {
			lock.lock();
			shared++;
			lock.unlock();
			tcounter--;
		}
	}
}
