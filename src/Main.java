import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    static final int maxWaitingCalls = 100;
    static final int callsPerSec = 60;
    static final int operators = 10;
    static Queue<Call> queue = new ArrayBlockingQueue<Call>(maxWaitingCalls);

    public static void main(String[] args) throws InterruptedException {
        Runnable ats = (() -> {
            for (int i = 0; i < callsPerSec; i++) {
                Call call = new Call((int) Math.random() * 1000 + 3000);
                queue.add(call);
                System.out.println("Звонок поставлен в очередь.");
            }
        });

        Runnable operator = (() -> {
            while (true) {
                Call call = queue.poll();
                if (call == null) {
                    return;
                }
                try {
                    Thread.sleep(call.duration);
                    System.out.println("Оператор обработал звонок.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        ExecutorService pool = Executors.newFixedThreadPool(operators);
        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < operators; i++) {
            tasks.add(operator);
            pool.submit(operator);
        }

        tasks.add(ats);
        pool.submit(ats);


        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.MINUTES);


    }

}
