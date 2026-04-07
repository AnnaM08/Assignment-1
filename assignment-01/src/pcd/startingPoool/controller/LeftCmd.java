package pcd.startingPoool.controller;

import pcd.startingPoool.Board;
import pcd.startingPoool.V2d;

public class LeftCmd implements Cmd{
    private final BallType ballType;

    public LeftCmd(BallType t) {
        ballType = t;
    }

    @Override
    public void execute(Board b) {
        var v = new V2d(-1.0, 0.0);
        switch (this.ballType){
            case PLAYER -> b.updatePlayerBall(v);
            case BOT -> b.updateBotBall(v);
        }
    }
}
