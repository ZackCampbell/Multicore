package hw1.q6;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
//import org.junit.jupiter.api.Test;

public class SimpleTest {
	
	@Test
	public void TimingAll() {
		int startVal = 0;
		long startTime;
		long endTime;
		long elapsedTime;
		int res;
		String status = "";
		ArrayList<String> methodTypes = new ArrayList<String>();
		methodTypes.add("Tournament"); methodTypes.add("Atomic"); 
		methodTypes.add("Synchronized"); methodTypes.add("Reentrant");
		for(String type : methodTypes) {
			for(int i = 1; i <= 8; i++) {
				startTime = System.nanoTime();
				if(type == "Tournament") {
					res = q6.Tournament.PIncrement.parallelIncrement(startVal, i);
				} else if(type == "Atomic") {
					res = hw1.q6.AtomicInteger.PIncrement.parallelIncrement(startVal, i);
				} else if(type == "Synchronized") {
					res = q6.Synchronized.PIncrement.parallelIncrement(startVal, i);
				} else {
					res = hw1.q6.ReentrantLock.PIncrement.parallelIncrement(startVal, i);
				}
				endTime = System.nanoTime();
				elapsedTime = endTime - startTime;
				if(res == (startVal + 1200000)) {
					status = "succeded";
				} else {
					status = "failed";
				}
				System.out.println(type + " with starting value " + Integer.toString(startVal) +
						" and " + Integer.toString(i) + " threads " + status + " in " +
						Double.toString((double)elapsedTime / 1000000000.0) + "s.");
			}
		}
	}

//	@Test
//	public void TestTournament() {
//		int res = q6.Tournament.PIncrement.parallelIncrement(0, 8);
//		System.out.println("Result is " + res + ", expected result is 1200000.");
//		assert(res == 1200000);
//	}
//
//	@Test
//	public void TestAtomicInteger() {
//    	int res = q6.AtomicInteger.PIncrement.parallelIncrement(0, 8);
//		System.out.println("Result is " + res + ", expected result is 1200000.");
//    	assert(res == 1200000);
//	}
//
//	@Test
//	public void TestSynchronized() {
//    	int res = q6.Synchronized.PIncrement.parallelIncrement(0, 8);
//		System.out.println("Result is " + res + ", expected result is 1200000.");
//    	assert(res == 1200000);
//	}
//
//	@Test
//	public void TestReentrantLock() {
//		int res = q6.ReentrantLock.PIncrement.parallelIncrement(0, 8);
//		System.out.println("Result is " + res + ", expected result is 1200000.");
//		assert(res == 1200000);
//	}
}