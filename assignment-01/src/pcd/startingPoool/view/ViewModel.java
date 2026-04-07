package pcd.startingPoool.view;

import pcd.startingPoool.Board;
import pcd.startingPoool.P2d;

import java.util.ArrayList;

record BallViewInfo(P2d pos, double radius) {}

public class ViewModel {

	private ArrayList<BallViewInfo> balls;
	private BallViewInfo player;
    private BallViewInfo bot;
    private int playerScore;
    private int botScore;
	private int framePerSec;
    private BallViewInfo firstHole;
    private BallViewInfo secondHole;
	
	public ViewModel() {
		balls = new ArrayList<BallViewInfo>();
		framePerSec = 0;
	}
	
	public synchronized void update(Board board, int framePerSec) {
		balls.clear();
		for (var b: board.getBalls()) {
			balls.add(new BallViewInfo(b.getPos(), b.getRadius()));
		}
		this.framePerSec = framePerSec;
		var p = board.getPlayerBall();
		player = new BallViewInfo(p.getPos(), p.getRadius());
        //bot ball
        var b = board.getBotBall();
        bot = new BallViewInfo(b.getPos(), b.getRadius());

        //score dei giocatori
        this.playerScore = board.getPlayerScore();
        this.botScore = board.getBotScore();

        //buche del gioco
        var fh = board.getFistHole();
        this.firstHole = new BallViewInfo(fh.position(), fh.radius());
        var sh = board.getSecondHole();
        this.secondHole = new BallViewInfo(sh.position(), sh.radius());
	}
	
	public synchronized ArrayList<BallViewInfo> getBalls(){
		var copy = new ArrayList<BallViewInfo>();
		copy.addAll(balls);
		return copy;
		
	}

	public synchronized int getFramePerSec() {
		return framePerSec;
	}

	public synchronized BallViewInfo getPlayerBall() {
		return player;
	}

    public synchronized BallViewInfo getBotBall() {
        return bot;
    }

    public synchronized String getPlayerScore(){ return String.valueOf(playerScore); }

    public synchronized String getBotScore(){ return String.valueOf(botScore); }

    public synchronized BallViewInfo getFirstHole(){ return firstHole; }

    public synchronized BallViewInfo getSecondHole(){ return secondHole; }
}
