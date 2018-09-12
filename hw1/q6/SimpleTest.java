package q6;

import org.junit.Test;

import static org.junit.Assert.*;
//import org.junit.jupiter.api.Test;

public class SimpleTest {

	@Test
	public void TestTournament() {
		int res = q6.Tournament.PIncrement.parallelIncrement(0, 8);
		System.out.println("Result is " + res + ", expected result is 1200000.");
		assert(res == 1200000);
	}

	@Test
	public void TestAtomicInteger() {
    	int res = q6.AtomicInteger.PIncrement.parallelIncrement(0, 8);
		System.out.println("Result is " + res + ", expected result is 1200000.");
    	assert(res == 1200000);
	}

	@Test
	public void TestSynchronized() {
    	int res = q6.Synchronized.PIncrement.parallelIncrement(0, 8);
		System.out.println("Result is " + res + ", expected result is 1200000.");
    	assert(res == 1200000);
	}

	@Test
	public void TestReentrantLock() {
		int res = q6.ReentrantLock.PIncrement.parallelIncrement(0, 8);
		System.out.println("Result is " + res + ", expected result is 1200000.");
		assert(res == 1200000);
	}
}