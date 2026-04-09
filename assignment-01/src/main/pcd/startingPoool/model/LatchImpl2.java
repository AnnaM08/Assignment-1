/*package pcd.startingPoool.model;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LatchImpl2 implements Latch {

    private int numTasks;
    private AtomicInteger numTasksExecuted;
    private Lock lock;
    private Condition allDone;

    public LatchImpl2(final int numTasks) {
        this.numTasks = numTasks;
        this.numTasksExecuted = new AtomicInteger(0);
        this.lock = new ReentrantLock();
        this.allDone = lock.newCondition();
    }

    @Override
    public void await() throws InterruptedException {
        lock.lock();
        try {
            // Usiamo l'AtomicInteger per controllare la condizione
            while (numTasksExecuted.get() < numTasks) {
                allDone.await();
            }
            // Reset per il prossimo frame
            numTasksExecuted.set(0);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void countDown(int numTasksDoneByAgent) {
        // L'incremento avviene fuori dal lock!
        // Più worker possono farlo simultaneamente senza aspettarsi.
        int totalSoFar = numTasksExecuted.addAndGet(numTasksDoneByAgent);

        // Solo chi raggiunge la quota finale acquisisce il lock per svegliare il Master
        if (totalSoFar == numTasks) {
            lock.lock();
            try {
                allDone.signal();
            } finally {
                lock.unlock();
            }
        }
    }
}*/
