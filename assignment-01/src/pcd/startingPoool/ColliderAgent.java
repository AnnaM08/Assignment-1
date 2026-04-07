package pcd.startingPoool;

import static pcd.startingPoool.Ball.resolveCollision;

//Worker
public class ColliderAgent extends Thread{
//Il Worker ha una istanza del monitor per chiamare la get e acquisire un nuovo task da compiere

    private Monitor bufferOfTasks;

    public ColliderAgent(Monitor b){
        this.bufferOfTasks = b;
    }

    public void run(){
        while (true) {
            var task = bufferOfTasks.get();
            //una volta preso il task lo esegue
            resolveCollision(task.b1(), task.b2(), task.lastTouchedBy());
        }
    }

}
