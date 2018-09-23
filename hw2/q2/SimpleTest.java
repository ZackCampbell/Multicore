package q2;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class SimpleTest {

    private static final int OPERATIONS = 120000;

    @Test
    public void testAll() {
        int startVal = 0;
        long startTime;
        long endTime;
        long elapsedTime;
        int res;
        String status = "";
        ArrayList<String> methodTypes = new ArrayList<String>();
        methodTypes.add("Fischer"); methodTypes.add("Lamport");
        methodTypes.add("Anderson");
        for(String type : methodTypes) {
            for(int i = 1; i <= 8; i *= 2) {
                startTime = System.nanoTime();
                if(type.equals("Fischer")) {
                    res = q2.a.PIncrement.parallelIncrement(0, i);
                } else if(type.equals("Lamport")) {
                    res = q2.b.PIncrement.parallelIncrement(0, i);
                } else {
                    res = q2.c.PIncrement.parallelIncrement(0, i);
                }
                endTime = System.nanoTime();
                elapsedTime = endTime - startTime;
                if(res == (startVal + OPERATIONS)) {
                    status = "succeeded";
                } else {
                    status = "failed";
                }
                System.out.println(type + " with starting value " + Integer.toString(startVal) +
                        " and " + Integer.toString(i) + " threads " + status + " in " +
                        Double.toString((double)elapsedTime / 1000000000.0) + "s.");
            }
        }
    }

//    @Test
//    public void testFischer() {
//        System.out.println("Fischer's Algorithm ------ \n");
//        int succeedCounter = 0;
//        for (int i = 1; i <= 8; i *= 2) {
//            int result = q2.a.PIncrement.parallelIncrement(0, i);
//            System.out.println("Test with " + i + " threads yields " + result);
//            if (result == OPERATIONS) {
//                succeedCounter++;
//                System.out.println("Succeeded");
//            } else {
//                System.out.println("Failed");
//            }
//        }
//        Assert.assertEquals(succeedCounter, 4);
//    }
//
//    @Test
//    public void testLamport() {
//        System.out.println("Lamport's Fast Mutex ------ \n");
//        int succeedCounter = 0;
//        for (int i = 1; i <= 8; i *= 2) {
//            int result = q2.b.PIncrement.parallelIncrement(0, i);
//            System.out.println("Test with " + i + " threads yields " + result);
//            if (result == OPERATIONS) {
//                succeedCounter++;
//                System.out.println("Succeeded");
//            } else {
//                System.out.println("Failed");
//            }
//        }
//        Assert.assertEquals(succeedCounter, 4);
//    }
//
//    @Test
//    public void testAnderson() {
//        System.out.println("Anderson's Lock ------ \n");
//        int succeedCounter = 0;
//        for (int i = 1; i <= 8; i *= 2) {
//            int result = q2.c.PIncrement.parallelIncrement(0, i);
//            System.out.println("Test with " + i + " threads yields " + result);
//            if (result == OPERATIONS) {
//                succeedCounter++;
//                System.out.println("Succeeded");
//            } else {
//                System.out.println("Failed");
//            }
//        }
//        Assert.assertEquals(succeedCounter, 4);
//    }

}
