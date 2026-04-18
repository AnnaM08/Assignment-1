package pcd.startingPoool.model.game;

import java.util.Objects;

public final class V2d {
    private final double x;
    private final double y;

    public V2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public V2d sum(V2d v) {
        return new V2d(x + v.x(), y + v.y());
    }

    public double abs() {
        return Math.sqrt(x * x + y * y);
    }

    public V2d getNormalized() {
        double module = Math.sqrt(x * x + y * y);
        return new V2d(x / module, y / module);
    }

    public V2d mul(double fact) {
        return new V2d(x * fact, y * fact);
    }

    public V2d getSwappedX() {
        return new V2d(-x, y);
    }

    public V2d getSwappedY() {
        return new V2d(x, -y);
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    @Override
    public String toString() {
        return "V2d(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof V2d)) {
            return false;
        }
        V2d other = (V2d) obj;
        return Double.compare(x, other.x) == 0 && Double.compare(y, other.y) == 0;
    }


    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
