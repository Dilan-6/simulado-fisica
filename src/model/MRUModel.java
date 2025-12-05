package model;

/**
 * Modelo matemático para simular el Movimiento Rectilíneo Uniforme (MRU).
 * Implementa las ecuaciones físicas para calcular posición, desplazamiento
 * y tiempo de objetos que se mueven con velocidad constante.
 * 
 * @author SimuladorFisica
 * @version 1.0
 */
public class MRUModel {
    /** Posición inicial del objeto en metros */
    private double x0;
    
    /** Velocidad constante del objeto en m/s (positiva hacia la derecha, negativa hacia la izquierda) */
    private double v;

    /**
     * Construye un nuevo modelo de MRU.
     * 
     * @param x0 Posición inicial en metros
     * @param v Velocidad constante en m/s
     */
    public MRUModel(double x0, double v) {
        this.x0 = x0;
        this.v = v;
    }

    /**
     * Calcula la posición del objeto en un tiempo dado.
     * Fórmula: x = x0 + v*t
     * 
     * @param t Tiempo transcurrido en segundos
     * @return Posición actual en metros
     */
    public double positionAt(double t) {
        return x0 + v * t;
    }

    /**
     * Calcula el desplazamiento desde la posición inicial.
     * 
     * @param t Tiempo transcurrido en segundos
     * @return Desplazamiento en metros (puede ser negativo si v < 0)
     */
    public double displacementAt(double t) {
        return positionAt(t) - x0;
    }

    /**
     * Calcula el tiempo necesario para llegar a una posición destino.
     * Fórmula: t = (xDestino - x0) / v
     * 
     * @param xDestino Posición destino en metros
     * @return Tiempo necesario en segundos, o infinito si v = 0
     */
    public double timeToReach(double xDestino) {
        if (v == 0) return Double.POSITIVE_INFINITY;
        return (xDestino - x0) / v;
    }

    /**
     * Obtiene la posición inicial del objeto.
     * 
     * @return Posición inicial en metros
     */
    public double getX0() {
        return x0;
    }

    /**
     * Obtiene la velocidad constante del objeto.
     * 
     * @return Velocidad en m/s
     */
    public double getVelocity() {
        return v;
    }
}
