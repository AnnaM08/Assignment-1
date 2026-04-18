package pcd.StartingPoool.model.multithread;


import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import pcd.startingPoool.controller.BallType;
import pcd.startingPoool.model.LatchImpl;
import pcd.startingPoool.model.game.Ball;
import pcd.startingPoool.model.game.P2d;
import pcd.startingPoool.model.game.V2d;
import pcd.startingPoool.model.multithread.ColliderAgent;
import pcd.startingPoool.model.multithread.CollisionMonitor;
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
    private static final int BALL_NUMBER = 1000;

    private void testCollisions(int numBalls, int numAgents) {
        List<Ball> allBalls = new ArrayList<>();
        IntStream.range(0, numBalls).forEach(i ->
                allBalls.add(new Ball(new P2d(PX, PY), BALL_RADIUS, 0.25, new V2d(0, 0), BallType.BASE))
        );

        CollisionMonitor bufferOfTasks = new CollisionsMonitorImpl();
        var latch = new LatchImpl(numAgents);


        List<ColliderAgent> colliderAgents = new ArrayList<>();
        for (int i = 0; i < numAgents; i++) {
            var agent =  new pcd.startingPoool.model.multithread.ColliderAgent(bufferOfTasks, latch, allBalls);
            colliderAgents.add(agent);
        }

        int chunkSize = (allBalls.size()) / numAgents;
        for (int i = 0; i < allBalls.size(); i += chunkSize) {
            // Calcola la fine del pacchetto (evitando di andare fuori dai limiti della lista)
            int end = Math.min(i + chunkSize, allBalls.size());

            if (end + chunkSize > allBalls.size()) {
                end  = allBalls.size();
            }
            // Estrai la sottolista
            List<Ball> chunk = allBalls.subList(i, end);
            System.out.println("--- " + "Indice partenza " +i + " Indice Fine " + end + "final Size " + chunk.size());

            // Invia una COPIA al monitor (importante per la thread-safety)
            bufferOfTasks.put(new ArrayList<>(chunk));

            if (end == allBalls.size()) {
                break;
            }
        }


        long t1 = System.currentTimeMillis();
        colliderAgents.forEach(ColliderAgent::start);


        try{
            latch.await();
        } catch (InterruptedException ex){

        }

        long t2 = System.currentTimeMillis() - t1;

        LOGGER.info( numBalls + " " + numAgents + " " + t2);

       // colliderAgents.forEach(ColliderAgent::interrupt);

    }



    @Test
    public void test1_WithBallsAnd1Agent() {
        testCollisions(BALL_NUMBER, 1);
    }

    @Test
    public void test2_WitMaxAgents() {
        int maxAgents = Runtime.getRuntime().availableProcessors() + 1;
        testCollisions(BALL_NUMBER, maxAgents);
    }

    @Test
    public void test3_WithTwoAgents() {
        testCollisions(BALL_NUMBER, 2);
    }

    @Test
    public void test4_With8Agents() {
        testCollisions(BALL_NUMBER, 4);
    }


}