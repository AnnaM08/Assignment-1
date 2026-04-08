package pcd.startingPoool.model.multithread;

import pcd.startingPoool.model.Latch;

import static pcd.startingPoool.model.game.Ball.resolveCollision;

//Worker
public class ColliderAgent extends Thread{
//Il Worker ha una istanza del monitor per chiamare la get e acquisire un nuovo task da compiere

    private CollisionMonitor bufferOfTasks;
    private Latch latch;

    public ColliderAgent(CollisionMonitor b, Latch l){
        this.bufferOfTasks = b;
        this.latch = l;
    }

    public void run(){
        while (true) {
            var tasks = bufferOfTasks.get();
            //System.out.println(tasks.size() + " "+ this.hashCode());
            //una volta preso il task lo esegue
            for(var task : tasks){
                resolveCollision(task.b1(), task.b2(), task.lastTouchedBy());
            }
            //Dopo aver eseguito i task il worker deve segnalarlo al latch specificando il numero di task eseguiti
            latch.countDown(tasks.size());
        }
    }


}
