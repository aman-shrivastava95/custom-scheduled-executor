package customExecutorService;


import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

interface CustomExecutorService {
    public void submit(Runnable r) ;
    public void shutdown() ;
}


class CustomExecutorServiceImpl implements CustomExecutorService{
    private final BlockingQueue<Runnable> taskQueue ;
    Thread[] workers ;
    boolean started ;

    public CustomExecutorServiceImpl(int maxWorkers){
        this.taskQueue =  new LinkedBlockingQueue<>() ;
        this.workers = new Thread[maxWorkers] ;
        this.started =  true ;

        //initiate the workers
        for(int i = 0; i < maxWorkers; i++){
            this.workers[i] =  new Thread(()->{
                while(started || (!started && !taskQueue.isEmpty())){
                    try {
                        Runnable task = taskQueue.take();
                        task.run();
                    } catch (InterruptedException e) {
                        break ;
                    }
                }
            }) ;
        }
        //start the workers
        for(Thread worker : workers){
            worker.start();
        }
    }

    @Override
    public void submit(Runnable r) {
        if(!started)
            return;
        taskQueue.add(r) ;
    }
    @Override
    public void shutdown()  {
        this.started = false;
        new Thread(()->{
            while(!this.taskQueue.isEmpty()){
                //wait
            }
            for(Thread worker: workers){
                worker.interrupt();
            }
        }).start();
    }
}
public class Main {
    public static void main(String[] args) throws InterruptedException {
        CustomExecutorService executorService  = new CustomExecutorServiceImpl(2) ;

        for(int i=0 ; i < 6 ; i++){
            AtomicInteger current = new AtomicInteger(i) ;
            executorService.submit(()->{
                System.out.println("executing task " + current.get() + " by "+ Thread.currentThread().getName());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        Thread.sleep(8000);
        for(int i =0 ;i<10 ;i++){
            executorService.submit(()->{
                System.out.println("executing final task after some delay");
            });
        }

        executorService.shutdown();

    }
}
