package pcd.startingPoool.controller;

import pcd.startingPoool.Board;
import pcd.startingPoool.V2d;

public class LeftCmd implements Cmd{
    @Override
    public void execute(Board b) {
        b.updatePlayerBall(new V2d(-1.0, 0.0));
    }
}
