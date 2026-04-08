package pcd.StartingPool.model.multithread;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import pcd.startingPoool.model.Latch;
import pcd.startingPoool.model.LatchImpl;
import pcd.startingPoool.model.game.Ball;
import pcd.startingPoool.model.game.P2d;
import pcd.startingPoool.model.game.V2d;
import pcd.startingPoool.model.multithread.ColliderAgent;
import pcd.startingPoool.model.multithread.CollisionMonitor;
import pcd.startingPoool.model.multithread.CollisionTask;
import pcd.startingPoool.model.multithread.CollisionsMonitorImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ColliderAgentTest {
    @BeforeClass
    public static void setupLogger() throws IOException {
        LOGGER.setUseParentHandlers(false);
        LOGGER.setLevel(Level.INFO);

        // Evita handler duplicati se i test vengono rieseguiti nella stessa JVM
        for (var h : LOGGER.getHandlers()) {
            LOGGER.removeHandler(h);
        }

        Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                return String.format(
                        "%1$tF %1$tT [%2$s] %3$s%n",
                        record.getMillis(),
                        record.getLevel().getName(),
                        record.getMessage()
                );
            }
        };

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(formatter);
        LOGGER.addHandler(consoleHandler);

        Path logDir = Path.of("target", "test-logs");
        Files.createDirectories(logDir);

        Path logFile = logDir.resolve("collider-agent-test.log");
        Files.deleteIfExists(logFile);


        FileHandler fileHandler = new FileHandler( logFile.toString(), true);
        fileHandler.setLevel(Level.INFO);
        fileHandler.setFormatter(formatter);
        LOGGER.addHandler(fileHandler);
    }

    private static final Logger LOGGER = Logger.getLogger(ColliderAgentTest.class.getName());

    private static final double PX = 0.0;
    private static final double PY = 0.0;
    private static final double BALL_RADIUS = 0.01;
    private static final int BALL_NUMBER = 2000;

    private void testCollisions(int numBalls, int numAgents) {
        List<Ball> balls = new ArrayList<>();
        IntStream.range(0, numBalls).forEach(i ->
                balls.add(new Ball(new P2d(PX, PY), BALL_RADIUS, 0.25, new V2d(0, 0)))
        );


        Latch latch = new LatchImpl();
        latch.setNumberTasks(balls.size() * (balls.size() - 1) / 2);


        CollisionMonitor bufferOfTasks = new CollisionsMonitorImpl();
        List<ColliderAgent> colliderAgents = new ArrayList<>();
        for (int i = 0; i < numAgents; i++) {
           var agent =  new pcd.startingPoool.model.multithread.ColliderAgent(bufferOfTasks, latch);
           colliderAgents.add(agent);
        }

        colliderAgents.forEach(ColliderAgent::start);

        long t1 = System.currentTimeMillis();

        for (int i = 0; i < balls.size() - 1; i++) {
            List<CollisionTask> listOfTasks = new ArrayList<>();
            for (int j = i + 1; j < balls.size(); j++) {
                listOfTasks.add(new CollisionTask(balls.get(i), balls.get(j), Ball.LastTouchedBy.NONE));
            }
            bufferOfTasks.put(listOfTasks);
        }

        assertTrue(bufferOfTasks.allTasksDone());

        long t2 = System.currentTimeMillis() - t1;
        //colliderAgents.forEach(ColliderAgent::interrupt);
        //colliderAgents.forEach(t -> {
        //    try{
        //        t.join(1000);
        //    } catch (Exception e){
        //
         //    }
        //});
        LOGGER.info( numBalls + " " + numAgents + " " + t2);

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
        testCollisions(BALL_NUMBER, 2);
    }

    @Test
    public void test4_With8Agents() {
        testCollisions(12000, 4);
    }



    @Test
    public void test5_parametrized(){
        var BallsNumber = IntStream.range(1,25).map(n -> n* 500).boxed().toList();
        var numberOfThread = IntStream.range(1,  Runtime.getRuntime().availableProcessors() + 2).boxed().toList();

        BallsNumber.forEach( n -> {

                numberOfThread.forEach( t -> {
                        testCollisions(n,t);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                        }

                });

        });

    }
}