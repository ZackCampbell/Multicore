package q5;
import java.util.concurrent.*;

public class Frequency implements Callable<Integer>{
    public static ExecutorService threadPool = Executors.newCachedThreadPool();
    int[] array;
    int x;
    public static void main(String[] args) {
        System.out.println("# of Threads: " + 3);
        System.out.println("Looking for: " + 0);
        int[] array = {1, 2, 2, 4, 2, 6, 7, 8, 9, 10};
        System.out.println(parallelFreq(2, array, 3));
    }

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
            int newLength = (A.length / numThreads) + 1;
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
            printArr(newArray);
            try {
                Future<Integer> f1 = threadPool.submit(new Frequency(newArray, x));
                result += f1.get();
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        return result;
    }

    public static void printArr(int[] arr) {
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
