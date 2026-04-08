package pcd.startingPoool.model.multithread;

import static pcd.startingPoool.model.game.Ball.resolveCollision;

//Worker
public class ColliderAgent extends Thread{
//Il Worker ha una istanza del monitor per chiamare la get e acquisire un nuovo task da compiere

    private final CollisionMonitor bufferOfTasks;


    public ColliderAgent(CollisionMonitor b){
        this.bufferOfTasks = b;
    }

    public void run(){
        try {
            while (true) {
                var tasks = bufferOfTasks.get();
                // System.out.println(tasks.size() + " "+ this.hashCode());
                //una volta preso il task lo esegue
                for (var task : tasks) {
                    resolveCollision(task.b1(), task.b2(), task.lastTouchedBy());
                }

            }
        } catch (Exception e) {

        }
    }


}
