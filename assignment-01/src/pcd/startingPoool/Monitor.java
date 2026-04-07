package pcd.startingPoool;

public interface Monitor {

    void put(CollisionTask task);

    CollisionTask get();

    boolean bufferIsEmpty();

}
