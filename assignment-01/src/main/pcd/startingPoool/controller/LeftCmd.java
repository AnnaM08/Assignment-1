package pcd.startingPoool.controller;

import pcd.startingPoool.model.Board;
import pcd.startingPoool.model.game.V2d;

import java.sql.Timestamp;

public class LeftCmd extends AbstractCmd {

    private final BallType ballType;

    public LeftCmd(BallType t, Timestamp timestamp) {
        super(timestamp);
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
