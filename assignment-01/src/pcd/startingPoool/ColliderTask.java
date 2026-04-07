package pcd.startingPoool;

import java.util.List;
import java.util.concurrent.Callable;

import static pcd.startingPoool.Ball.resolveCollision;

//Worker
public class ColliderTask implements Callable<Boolean> {
//Il Worker ha una istanza del monitor per chiamare la get e acquisire un nuovo task da compiere

    private final List<CollisionTask> listOfTasks;

    public ColliderTask(List<CollisionTask> listOfTasks) {
        this.listOfTasks = listOfTasks;
    }

    public Boolean call(){

            //una volta preso il task lo esegue
            for(var task : listOfTasks){
                resolveCollision(task.b1(), task.b2(), task.lastTouchedBy());
            }

            return true;


    }

}
