package q2;
import org.junit.Assert;
import org.junit.Test;

public class SimpleTest {

    private static final int OPERATIONS = 120000;

//    @Test
//    public void testFischer() {
//        System.out.println("Fischer's Algorithm ------ \n");
//        for (int i = 1; i <= 8; i *= 2) {
//            int result = q2.a.PIncrement.parallelIncrement(0, i);
//            System.out.println(result);
//            if (result == OPERATIONS) {
//                System.out.println("Succeeded");
//            } else {
//                System.out.println("Failed");
//            }
//        }
//    }

    @Test
    public void testLamport() {
        System.out.println("Lamport's Fast Mutex ------ \n");
        int succeedCounter = 0;
        for (int i = 1; i <= 8; i *= 2) {
            int result = q2.b.PIncrement.parallelIncrement(0, i);
            System.out.println("Test with " + i + " threads yields " + result);
            if (result == OPERATIONS) {
                succeedCounter++;
                System.out.println("Succeeded");
            } else {
                System.out.println("Failed");
            }
        }
        Assert.assertEquals(succeedCounter, 4);
    }
//
//    @Test
//    public void testAnderson() {
//        int result = q2.c.PIncrement.parallelIncrement(0, 8);
//        System.out.println(result);
//        Assert.assertEquals(result, OPERATIONS);
//    }

}
