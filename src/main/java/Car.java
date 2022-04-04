import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Car implements Runnable {
    private static int CARS_COUNT;
    //добавляем коунт лаунчеры на начало и конец гонки, и барьер для того чтобы собрать всех у старта
    private static CyclicBarrier cyclicBarrier;
    private static CountDownLatch countDownLatchGo;
    static CountDownLatch countDownLatchFinish;
    //для определения первого окончившего гонку, не уверена что отрабатывает ок,
    // тк если заканчивают 2 потока одновременна, сперва пишется, что закончил 1 и 2 участник а потом только что победил 1
    private static boolean firstFinish = false;
    private static Lock lock = new ReentrantLock();

    static {
        CARS_COUNT = 0;
        //берем из мейна эти 3 переменных
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
            //отнимаем у первого cdl
            countDownLatchGo.countDown();
            System.out.println(this.name + " готов");
            //собираем у старта всех
            cyclicBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }
        //отнимаем каждого финишируещего
        countDownLatchFinish.countDown();
        //определяем первый ли финишист?
        firstFinish(this.getName());
    }
//блокировка потоков при выведении выигравшего
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

