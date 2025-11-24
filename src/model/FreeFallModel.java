package model;

public class FreeFallModel {
    public static final double G = 9.81;
    private double h0, v0;

    public FreeFallModel(double h0, double v0, double a) {
        this.h0 = h0;
        this.v0 = v0;
    }

    // Altura actual: h = h0 - (v0*t + (1/2)*g*t²)
    public double positionAt(double t) {
        return h0 - (v0 * t + 0.5 * G * t * t);
    }

    // Velocidad: v = v0 + g*t
    public double velocityAt(double t) {
        return v0 + G * t;
    }

    // Distancia caída desde la altura inicial
    public double distanceTravelled(double t) {
        double distance = h0 - positionAt(t);
        return Math.max(distance, 0);
    }

    // Tiempo hasta el suelo usando fórmula cuadrática
    // h0 = v0*t + (1/2)*g*t²  ->  (1/2)*g*t² + v0*t - h0 = 0
    public double timeToGround() {
        double A = 0.5 * G;
        double B = v0;
        double C = -h0;
        double disc = B * B - 4 * A * C;
        if (disc < 0) return -1;
        double t1 = (-B + Math.sqrt(disc)) / (2 * A);
        double t2 = (-B - Math.sqrt(disc)) / (2 * A);
        double t = Math.max(t1, t2);
        return t >= 0 ? t : -1;
    }

    // Velocidad de impacto usando v² = v0² + 2*g*h
    public double impactVelocity() {
        return Math.sqrt(v0 * v0 + 2 * G * h0);
    }

    public double getInitialHeight() {
        return h0;
    }

    public double getInitialVelocity() {
        return v0;
    }

    public double getAcceleration() {
        return G;
    }
}
