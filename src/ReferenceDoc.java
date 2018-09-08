import java.util.concurrent.*;

public class ReferenceDoc {
    public static void main(String[] args) {
        System.out.println("Hello World");
        //  Test various algorithms here

        // For HelloWorldThread
        // HelloWorldThread t = new HelloWorldThread();
        // t.start();

        // For RunnableExampleBar
        // RunnableExampleBar r1 = new RunnableExampleBar("Romeo");
        // Thread t1 = new Thread(r1);
        // t1.start();
        // RunnableExampleBar r2 = new RunnableExampleBar("Romeo");
        // Thread t2 = new Thread(r2);
        // t2.start();

        // For Fibonacci using join
//        Fibonacci f1 = new Fibonacci(8);
//        f1.start();
//        try {
//            f1.join();
//        } catch (InterruptedException e) {}
//        System.out.println("Answer is " + f1.getResult());

        // For Fibonacci using Callable
//        try {
//            ExecutorService es = Executors.newSingleThreadExecutor();
//            Fibonacci2 f = new Fibonacci2(8);
//            Future<Integer> f1 = es.submit(f);
//            System.out.println("Answer is: " + f1.get());
//            es.shutdown();
//            f.threadPool.shutdown();
//        } catch (Exception e) {
//            System.err.println(e);
//        }

        // For Fibonacci using Fork
//        int processors = Runtime.getRuntime().availableProcessors();
//        System.out.println("Number of processors: " + processors);
//        Fibonacci3 f = new Fibonacci3(8);
//        ForkJoinPool pool = new ForkJoinPool(processors);
//        int result = pool.invoke(f);
//        System.out.println("Result: " + result);
    }
}

/**
 * Fibonacci using "join"
 */
class Fibonacci extends Thread{
    int n;
    int result;
    public Fibonacci(int n) {
        this.n = n;
    }
    public void run() {
        if (n == 0 || n == 1) result = 1;
        else {
            Fibonacci f1 = new Fibonacci(n-1);
            Fibonacci f2 = new Fibonacci(n-2);
            f1.start();
            f2.start();
            try {
                f1.join();
                f2.join();
            } catch (InterruptedException e) {}
            result = f1.getResult() + f2.getResult();
        }
    }
    public int getResult() {
        return result;
    }
}

/**
 * Fibonacci using Callable
 */
class Fibonacci2 implements Callable<Integer> {
    public static ExecutorService threadPool = Executors.newCachedThreadPool();
    int n;
    public Fibonacci2(int n) {
        this.n = n;
    }
    public Integer call() {
        try {
            if((n == 0)||(n == 1 )) return 1;
            else {
                Future<Integer> f1 = threadPool.submit(new Fibonacci2(n-1));
                Future<Integer> f2 = threadPool.submit(new Fibonacci2(n-2));
                return f1.get() + f2.get();
            }
        } catch (Exception e) {
            System.err.println(e);
            return 1;
        }
    }
}

/**
 * Fibonacci using Fork
 */
class Fibonacci3 extends RecursiveTask<Integer> {
    final int n;
    public Fibonacci3(int n) {
        this.n = n;
    }
    protected Integer compute() {
        if (n == 0 || n == 1) return 1;
        Fibonacci3 f1 = new Fibonacci3(n-1);
        f1.fork();
        Fibonacci3 f2 = new Fibonacci3(n-2);
        return f2.compute() + f1.join();
    }
}

class HelloWorldThread extends Thread {
    public void run() {
        System.out.println("Hello World");
    }
}

/**
 * Example using Runnable
 */
class RunnableExample {
    String name;
    public RunnableExample(String s) {
        name = s;
    }
    public void setName(String s) {
        name = s;
    }
    public String getName() {
        return name;
    }
}
class RunnableExampleBar extends RunnableExample implements Runnable {
    public RunnableExampleBar(String s) {
        super(s);
    }
    public void run() {
        for (int i = 0; i < 10; i++)
            System.out.println(getName() + " Hello World");
    }
}