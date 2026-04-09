package pcd.startingPoool.model.multithread;

import pcd.startingPoool.controller.BallType;
import pcd.startingPoool.model.Latch;
import pcd.startingPoool.model.game.Ball;

import static pcd.startingPoool.model.game.Ball.resolveCollision;

//Worker
public class ColliderAgent2 extends Thread{
//Il Worker ha una istanza del monitor per chiamare la get e acquisire un nuovo task da compiere

    private CollisionMonitor bufferOfTasks;
    private Latch latch;

    public ColliderAgent2(CollisionMonitor b, Latch l){
        this.bufferOfTasks = b;
        this.latch = l;
    }

    public void run(){
        while (true) {
            var balls = bufferOfTasks.get();
            //System.out.println(tasks.size() + " thread id: "+ this.hashCode());

            for (int i = 0; i < balls.size() - 1; i++) {
                for (int j = i + 1; j < balls.size(); j++) {
                    //si verifica se le palline collidono allora sono allontanate secondo la normale
                    var b1 = balls.get(i);
                    var b2 = balls.get(j);
                    if (b1.getType() != BallType.BASE) {
                        if (b2.getType() != BallType.BASE) {
                            resolveCollision(b1, b2, Ball.LastTouchedBy.NONE); //implementazione sequenziale
                        } else {
                            resolveCollision(b2, b1, getLastTouchedBy(b1)) ;
                        }
                    } else {
                        if (b2.getType() != BallType.BASE) {
                          resolveCollision(b1, b2, getLastTouchedBy(b2));
                        } else {
                            resolveCollision(b1, b2, Ball.LastTouchedBy.NONE);
                        }
                    }
                }
            }
            latch.countDown(1);
        }


    }

    private Ball.LastTouchedBy getLastTouchedBy(Ball b) {
        if (b.getType() == BallType.PLAYER) {
            return Ball.LastTouchedBy.PLAYER;
        } else if (b.getType() == BallType.BOT) {
            return Ball.LastTouchedBy.BOT;
        }
        return null;
    }



}
