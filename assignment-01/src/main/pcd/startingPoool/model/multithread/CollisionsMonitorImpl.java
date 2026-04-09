package pcd.startingPoool.model.multithread;

import pcd.startingPoool.model.game.Ball;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CollisionsMonitorImpl implements CollisionMonitor {

    //private List<CollisionTask> bufferOfTasks;
    private final List<List<Ball>> bufferOfTasks;
    private final Lock lock;
    private final Condition allDone; //attesa da parte del Master
    private final Condition notEmpty;

    public CollisionsMonitorImpl(){
        this.bufferOfTasks = new ArrayList<>();
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.allDone = lock.newCondition();
    }

    @Override
    public void put(List<Ball> task) {
        try {
            lock.lock();
            bufferOfTasks.add(task);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Ball> get() {
        try {
            lock.lock();
            while (bufferOfTasks.isEmpty()){
                notEmpty.await();
            }
            List<Ball> task = bufferOfTasks.remove(bufferOfTasks.size() - 1);
            // si potrebbe fare che l'ultimo sveglia il master (Board in attesa di sapere se tutti i task sono stati eseguiti)
            if(bufferOfTasks.isEmpty()){
                allDone.signal();
            }
            return new ArrayList<>(task);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean allTasksDone() {
        try {
            lock.lock();
            while (! bufferOfTasks.isEmpty()){
                allDone.await();
            }
            return true;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
