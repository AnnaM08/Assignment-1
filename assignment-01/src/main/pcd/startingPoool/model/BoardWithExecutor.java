package pcd.startingPoool.model;

import pcd.startingPoool.model.game.Boundary;
import pcd.startingPoool.model.multithread.ColliderTask;
import pcd.startingPoool.model.multithread.CollisionMonitor;
import pcd.startingPoool.model.game.Ball;
import pcd.startingPoool.model.game.Hole;
import pcd.startingPoool.model.game.V2d;
import pcd.startingPoool.controller.BallType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static pcd.startingPoool.model.game.Ball.resolveCollision;



public class BoardWithExecutor implements Board {

    private List<Ball> balls;
    private Ball playerBall;
    private Ball botBall;
    private Boundary bounds;
    private int playerScore = 0;
    private int botScore = 0;
    private Hole fistHole;
    private Hole secondHole;
    private CollisionMonitor bufferOfTasks;
    private ExecutorService executor;
    private List<Future<Boolean>> results;
    private List<Ball> allBalls;
    private static final int NUMBER_OF_AGENTS = Runtime.getRuntime().availableProcessors() + 1;

    public BoardWithExecutor(){}
    
    public void init(BoardConf conf, CollisionMonitor b) {
    	balls = conf.getSmallBalls();    	
    	playerBall = conf.getPlayerBall();
        botBall = conf.getBotBall();
    	bounds = conf.getBoardBoundary();
        fistHole = conf.getFirstHole();
        secondHole = conf.getSecondHole();
        //passato il riferimento al monitor
        bufferOfTasks = b;
        //creazione della bag of tasks (#CORE + 1)
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1);
        this.results = new ArrayList<>();
        this.allBalls = new ArrayList<>(balls);
        this.allBalls.add(playerBall);
        this.allBalls.add(botBall);
    }

    public void updateState(long dt) {

    	playerBall.updateState(dt, this);
        botBall.updateState(dt, this);
    	for (var b: balls) {
    		b.updateState(dt, this);
    	}

        int chunkSize = (allBalls.size()) / NUMBER_OF_AGENTS ;
        for (int i = 0; i < allBalls.size(); i += chunkSize) {
            // Calcola la fine del pacchetto (evitando di andare fuori dai limiti della lista)
            int end = Math.min(i + chunkSize, allBalls.size());

            if (end + chunkSize > allBalls.size()) {
                end  = allBalls.size();
            }
            // Estrai la sottolista
            List<Ball> chunk = allBalls.subList(i, end);
            //System.out.println("--- " + "Indice partenza " +i + " Indice Fine " + end + "final Size " + chunk.size());

            // Invia una COPIA al monitor (importante per la thread-safety)
            Future<Boolean> res = executor.submit(new ColliderTask(new ArrayList<>(chunk), allBalls));
            results.add(res);

            if (end == allBalls.size()) {
                break;
            }
        }

        for (Future<Boolean> res: results) {
            try {
                res.get();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        //Uso iterator per ciclare gli elementi della lista e rimuovere le palline entrate in buca
        //Considera che anche il bot e il giocatore possono collidere con la buca
        var it = balls.iterator();
        while (it.hasNext()) {
            var b = it.next();
            if (fistHole.isInside(b)) {
                if (b.getLastTouchedBy() == BallType.PLAYER) {
                    playerScore++;
                }
                if (b.getLastTouchedBy() == BallType.BOT) {
                    botScore++;
                }
                it.remove();
                System.out.println("Pallina rimossa");
            } else if (secondHole.isInside(b)) {
                if (b.getLastTouchedBy() == BallType.PLAYER) {
                    playerScore++;
                }
                if (b.getLastTouchedBy() == BallType.BOT) {
                    botScore++;
                }
                it.remove();
                System.out.println("Pallina rimossa");
            }
        }
    }

    
    public List<Ball> getBalls(){
    	return balls;
    }
    
    public Ball getPlayerBall() {
    	return playerBall;
    }

    public Ball getBotBall(){ return botBall; }
    
    public Boundary getBounds(){
        return bounds;
    }

    public void updatePlayerBall(V2d velocity){ playerBall.kick(velocity); }

    public void updateBotBall(V2d velocity){ botBall.kick(velocity); }

    public int getPlayerScore(){ return playerScore; }

    public int getBotScore(){ return botScore; }

    public Hole getFistHole(){ return fistHole; }

    public Hole getSecondHole(){ return secondHole; }

    //il giocatore vince se il bot è entrato nella buca
    // o se tutte le palline sono state tirate in buca e il giocatore ha il punteggio più alto
    public boolean hasPlayerWon(){
        if (balls.isEmpty()){
            return playerScore > botScore;
        }

        return fistHole.isInside(botBall) || secondHole.isInside(botBall);
    }

    //il bot vince se il giocatore è entrato nella buca
    // o se tutte le palline sono state tirate in buca e il bot ha il punteggio più alto
    public boolean hasBotWon(){
        if (balls.isEmpty()){
            return botScore > playerScore;
        }

        return fistHole.isInside(playerBall) || secondHole.isInside(playerBall);
    }
}
