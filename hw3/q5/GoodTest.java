package q5;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

public class SimpleTest {
	
	@Test
	public void myThreadTest() {
		ArrayList<ListSet> arr = new ArrayList<ListSet>();
		arr.add(new CoarseGrainedListSet());
		arr.add(new FineGrainedListSet());
		arr.add(new LockFreeListSet());
		boolean ret;
		int[] numthreads = {1, 2, 4, 8};
		for(ListSet list : arr) {
			ret = list.add(3);
			System.out.println(Boolean.toString(ret) + " " + list.toString());
			ret = list.add(5);
			System.out.println(Boolean.toString(ret) + " " + list.toString());
			ret = list.add(7);
			System.out.println(Boolean.toString(ret) + " " + list.toString());
			ret = list.add(1);
			System.out.println(Boolean.toString(ret) + " " + list.toString());
			ret = list.add(5);
			System.out.println(Boolean.toString(ret) + " " + list.toString());
			ret = list.remove(2);
			System.out.println(Boolean.toString(ret) + " " + list.toString());
			ret = list.remove(10);
			System.out.println(Boolean.toString(ret) + " " + list.toString());
			ret = list.remove(3);
			System.out.println(Boolean.toString(ret) + " " + list.toString());
			ret = list.contains(5);
			System.out.println(Boolean.toString(ret) + " " + list.toString());
			ret = list.contains(0);
			System.out.println(Boolean.toString(ret) + " " + list.toString());
			ret = list.contains(100);
			System.out.println(Boolean.toString(ret) + " " + list.toString());
			ret = list.remove(5);
			System.out.println(Boolean.toString(ret) + " " + list.toString());
			ret = list.remove(7);
			System.out.println(Boolean.toString(ret) + " " + list.toString());
			ret = list.remove(1);
			System.out.println(Boolean.toString(ret) + " " + list.toString());
			for(int i = 0; i < numthreads.length; i++) {
				System.out.println("");
				makeThread(list, 1000, numthreads[i]);
				System.out.println(Integer.toString(numthreads[i]) + " threads");
				System.out.println("Final out: " + list.toString());
			}
			System.out.println("");
		}
	}

    private void makeThread(ListSet list, int max, int numt) {
        Thread[] threads = new Thread[numt];
        int bot;
        for(int i = 0; i < numt; i++) {
        	bot = i * 10;
        	if(bot >= max) bot = 0;
            threads[i] = new Thread(new MyThread(bot, max, list));
        }
        for(int i = 0; i < numt; i++) {
        	threads[i].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private class MyThread implements Runnable {
        int begin;
        int end;
        ListSet list;
        MyThread(int begin, int end, ListSet list) {
            this.begin = begin;
            this.end = end;
            this.list = list;
        }
        @Override
        public void run() {
            for (int i = begin; i <= end; ++i) {
                list.add(i);
                //Assert.assertTrue(list.contains(i));
                list.remove(i);
            }
        }
    }
}
