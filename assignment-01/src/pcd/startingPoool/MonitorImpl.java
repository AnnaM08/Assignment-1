package pcd.startingPoool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorImpl implements Monitor{

    private List<CollisionTask> bufferOfTasks;
    private Lock lock;
    private Condition allDone; //attesa da parte del Master
    private Condition notEmpty;

    public MonitorImpl(){
        this.bufferOfTasks = new ArrayList<>();
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.allDone = lock.newCondition();
    }

    @Override
    public void put(CollisionTask task) {
        try {
            lock.lock();
            bufferOfTasks.add(task);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public CollisionTask get() {
        try {
            lock.lock();
            while (bufferOfTasks.isEmpty()){
                notEmpty.await();
            }
            int lastItemPosition = bufferOfTasks.size() - 1;
            CollisionTask task = bufferOfTasks.get(lastItemPosition);
            bufferOfTasks.remove(lastItemPosition);
            // si potrebbe fare che l'ultimo sveglia il master (Board in attesa di sapere se tutti i task sono stati eseguiti)
            if(bufferOfTasks.isEmpty()){
                allDone.signal();
            }
            return task;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean bufferIsEmpty() {
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
