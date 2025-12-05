package model;

/**
 * Modelo matemático para simular el movimiento de caída libre.
 * Implementa las ecuaciones físicas para calcular posición, velocidad y tiempo
 * de objetos en caída libre bajo la influencia de la gravedad.
 * 
 * @author SimuladorFisica
 * @version 1.0
 */
public class FreeFallModel {
    /** Aceleración gravitacional estándar en m/s² */
    public static final double G = 9.81;
    
    /** Altura inicial del objeto en metros */
    private double h0;
    
    /** Velocidad inicial del objeto en m/s (positiva hacia arriba, negativa hacia abajo) */
    private double v0;

    /**
     * Construye un nuevo modelo de caída libre.
     * 
     * @param h0 Altura inicial en metros (debe ser positiva)
     * @param v0 Velocidad inicial en m/s (positiva hacia arriba, negativa hacia abajo)
     * @param a Aceleración (actualmente no se usa, se utiliza G constante)
     */
    public FreeFallModel(double h0, double v0, double a) {
        this.h0 = h0;
        this.v0 = v0;
    }

    /**
     * Calcula la altura del objeto en un tiempo dado.
     * Fórmula: h = h0 - (v0*t + (1/2)*g*t²)
     * 
     * @param t Tiempo transcurrido en segundos
     * @return Altura actual en metros (puede ser negativa si el objeto pasó el suelo)
     */
    public double positionAt(double t) {
        return h0 - (v0 * t + 0.5 * G * t * t);
    }

    /**
     * Calcula la velocidad del objeto en un tiempo dado.
     * Fórmula: v = v0 + g*t
     * 
     * @param t Tiempo transcurrido en segundos
     * @return Velocidad en m/s (positiva hacia abajo, negativa hacia arriba)
     */
    public double velocityAt(double t) {
        return v0 + G * t;
    }

    /**
     * Calcula la distancia total caída desde la altura inicial.
     * 
     * @param t Tiempo transcurrido en segundos
     * @return Distancia caída en metros (siempre positiva o cero)
     */
    public double distanceTravelled(double t) {
        double distance = h0 - positionAt(t);
        return Math.max(distance, 0);
    }

    /**
     * Calcula el tiempo necesario para que el objeto llegue al suelo.
     * Utiliza la fórmula cuadrática para resolver: h0 = v0*t + (1/2)*g*t²
     * 
     * @return Tiempo hasta el suelo en segundos, o -1 si el objeto nunca toca el suelo
     */
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

    /**
     * Calcula la velocidad de impacto cuando el objeto toca el suelo.
     * Utiliza la fórmula: v² = v0² + 2*g*h
     * 
     * @return Velocidad de impacto en m/s (siempre positiva)
     */
    public double impactVelocity() {
        return Math.sqrt(v0 * v0 + 2 * G * h0);
    }

    /**
     * Obtiene la altura inicial del objeto.
     * 
     * @return Altura inicial en metros
     */
    public double getInitialHeight() {
        return h0;
    }

    /**
     * Obtiene la velocidad inicial del objeto.
     * 
     * @return Velocidad inicial en m/s
     */
    public double getInitialVelocity() {
        return v0;
    }

    /**
     * Obtiene la aceleración gravitacional utilizada.
     * 
     * @return Aceleración en m/s² (siempre 9.81)
     */
    public double getAcceleration() {
        return G;
    }
}
