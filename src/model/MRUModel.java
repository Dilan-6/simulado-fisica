package model;

public class MRUModel {
    private double x0, v;

    public MRUModel(double x0, double v) {
        this.x0 = x0;
        this.v = v;
    }

    public double positionAt(double t) {
        return x0 + v * t;
    }

    public double displacementAt(double t) {
        return positionAt(t) - x0;
    }

    public double timeToReach(double xDestino) {
        if (v == 0) return Double.POSITIVE_INFINITY;
        return (xDestino - x0) / v;
    }

    public double getX0() {
        return x0;
    }

    public double getVelocity() {
        return v;
    }
}
