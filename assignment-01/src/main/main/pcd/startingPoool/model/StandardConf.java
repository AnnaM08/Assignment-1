package pcd.startingPoool.model;

import pcd.startingPoool.model.game.*;

import java.util.ArrayList;
import java.util.List;

public class StandardConf implements BoardConf {

    @Override
    public Ball getPlayerBall() {
        return  new Ball(new P2d(-0.4, -0.75), 0.05, 1.5, new V2d(0,0));
    }

    @Override
    public List<Ball> getSmallBalls() {
        var ballRadius = 0.01;
        var balls = new ArrayList<Ball>();

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 100; col++) {
                var px = -0.75 + col*0.015;
                var py = -0.15 + row*0.015;
                var b = new Ball(new P2d(px, py), ballRadius, 0.25, new V2d(0,0));
                balls.add(b);
            }
        }
        return balls;
    }

    @Override
    public Ball getBotBall() {return new Ball(new P2d(0.4, -0.75), 0.05, 1.5, new V2d(0,0));}

    public Boundary getBoardBoundary() {
        return new Boundary(-1.5,-1.0,1.5,1.0);
    }

    public Hole getFirstHole(){ return new Hole(0.15, new P2d(1.45, 0.97)); }

    public Hole getSecondHole(){ return new Hole(0.15, new P2d(-1.50, 0.97)); }
}

