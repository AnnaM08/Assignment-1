package pcd.startingPoool.model.multithread;

import pcd.startingPoool.controller.BallType;
import pcd.startingPoool.model.Latch;
import pcd.startingPoool.model.game.Ball;
import java.util.List;

import static pcd.startingPoool.model.game.Ball.resolveCollision;

//Worker
public class ColliderAgent extends Thread{
//Il Worker ha una istanza del monitor per chiamare la get e acquisire un nuovo task da compiere

    private CollisionMonitor bufferOfTasks;
    private Latch latch;
    private final List<Ball> allBalls;

    public ColliderAgent(CollisionMonitor b, Latch l, List<Ball> allBalls){
        this.bufferOfTasks = b;
        this.latch = l;
        this.allBalls = allBalls;
    }

    public void run(){
        while (true) {
            var balls = bufferOfTasks.get();
            //System.out.println(tasks.size() + " thread id: "+ this.hashCode());

            for (int i = 0; i < balls.size() - 1; i++) {
                for (int j = 0; j < allBalls.size(); j++) {
                    //si verifica se le palline collidono allora sono allontanate secondo la normale
                    var b1 = balls.get(i);
                    var b2 = allBalls.get(j);
                    if (b1.getType() != BallType.BASE) {
                        if (b2.getType() != BallType.BASE) {
                            resolveCollision(b1, b2, BallType.BASE); //implementazione sequenziale
                        } else {
                            resolveCollision(b2, b1, b1.getType()) ;
                        }
                    } else {
                        if (b2.getType() != BallType.BASE) {
                          resolveCollision(b1, b2, b2.getType());
                        } else {
                            resolveCollision(b1, b2, BallType.BASE);
                        }
                    }
                }
            }
            latch.countDown(1);
        }


    }

}
