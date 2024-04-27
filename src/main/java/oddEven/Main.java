package oddEven;
class OddEvenPrinter implements Runnable {
    private final boolean isOdd ;
    private final Monitor monitor ;
    public OddEvenPrinter(boolean isOdd, Monitor monitor){
        this.isOdd = isOdd ;
        this.monitor = monitor ;
    }
    @Override
    public void run() {
        int i = 1 ;
        if(!isOdd) i=2 ;
        for( ; i<=100; i+=2){
            monitor.waitForTurn(isOdd) ;
            System.out.printf("%d Thread[%s]\n", i, Thread.currentThread().getName() );
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            monitor.toggleTurn() ;
        }
    }
}

class Monitor {
    //true means odd turn, false means even turn
    private boolean oddTurn = true ;
    private Object lock = new Object();
    public void waitForTurn(boolean isOdd) {
        synchronized (lock) {
            while (isOdd != oddTurn) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void toggleTurn() {
        synchronized (lock) {
            oddTurn = !(oddTurn);
            lock.notifyAll();
        }
    }
}
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Monitor monitor = new Monitor();
        Thread odd = new Thread(new OddEvenPrinter(true, monitor), "Odd thread") ;
        Thread even  = new Thread(new OddEvenPrinter(false, monitor), "Even Thread") ;
        odd.start(); ;
        even.start(); ;
        odd.join();
        even.join();
    }
}
