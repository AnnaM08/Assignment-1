package pcd.startingPoool.model;

import pcd.startingPoool.model.game.Boundary;
import pcd.startingPoool.model.multithread.CollisionMonitor;
import pcd.startingPoool.model.game.Ball;
import pcd.startingPoool.model.game.Hole;
import pcd.startingPoool.model.game.V2d;

import java.util.List;

public interface Board {
    void init(BoardConf conf, CollisionMonitor b);

    void updateState(long dt);

    List<Ball> getBalls();

    Ball getPlayerBall();

    Ball getBotBall();

    Boundary getBounds();

    void updatePlayerBall(V2d velocity);

    void updateBotBall(V2d velocity);

    int getPlayerScore();

    int getBotScore();

    Hole getFistHole();

    Hole getSecondHole();

    //il giocatore vince se il bot è entrato nella buca
    // o se tutte le palline sono state tirate in buca e il giocatore ha il punteggio più alto
    boolean hasPlayerWon();

    //il bot vince se il giocatore è entrato nella buca
    // o se tutte le palline sono state tirate in buca e il bot ha il punteggio più alto
    boolean hasBotWon();
}
