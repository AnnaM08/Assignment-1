package pcd.startingPoool.controller;

import pcd.startingPoool.Board;
import pcd.startingPoool.V2d;

public class UpCmd implements Cmd{
    @Override
    public void execute(Board b) {
        b.updatePlayerBall(new V2d(0.0, 1.0));
    }
}
