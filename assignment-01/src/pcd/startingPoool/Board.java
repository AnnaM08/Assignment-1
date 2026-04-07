package pcd.startingPoool;

import java.util.List;

import static pcd.startingPoool.Ball.resolveCollision;

public class Board {

    private List<Ball> balls;
    private Ball playerBall;
    private Ball botBall;
    private Boundary bounds;
    private int playerScore = 0;
    private int botScore = 0;
    private Hole fistHole;
    private Hole secondHole;

    public Board(){} 
    
    public void init(BoardConf conf) {
    	balls = conf.getSmallBalls();    	
    	playerBall = conf.getPlayerBall();
        botBall = conf.getBotBall();
    	bounds = conf.getBoardBoundary();
        fistHole = conf.getFirstHole();
        secondHole = conf.getSecondHole();
    }
    
    public void updateState(long dt) {

    	playerBall.updateState(dt, this);
        botBall.updateState(dt, this);
    	
    	for (var b: balls) {
    		b.updateState(dt, this);
    	}       	
    	
    	for (int i = 0; i < balls.size() - 1; i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                //si verifica se le palline collidono allora sono allontanate secondo la normale
                resolveCollision(balls.get(i), balls.get(j), Ball.LastTouchedBy.NONE);
            }
        }
    	for (var b: balls) {
    		resolveCollision(b, playerBall, Ball.LastTouchedBy.PLAYER);
    	}
        for (var b: balls) {
            resolveCollision(b, botBall, Ball.LastTouchedBy.BOT);
        }

        resolveCollision(playerBall, botBall, Ball.LastTouchedBy.NONE);
//Uso iterator per ciclare gli elementi della lista e rimuovere le palline entrate in buca
        //Considera che anche il bot e il giocatore possono collidere con la buca
        var it = balls.iterator();
        while (it.hasNext()) {
            var b = it.next();
            if (fistHole.isInside(b)) {
                if(b.getLastTouchedBy() == Ball.LastTouchedBy.PLAYER){
                    playerScore++;
                }
                if(b.getLastTouchedBy() == Ball.LastTouchedBy.BOT){
                    botScore++;
                }
                it.remove();
                System.out.println("Pallina rimossa");
            } else if (secondHole.isInside(b)) {
                if(b.getLastTouchedBy() == Ball.LastTouchedBy.PLAYER){
                    playerScore++;
                }
                if(b.getLastTouchedBy() == Ball.LastTouchedBy.BOT){
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
