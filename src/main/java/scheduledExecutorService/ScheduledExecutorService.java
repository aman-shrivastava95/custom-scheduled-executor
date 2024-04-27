package scheduledExecutorService;

import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Getter
class ScheduledTask {
    private final Runnable runnable ;
    @Setter
    private Long scheduledTime ;
    private final int taskType ;
    private final Long period ;
    private final Long delay ;
    private final TimeUnit unit ;

    public ScheduledTask(Runnable runnable, Long scheduledTime, int taskType, Long period, Long delay, TimeUnit unit){
        this.runnable = runnable ;
        this.scheduledTime = scheduledTime ;
        this.taskType = taskType ;
        this.period = period ;
        this.delay = delay ;
        this.unit = unit ;
    }
}
public class ScheduledExecutorService {
    private final PriorityQueue<ScheduledTask> taskQueue ;
    private final Lock lock = new ReentrantLock() ;
    private final Condition newTaskAdded = lock.newCondition() ;
    private  final ThreadPoolExecutor workerExecutor ;

    public ScheduledExecutorService(int workerThreadSize){
        this.taskQueue = new PriorityQueue<>(Comparator.comparingLong(ScheduledTask::getScheduledTime)) ;
        workerExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(workerThreadSize) ;
    }

    public void start(){
        long timeToSleep = 0 ;
        while(true){
            lock.lock();
            try{
                //below two while loops are just letting the worker sleep
                while(taskQueue.isEmpty()) {
                    newTaskAdded.await();
                }
                while(!taskQueue.isEmpty()){
                    timeToSleep = taskQueue.peek().getScheduledTime() - System.currentTimeMillis() ;
                    if(timeToSleep <= 0){
                        break ;
                    }
                    newTaskAdded.await(timeToSleep, TimeUnit.MILLISECONDS);
                }
                ScheduledTask task = taskQueue.poll() ;
                switch (Objects.requireNonNull(task).getTaskType()){
                    case 1:
                        //this type of task will be executed once
                        workerExecutor.submit(task.getRunnable()) ;
                        break ;
                    case 2:
                        long newScheduledTime = System.currentTimeMillis() + task.getUnit().toMillis(task.getPeriod()) ;
                        workerExecutor.submit(task.getRunnable()) ;
                        task.setScheduledTime(newScheduledTime);
                        taskQueue.add(task) ;
                        break ;
                    case 3:
                        workerExecutor.submit(()-> {
                            Future<?> future = workerExecutor.submit(task.getRunnable());
                            try {
                                future.get();// will wait for the task to finish
                                long nextScheduledTime = System.currentTimeMillis() + task.getUnit().toMillis(task.getDelay()) ;
                                task.setScheduledTime(nextScheduledTime);
                                taskQueue.add(task) ;
                            } catch (InterruptedException | ExecutionException e) {
                                System.out.println("something went wrong while executing delay with initial delay");
                            }
                        });
                        break ;
                }
            }catch (Exception e){
                System.out.println("something went wrong in the start");
            }finally {
                lock.unlock();
            }
        }
    }
    /**
     * Creates and executes a one-shot action that becomes enabled after the given delay.
     */
    public void schedule(Runnable command, long delay, TimeUnit unit) {
        lock.lock();
        long scheduledTime =  System.currentTimeMillis() + unit.toMillis(delay) ;
        ScheduledTask task  = new ScheduledTask(command, scheduledTime, 1, null , null, null);
        taskQueue.add(task) ;
        newTaskAdded.signalAll();
        lock.unlock();
    }

    /**
     * Creates and executes a periodic action that becomes enabled first after the given initial delay, and
     * subsequently with the given period; that is executions will commence after initialDelay then
     * initialDelay+period, then initialDelay + 2 * period, and so on.
     */
    public void scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        lock.lock();
        long scheduledTime = System.currentTimeMillis() + unit.toMillis(initialDelay) ;
        ScheduledTask task = new ScheduledTask(command, scheduledTime, 2, period, null, unit);
        taskQueue.add(task);
        newTaskAdded.signalAll();
        lock.unlock();
    }

    /**
     * Creates and executes a periodic action that becomes enabled first after the given initial delay, and
     * subsequently with the given delay between the termination of one execution and the commencement of the next.
     */
    public void scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        lock.lock();
        long scheduledTime = System.currentTimeMillis() + unit.toMillis(initialDelay) ;
        ScheduledTask task = new ScheduledTask(command, scheduledTime, 3, null, delay, unit);
        taskQueue.add(task) ;
        newTaskAdded.signalAll();
        lock.unlock();
    }

    public static void main(String[] args) {
        ScheduledExecutorService schedulerService =  new ScheduledExecutorService(10) ;
        schedulerService.schedule(getRunnableTask("task 1"), 1, TimeUnit.SECONDS);
        schedulerService.scheduleAtFixedRate(getRunnableTask("task 2"), 1, 2 , TimeUnit.SECONDS);
        schedulerService.scheduleWithFixedDelay(getRunnableTask("task 3"), 1,2, TimeUnit.SECONDS);
        schedulerService.scheduleAtFixedRate(getRunnableTask("task 4"),1,2, TimeUnit.SECONDS);
        schedulerService.start();
    }

    private static Runnable getRunnableTask(String s){
        return () -> {
            System.out.println(s + "started at " + System.currentTimeMillis() / 1000);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }
        } ;
    }
}
