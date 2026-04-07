package pcd.startingPoool;

import java.util.List;

public interface BoardConf {

	Boundary getBoardBoundary();
	
	Ball getPlayerBall();
	
	List<Ball> getSmallBalls();

    Ball getBotBall();

    Hole getFirstHole();

    Hole getSecondHole();
}
