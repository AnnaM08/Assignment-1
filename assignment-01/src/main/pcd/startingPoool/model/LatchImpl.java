package pcd.startingPoool.model;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LatchImpl implements  Latch{

    private int numTasks;
    private int numTasksExecuted;
    private Lock lock;
    private Condition allDone;

    public LatchImpl() {
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
                //System.out.println("Master in attesa che tutti i worker abbiano fatto countDown");
                allDone.await();
            }
            //resettato il latch
            numTasksExecuted = 0;
            //System.out.println("Master sbloccato");
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void countDown(int numTasksDoneByAgent) {
        try {
            lock.lock();
            numTasksExecuted += numTasksDoneByAgent;
            if (allArrived()) {
                allDone.signal();
                //System.out.println("Signal per sbloccare il Master");
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean allArrived() { return this.numTasks == this.numTasksExecuted; }

    @Override
    public void setNumberTasks(int nt){
        try {
            lock.lock();
            this.numTasks = nt;
        } finally {
            lock.unlock();
        }
    }

}