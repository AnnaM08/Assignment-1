package pcd.startingPoool;

public record Hole(double radius, P2d position) {
    //metodo per verificare se data una pallina si trova nella buca
    public boolean isInside(Ball ball){
        P2d ballPos = ball.getPos();

        double dx = ballPos.x() - position.x();
        double dy = ballPos.y() - position.y();

        double distanceSquared = dx * dx + dy * dy;

        return distanceSquared < (radius * radius);
    }
}
