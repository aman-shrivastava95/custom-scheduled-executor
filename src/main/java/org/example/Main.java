package org.example;

import java.util.Objects;
import java.util.concurrent.*;


class ScheduledTask implements Comparable<ScheduledTask> {
    Runnable task ;
    long executeAt ;

    public ScheduledTask(Runnable task, long executeAt){
        this.task  =  task ;
        this.executeAt = executeAt ;
    }

    public void run() {
        task.run();
    }

    @Override
    public int compareTo(ScheduledTask o) {
        return (int)(this.executeAt - o.executeAt) ;
    }
}
interface TaskScheduler {
    public boolean scheduleTask(ScheduledTask task) ;
    public void start() ;

    public void shutDown() ;
}

class TaskSchedulerImpl implements TaskScheduler {
    private  long maxTime =  -1L ;
    BlockingQueue<ScheduledTask> taskList ;
    ExecutorService executorService ;
    private volatile boolean started ;

    public TaskSchedulerImpl(){
        taskList =  new PriorityBlockingQueue<>() ;
        executorService = Executors.newCachedThreadPool() ;
        this.started = false ;
    }

    @Override
    public boolean scheduleTask(ScheduledTask task) {
        maxTime= Math.max(maxTime, task.executeAt) ;
        return taskList.offer(task) ;
    }

    @Override
    public void start() {
        this.started = true ;
        while(!executorService.isShutdown()){
            if(!taskList.isEmpty() && taskList.peek().executeAt <= System.currentTimeMillis()){
                executorService.submit(Objects.requireNonNull(taskList.poll()).task) ;
            }
        }
    }

    @Override
    public void shutDown() {
      while(this.taskList.isEmpty()){
          //busy waiting ;
      }
      while(!this.taskList.isEmpty()){
          try {
              Thread.sleep(500);
          } catch (InterruptedException e) {
              throw new RuntimeException(e);
          }
      }
      this.executorService.shutdown();
        try {
            this.executorService.awaitTermination(maxTime, TimeUnit.MILLISECONDS) ;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("closed scheduled executor service");
    }
}


public class Main {
    public static void main(String[] args) throws InterruptedException {
        TaskScheduler scheduler = new TaskSchedulerImpl() ;
        //Keeping separate thread for scheduling the tasks
        new Thread(()->{
            boolean res = false ;
            res = scheduler.scheduleTask(new ScheduledTask(()-> System.out.println("executed the task after 10 seconds"), System.currentTimeMillis() + 10000));
            res = scheduler.scheduleTask(new ScheduledTask(()-> System.out.println("executed the task after 5 seconds"), System.currentTimeMillis() + 5000));
            res = scheduler.scheduleTask(new ScheduledTask(()-> System.out.println("executed the task after 2 seconds"), System.currentTimeMillis() + 2000));

            scheduler.start();
        }).start();

        //keeping separate thread for shutting down the scheduled executor service
        new Thread(scheduler::shutDown).start();
    }
}
