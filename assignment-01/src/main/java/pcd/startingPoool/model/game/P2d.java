package pcd.startingPoool.model.game;

import java.util.Objects;

public final class P2d {
    private final double x;
    private final double y;

    public P2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public P2d sum(V2d v) {
        return new P2d(x + v.x(), y + v.y());
    }

    public V2d sub(P2d v) {
        return new V2d(x - v.x(), y - v.y());
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    @Override
    public String toString() {
        return "P2d(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof P2d)) {
            return false;
        }
        P2d other = (P2d) obj;
        return Double.compare(x, other.x) == 0 && Double.compare(y, other.y) == 0;
    }


    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
