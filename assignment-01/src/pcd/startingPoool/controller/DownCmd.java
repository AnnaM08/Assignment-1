package pcd.startingPoool.controller;

import pcd.startingPoool.Board;
import pcd.startingPoool.V2d;

import java.sql.Timestamp;

public class DownCmd extends AbstractCmd {


    private final BallType ballType;
    public DownCmd(BallType t, Timestamp timestamp) {
        super(timestamp);
        ballType = t;
    }

    @Override
    public void execute(Board b) {
        var v = new V2d(0.0, -1.0);
        switch (this.ballType){
            case PLAYER -> b.updatePlayerBall(v);
            case BOT -> b.updateBotBall(v);
        }
    }
}
