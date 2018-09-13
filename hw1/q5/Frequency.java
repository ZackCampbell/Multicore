package q5;
import java.util.concurrent.*;

public class Frequency implements Callable<Integer>{
    private static ExecutorService threadPool = Executors.newCachedThreadPool();
    private int[] array;
    private int x;

    public Frequency(int[] array, int x) {
        this.array = array;
        this.x = x;
    }

    // Calculate the frequency of x in A
    public static int parallelFreq(int x, int[] A, int numThreads) {
        int result = 0;
        int[] newArray;
        int index = 0;
        for (int i = 0; i < numThreads; i++) {
            int newLength;
            if (A.length % numThreads == 0)
                newLength = (A.length / numThreads);
            else
                newLength = (A.length / numThreads) + 1;
            newArray = new int[newLength];
            int newArrayIndex = 0;
            for (int j = index; j < index + newLength; j++) {
                if (j < A.length) {
                    newArray[newArrayIndex] = A[j];
                    newArrayIndex++;
                } else {
                    for (int k = newArrayIndex; k < newArray.length; k++) {
                        if (x == -1)
                            newArray[k] = 0;
                        else
                            newArray[k] = -1;
                    }
                    break;
                }
            }
            index += newLength;
            // Arrays are separated
            //printArr(newArray);
            try {
                Future<Integer> f1 = threadPool.submit(new Frequency(newArray, x));
                result += f1.get();

            } catch (Exception e) {
                System.err.println(e);
            }
        }
        return result;
    }

    private static void printArr(int[] arr) {
        System.out.print("[ ");
        for (int i : arr) {
            System.out.print(i + ", ");
        }
        System.out.println("]");
    }

    @Override
    public Integer call() {
        try {
            int count = 0;
            for (int i : array) {
                if (i == x) {
                    count++;
                }
            }
            return count;
        } catch (Exception e) {
            System.err.println(e);
            return 1;
        }
    }
}
