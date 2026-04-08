package pcd.startingPoool.model;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LatchImpl implements  Latch{

    private int numTasks;
    private int numTasksExecuted;
    private Lock lock;
    private Condition allDone;

    public LatchImpl(int nt) {
        this.numTasks = nt;
        this.numTasksExecuted = 0;
        this.lock = new ReentrantLock();
        this.allDone = lock.newCondition();
    }

    @Override
    public void await() throws InterruptedException {
        //master esegue la await per aspettare che tutti i worker abbiano fatto countDown
        try {
            lock.lock();
            while (!allArrived()) {
                System.out.println("Master in attesa che tutti i worker abbiano fatto countDown");
                allDone.await();
            }
            System.out.println("Master sbloccato");
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void countDown() {
        try {
            lock.lock();
            numTasksExecuted++;
            if (allArrived()) {
                allDone.signal();
                System.out.println("Signal per sbloccare il Master");
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean allArrived() { return this.numTasks == this.numTasksExecuted; }


}