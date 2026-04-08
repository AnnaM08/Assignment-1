package pcd.startingPoool.model.multithread;

import java.util.List;

public interface CollisionMonitor {

    void put(List<CollisionTask> task);

    List<CollisionTask> get();

    boolean allTasksDone();

}
