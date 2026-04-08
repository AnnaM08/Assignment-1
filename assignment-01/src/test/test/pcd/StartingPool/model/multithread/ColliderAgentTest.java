package pcd.StartingPool.model.multithread;

import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import pcd.startingPoool.model.game.Ball;
import pcd.startingPoool.model.game.P2d;
import pcd.startingPoool.model.game.V2d;
import pcd.startingPoool.model.multithread.CollisionMonitor;
import pcd.startingPoool.model.multithread.CollisionTask;
import pcd.startingPoool.model.multithread.CollisionsMonitorImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ColliderAgentTest {

    private static final double PX = 0.0;
    private static final double PY = 0.0;
    private static final double BALL_RADIUS = 0.01;
    private static final int BALL_NUMBER = 1000;

    private void testCollisions(int numBalls, int numAgents) {
        List<Ball> balls = new ArrayList<>();
        IntStream.range(0, numBalls).forEach(i -> {
            balls.add(new Ball(new P2d(PX, PY), BALL_RADIUS, 0.25, new V2d(0, 0)));
        });

        CollisionMonitor bufferOfTasks = new CollisionsMonitorImpl();

        for (int i = 0; i < numAgents; i++) {
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

        assertTrue(bufferOfTasks.allTasksDone());

        var t2 = System.currentTimeMillis() - t1;
        System.out.println("Tempo di esecuzione con " + numBalls + " palline e " + numAgents + " agenti: " + t2 + " ms");
    }

    @Test
    public void test1_WithBallsAnd1Agent() {
        testCollisions(BALL_NUMBER, 1);
    }

    @Test
    public void test2_WitMaxAgents() {
        int maxAgents = Runtime.getRuntime().availableProcessors();
        testCollisions(BALL_NUMBER, maxAgents);
    }

    @Test
    public void test3_WithTwoAgents() {
        int maxAgents = 2;
        testCollisions(BALL_NUMBER, maxAgents);
    }
}