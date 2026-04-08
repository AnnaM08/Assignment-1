package pcd.startingPoool.model;

import pcd.startingPoool.model.game.Boundary;
import pcd.startingPoool.model.multithread.ColliderAgent;
import pcd.startingPoool.model.multithread.CollisionTask;
import pcd.startingPoool.model.multithread.CollisionMonitor;
import pcd.startingPoool.model.game.Ball;
import pcd.startingPoool.model.game.Hole;
import pcd.startingPoool.model.game.V2d;

import java.util.ArrayList;
import java.util.List;

import static pcd.startingPoool.model.game.Ball.resolveCollision;

public class BoardWithThreads implements Board {

    private List<Ball> balls;
    private Ball playerBall;
    private Ball botBall;
    private Boundary bounds;
    private int playerScore = 0;
    private int botScore = 0;
    private Hole fistHole;
    private Hole secondHole;
    private CollisionMonitor bufferOfTasks;
    private Latch latch;

    public BoardWithThreads(){}
    
    @Override
    public  synchronized void init(BoardConf conf, CollisionMonitor b) {
    	balls = conf.getSmallBalls();    	
    	playerBall = conf.getPlayerBall();
        botBall = conf.getBotBall();
    	bounds = conf.getBoardBoundary();
        fistHole = conf.getFirstHole();
        secondHole = conf.getSecondHole();
        //passato il riferimento al monitor
        bufferOfTasks = b;
        latch = new LatchImpl();
        latch.setNumberTasks(calcNumTasksFromNumBalls(balls.size()));

        //creazione della bag of tasks (#CORE + 1)
        for (int i = 0; i <  8; i++){
            new ColliderAgent(bufferOfTasks, latch).start();
        }
    }
    
    @Override
    public synchronized void updateState(long dt) {

    	playerBall.updateState(dt, this);
        botBall.updateState(dt, this);
    	
    	for (var b: balls) {
    		b.updateState(dt, this);
    	}

        List<CollisionTask> listOfTasks = new ArrayList<>();
    	for (int i = 0; i < balls.size() - 1; i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                //si verifica se le palline collidono allora sono allontanate secondo la normale
                //resolveCollision(balls.get(i), balls.get(j), Ball.LastTouchedBy.NONE);
                listOfTasks.add(new CollisionTask(balls.get(i), balls.get(j), Ball.LastTouchedBy.NONE));
                //il Master, che è la Board, crea i task da eseguire e li assegna ai worker attraverso il monitor
                //bufferOfTasks.put(new CollisionTask(balls.get(i), balls.get(j), Ball.LastTouchedBy.NONE));
            }
            bufferOfTasks.put(new ArrayList<>(listOfTasks));
            listOfTasks.clear();
        }

    	for (var b: balls) {
    		//resolveCollision(b, playerBall, Ball.LastTouchedBy.PLAYER);
           // bufferOfTasks.put(new CollisionTask(b, playerBall, Ball.LastTouchedBy.PLAYER));
            listOfTasks.add(new CollisionTask(b, playerBall, Ball.LastTouchedBy.PLAYER));
    	}
        bufferOfTasks.put(new ArrayList<>(listOfTasks));
        listOfTasks.clear();

        for (var b: balls) {
            //resolveCollision(b, botBall, Ball.LastTouchedBy.BOT);
            //bufferOfTasks.put(new CollisionTask(b, botBall, Ball.LastTouchedBy.BOT));
            listOfTasks.add(new CollisionTask(b, botBall, Ball.LastTouchedBy.BOT));
        }
        bufferOfTasks.put(new ArrayList<>(listOfTasks));

        resolveCollision(playerBall, botBall, Ball.LastTouchedBy.NONE);
        try {
            //si attende la terminazione dei task da parte dei worker
            latch.await();
        } catch (InterruptedException ex) {
            System.out.println("Interrupted!");
        }

//Uso iterator per ciclare gli elementi della lista e rimuovere le palline entrate in buca
        //Considera che anche il bot e il giocatore possono collidere con la buca
        int previousSize = balls.size();
        var it = balls.iterator();
        while (it.hasNext()) {
            var b = it.next();
            if (fistHole.isInside(b)) {
                if (b.getLastTouchedBy() == Ball.LastTouchedBy.PLAYER) {
                playerScore++;
                }
                if (b.getLastTouchedBy() == Ball.LastTouchedBy.BOT) {
                    botScore++;
                }
                it.remove();
                System.out.println("Pallina rimossa");
            } else if (secondHole.isInside(b)) {
                if (b.getLastTouchedBy() == Ball.LastTouchedBy.PLAYER) {
                    playerScore++;
                }
                if (b.getLastTouchedBy() == Ball.LastTouchedBy.BOT) {
                    botScore++;
                }
                it.remove();
                System.out.println("Pallina rimossa");
            }
        }
        //verifica se sono state rimosse palline
        if (balls.size() != previousSize){
            //in questo caso si rimuovono le palline
            latch.setNumberTasks(calcNumTasksFromNumBalls(balls.size()));
        }

    }
    
    @Override
    public synchronized List<Ball> getBalls(){
    	return balls;
    }
    
    @Override
    public synchronized Ball getPlayerBall() {
    	return playerBall;
    }

    @Override
    public synchronized Ball getBotBall(){ return botBall; }
    
    @Override
    public synchronized Boundary getBounds(){
        return bounds;
    }

    @Override
    public synchronized void updatePlayerBall(V2d velocity){ playerBall.kick(velocity); }

    @Override
    public synchronized void updateBotBall(V2d velocity){ botBall.kick(velocity); }

    @Override
    public synchronized int getPlayerScore(){ return playerScore; }

    @Override
    public synchronized int getBotScore(){ return botScore; }

    @Override
    public synchronized Hole getFistHole(){ return fistHole; }

    @Override
    public synchronized  Hole getSecondHole(){ return secondHole; }

    //il giocatore vince se il bot è entrato nella buca
    // o se tutte le palline sono state tirate in buca e il giocatore ha il punteggio più alto
    @Override
    public synchronized boolean hasPlayerWon(){
        if (balls.isEmpty()){
            return playerScore > botScore;
        }

        return fistHole.isInside(botBall) || secondHole.isInside(botBall);
    }

    //il bot vince se il giocatore è entrato nella buca
    // o se tutte le palline sono state tirate in buca e il bot ha il punteggio più alto
    @Override
    public synchronized boolean hasBotWon(){
        if (balls.isEmpty()){
            return botScore > playerScore;
        }

        return fistHole.isInside(playerBall) || secondHole.isInside(playerBall);
    }

    private int calcNumTasksFromNumBalls (int numBalls){
        return numBalls * (numBalls - 1) / 2 + numBalls + numBalls;
    }
}
