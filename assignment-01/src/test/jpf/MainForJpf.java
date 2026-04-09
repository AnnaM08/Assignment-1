package jpf;

import pcd.startingPoool.controller.BallType;
import pcd.startingPoool.model.Latch;
import pcd.startingPoool.model.LatchImpl;
import pcd.startingPoool.model.game.Ball;
import pcd.startingPoool.model.game.P2d;
import pcd.startingPoool.model.game.V2d;
import pcd.startingPoool.model.multithread.ColliderAgent;
import pcd.startingPoool.model.multithread.CollisionMonitor;
import pcd.startingPoool.model.multithread.CollisionsMonitorImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class MainForJpf {

    private static final double PX = 0.0;
    private static final double PY = 0.0;
    private static final double BALL_RADIUS = 0.01;

    // Valori fissi: nessun parametro da command line
    private static final int NUM_BALLS = 1000;
    private static final int NUM_AGENTS = 4;

    public static void main(String[] args) throws InterruptedException {
        List<Ball> allBalls = new ArrayList<>(NUM_BALLS);
        IntStream.range(0, NUM_BALLS).forEach(i ->
                allBalls.add(new Ball(
                        new P2d(PX, PY),
                        BALL_RADIUS,
                        0.25,
                        new V2d(0, 0),
                        BallType.BASE
                ))
        );

        // Evita casi invalidi (es. più agenti che palline)
        int workers = Math.max(1, Math.min(NUM_AGENTS, allBalls.size()));

        CollisionMonitor bufferOfTasks = new CollisionsMonitorImpl();
        Latch latch = new LatchImpl(workers);

        List<ColliderAgent> agents = new ArrayList<>(workers);
        for (int i = 0; i < workers; i++) {
            ColliderAgent agent = new ColliderAgent(bufferOfTasks, latch, allBalls);
            agent.setDaemon(true); // run() è infinito, così il processo può terminare
            agents.add(agent);
        }

        // Chunk bilanciati: differenza massima 1 elemento tra i worker
        int base = allBalls.size() / workers;
        int rem = allBalls.size() % workers;
        int cursor = 0;
        for (int i = 0; i < workers; i++) {
            int chunkSize = base + (i < rem ? 1 : 0);
            int end = cursor + chunkSize;
            bufferOfTasks.put(new ArrayList<>(allBalls.subList(cursor, end)));
            cursor = end;
        }

        long t1 = System.currentTimeMillis();

        agents.forEach(Thread::start);
        latch.await();

        long elapsed = System.currentTimeMillis() - t1;
        System.out.println("Done. balls=" + NUM_BALLS + ", agents=" + workers + ", timeMs=" + elapsed);
    }
}
