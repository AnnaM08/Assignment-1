package pcd.startingPoool.model.game;

import java.util.Objects;

public final class Hole {
    private final double radius;
    private final P2d position;

    public Hole(double radius, P2d position) {
        this.radius = radius;
        this.position = position;
    }

    public double radius() {
        return radius;
    }

    public P2d position() {
        return position;
    }

    // metodo per verificare se data una pallina si trova nella buca
    public boolean isInside(Ball ball) {
        P2d ballPos = ball.getPos();

        double dx = ballPos.x() - position.x();
        double dy = ballPos.y() - position.y();

        double distanceSquared = dx * dx + dy * dy;

        return distanceSquared < (radius * radius);
    }


    @Override
    public int hashCode() {
        return Objects.hash(radius, position);
    }

    @Override
    public String toString() {
        return "Hole[radius=" + radius + ", position=" + position + "]";
    }
}
