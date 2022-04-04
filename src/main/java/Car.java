import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Car implements Runnable {
    private static int CARS_COUNT;
    private static CyclicBarrier cyclicBarrier;
    private static CountDownLatch countDownLatchGo;
    static CountDownLatch countDownLatchFinish;
    private static boolean firstFinish = false;
    private static Lock lock = new ReentrantLock();

    static {
        CARS_COUNT = 0;
        cyclicBarrier = Main.startCB;
        countDownLatchGo = Main.cdlGo;
        countDownLatchFinish = Main.cdlFinish;
    }

    private Race race;
    private int speed;
    private String name;

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public Car(Race race, int speed) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }

    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int) (Math.random() * 800));
            countDownLatchGo.countDown();
            System.out.println(this.name + " готов");
            cyclicBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this, i);
        }
        countDownLatchFinish.countDown();
        firstFinish(this.getName());

    }

    public static boolean firstFinish(String name) {
        if (!firstFinish) {
            try {
                lock.lock();
                firstFinish = true;
                System.out.println("Выиграл " + name);
            } finally {
                lock.unlock();
            }
            return true;
        } else {
            return false;
        }
    }
}

