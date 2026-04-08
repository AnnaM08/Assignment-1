package pcd.startingPoool.model;

import pcd.startingPoool.model.game.Boundary;
import pcd.startingPoool.model.game.Ball;
import pcd.startingPoool.model.game.Hole;

import java.util.List;

public interface BoardConf {

	Boundary getBoardBoundary();
	
	Ball getPlayerBall();
	
	List<Ball> getSmallBalls();

    Ball getBotBall();

    Hole getFirstHole();

    Hole getSecondHole();
}
