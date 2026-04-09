package pcd.startingPoool.model.multithread;

import pcd.startingPoool.controller.BallType;
import pcd.startingPoool.model.game.Ball;

import java.util.List;
import java.util.concurrent.Callable;

import static pcd.startingPoool.model.game.Ball.resolveCollision;

//Worker
public class ColliderTask2 implements Callable<Boolean> {
//Il Worker ha una istanza del monitor per chiamare la get e acquisire un nuovo task da compiere

    private final List<Ball> balls;
    private final List<Ball> allBalls;

    public ColliderTask2(List<Ball> l, List<Ball> allBalls) {
        this.balls = l;
        this.allBalls = allBalls;
    }

    public Boolean call() {

        for (int i = 0; i < balls.size() - 1; i++) {
            for (int j = 0; j < allBalls.size(); j++) {
                //si verifica se le palline collidono allora sono allontanate secondo la normale
                var b1 = balls.get(i);
                var b2 = allBalls.get(j);
                if (b1.getType() != BallType.BASE) {
                    if (b2.getType() != BallType.BASE) {
                        resolveCollision(b1, b2, Ball.LastTouchedBy.NONE); //implementazione sequenziale
                    } else {
                        resolveCollision(b2, b1, getLastTouchedBy(b1));
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
        return true;
    }
    private Ball.LastTouchedBy getLastTouchedBy (Ball b){
        if (b.getType() == BallType.PLAYER) {
            return Ball.LastTouchedBy.PLAYER;
        } else if (b.getType() == BallType.BOT) {
            return Ball.LastTouchedBy.BOT;
        }
        return null;
    }

}
