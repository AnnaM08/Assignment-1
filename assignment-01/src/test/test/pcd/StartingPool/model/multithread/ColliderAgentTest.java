package pcd.StartingPool.model.multithread;



import static org.junit.Assert.assertTrue;

import org.junit.Test;
import pcd.startingPoool.model.BoardConf;
import pcd.startingPoool.model.StandardConf;
import pcd.startingPoool.model.game.Ball;
import pcd.startingPoool.model.game.P2d;
import pcd.startingPoool.model.game.V2d;
import pcd.startingPoool.model.multithread.CollisionMonitor;
import pcd.startingPoool.model.multithread.CollisionTask;
import pcd.startingPoool.model.multithread.CollisionsMonitorImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


public class ColliderAgentTest {

    private static final double PX = 0.0;
    private static final double PY = 0.0;
    private static final double BALL_RADIUS = 0.01;

    @Test
    public void sum_shouldAddComponents() {



        List<Ball> balls= new ArrayList<>();
        IntStream.range(0, 40000).forEach(i -> {
            balls.add(new Ball(new P2d(PX, PY), BALL_RADIUS, 0.25, new V2d(0,0)));
        });

        CollisionMonitor bufferOfTasks = new CollisionsMonitorImpl();
        for (int i = 0; i < 1; i++){
            new pcd.startingPoool.model.multithread.ColliderAgent(bufferOfTasks).start();
        }

        var t1 = System.currentTimeMillis();
        for (int i = 0; i < balls.size() - 1; i++) {
            List<CollisionTask> listOfTasks = new ArrayList<>();
            for (int j = i + 1; j < balls.size(); j++) {
                listOfTasks.add(new CollisionTask(balls.get(i), balls.get(j), Ball.LastTouchedBy.NONE));
            }
            bufferOfTasks.put(listOfTasks);

        }



        assertTrue( bufferOfTasks.allTasksDone()); ;

        var t2 = System.currentTimeMillis() - t1;
        System.out.println("time of task " + t2);

    }


}

