package pcd.startingPoool.model.multithread;

import pcd.startingPoool.model.game.Ball;

import java.util.List;

public interface CollisionMonitor {

    void put(List<Ball> task);

    List<Ball> get();


}
