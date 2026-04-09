package pcd.startingPoool.model;

import pcd.startingPoool.model.game.Boundary;
//import pcd.startingPoool.model.multithread.ColliderAgent;
import pcd.startingPoool.model.multithread.ColliderAgent;
import pcd.startingPoool.model.multithread.CollisionMonitor;
import pcd.startingPoool.model.game.Ball;
import pcd.startingPoool.model.game.Hole;
import pcd.startingPoool.model.game.V2d;

import java.util.ArrayList;
import java.util.List;

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
    private static final int NUMBER_OF_AGENTS = Runtime.getRuntime().availableProcessors() + 1;
    private  List<Ball> allBalls;

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
        latch = new LatchImpl(NUMBER_OF_AGENTS);
        //latch.setNumberTasks(NUMBER_OF_AGENTS);
        this.allBalls = new ArrayList<>(balls);
        this.allBalls.add(playerBall);
        this.allBalls.add(botBall);

        //creazione della bag of tasks (#CORE + 1)
        for (int i = 0; i <  NUMBER_OF_AGENTS ; i++){
            new ColliderAgent(bufferOfTasks, latch, allBalls).start();
        }
    }
    
    @Override
    public synchronized void updateState(long dt) {

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
            System.out.println("--- " + "Indice partenza " +i + " Indice Fine " + end + "final Size " + chunk.size());

            // Invia una COPIA al monitor (importante per la thread-safety)
            bufferOfTasks.put(new ArrayList<>(chunk));

            if (end == allBalls.size()) {
                break;
            }
        }

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
        // if (balls.size() != previousSize){
        //    //in questo caso si rimuovono le palline
        //    latch.setNumberTasks(calcNumTasksFromNumBalls(balls.size()));
        //}
        //latch.setNumberTasks(NUMBER_OF_AGENTS);

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

    //conteggio di tutte le coppie di palline
    private int calcNumTasksFromNumBalls (int numBalls){
        //tutte le coppie non ripetute di palline + tutte le palline con quella del giocatore + tutte le palline con quella del bot + la pallina del giocatore con quella del bot
        return numBalls * (numBalls - 1) / 2 + numBalls + numBalls + 1;
    }
}
