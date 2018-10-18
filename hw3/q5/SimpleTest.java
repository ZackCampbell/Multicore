package q5;

import org.junit.Assert;
import org.junit.Test;

public class SimpleTest {
	
	@Test
	public void myTest() {
		//CoarseGrainedListSet list = new CoarseGrainedListSet();
		//FineGrainedListSet list = new FineGrainedListSet();
		LockFreeListSet list = new LockFreeListSet();
		boolean ret;
		ret = list.add(3);
		System.out.println(Boolean.toString(ret) + " " + list.toString());
		ret = list.add(5);
		System.out.println(Boolean.toString(ret) + " " + list.toString());
		ret = list.add(7);
		System.out.println(Boolean.toString(ret) + " " + list.toString());
		ret = list.add(4);
		System.out.println(Boolean.toString(ret) + " " + list.toString());
		ret = list.add(5);
		System.out.println(Boolean.toString(ret) + " " + list.toString());
		ret = list.remove(2);
		System.out.println(Boolean.toString(ret) + " " + list.toString());
		ret = list.remove(3);
		System.out.println(Boolean.toString(ret) + " " + list.toString());
		ret = list.contains(5);
		System.out.println(Boolean.toString(ret) + " " + list.toString());
		ret = list.contains(0);
		System.out.println(Boolean.toString(ret) + " " + list.toString());
		ret = list.contains(100);
		System.out.println(Boolean.toString(ret) + " " + list.toString());
	}

    @Test
    public void testCoarseGrainedListSet() {
        CoarseGrainedListSet list = new CoarseGrainedListSet();
        makeThread(list);
        checkNode(0, 3000, list);
    }
    @Test
    public void testFineGrainedListSet() {
        FineGrainedListSet list = new FineGrainedListSet();
        makeThread(list);
        checkNode(0, 3000, list);
    }
    @Test
    public void testLockFreeListSet() {
        LockFreeListSet list = new LockFreeListSet();
        makeThread(list);
        checkNode(0, 3000, list);
    }
    private void makeThread(ListSet list) {
        Thread[] threads = new Thread[3];
        threads[0] = new Thread(new MyThread(0, 3000, list));
        threads[1] = new Thread(new MyThread(0, 3000, list));
        threads[2] = new Thread(new MyThread(1000, 3000, list));
        threads[1].start(); threads[0].start(); threads[2].start();
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void checkNode(int start, int end, ListSet list) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i <= end; ++i) {
            sb.append(i).append(",");
        }
        System.out.println(list.toString());
        Assert.assertEquals(list.toString(), sb.toString());
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
            }
        }
    }
}
