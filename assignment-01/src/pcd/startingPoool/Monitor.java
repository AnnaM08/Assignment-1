package pcd.startingPoool;

import java.util.List;

public interface Monitor {

    void put(List<CollisionTask> task);

    List<CollisionTask> get();

    boolean allTasksDone();

}
