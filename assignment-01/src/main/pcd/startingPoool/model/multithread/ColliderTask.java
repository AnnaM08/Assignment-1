package pcd.startingPoool.model.multithread;

import pcd.startingPoool.controller.BallType;
import pcd.startingPoool.model.game.Ball;

import java.util.List;
import java.util.concurrent.Callable;

import static pcd.startingPoool.model.game.Ball.resolveCollision;

//Worker
public class ColliderTask implements Callable<Boolean> {
//Il Worker ha una istanza del monitor per chiamare la get e acquisire un nuovo task da compiere

    private final List<Ball> balls;
    private final List<Ball> allBalls;

    public ColliderTask(List<Ball> l, List<Ball> allBalls) {
        this.balls = l;
        this.allBalls = allBalls;
    }

    public Boolean call() {

        for (int i = 0; i < balls.size(); i++) {
            int globalIndex = allBalls.indexOf(balls.get(i));
            for (int j =globalIndex + 1; j < allBalls.size(); j++) {
                //si verifica se le palline collidono allora sono allontanate secondo la normale
                var b1 = balls.get(i);
                var b2 = allBalls.get(j);
                if (b1.getType() != BallType.BASE) {
                    if (b2.getType() != BallType.BASE) {
                        resolveCollision(b1, b2, BallType.BASE); //implementazione sequenziale
                    } else {
                        resolveCollision(b2, b1, b1.getType());
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
        return true;
    }

}
