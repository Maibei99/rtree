package structures;
import java.util.ArrayList;
import java.util.List;
import pointers.*;

/**
 * Representa un rectángulo con capacidad para contener rectángulos hijos,
 * lo que permite representar la estructura de un R-tree.
 */
public class Rectangle {
    // Coordenadas del rectángulo.
    public int x1;
    public int y1;
    public int x2;
    public int y2;

    // Lista de rectángulos hijos.
    public List<Rectangle> children;

    // Resolución para la función de la curva de Hilbert.
    private static final int HILBERT_RESOLUTION = 500000;
    public boolean isLeaf;

    /**
     * Constructor de la clase Rectangle.
     *
     * @param x1 Coordenada x del punto superior izquierdo.
     * @param y1 Coordenada y del punto superior izquierdo.
     * @param x2 Coordenada x del punto inferior derecho.
     * @param y2 Coordenada y del punto inferior derecho.
     */
    public Rectangle(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.children = new ArrayList<>();
    }

    /**
     * Determina si el rectángulo es una hoja (no tiene hijos).
     *
     * @return Verdadero si el rectángulo es una hoja, falso de lo contrario.
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }

    /**
     * Retorna el valor x del centro del rectángulo.
     *
     * @return Valor x del centro.
     */
    public int centerX() {
        return (x1 + x2) / 2;
    }

    /**
     * Retorna el valor y del centro del rectángulo.
     *
     * @return Valor y del centro.
     */
    public int centerY() {
        return (y1 + y2) / 2;
    }

    /**
     * Determina si este rectángulo se intersecta con otro rectángulo.
     *
     * @param other Otro rectángulo con el que verificar la intersección.
     * @return Verdadero si se intersectan, falso de lo contrario.
     */
    public boolean intersects(Rectangle other) {
        return !(other.x2 < this.x1 ||
                other.x1 > this.x2 ||
                other.y2 < this.y1 ||
                other.y1 > this.y2);
    }

    /**
     * Calcula el valor basado en la curva de Hilbert para el rectángulo.
     *
     * @return El valor de Hilbert para el rectángulo.
     */
    public int hilbertValue(){
        int x = centerX();
        int y = centerY();
        int d = 0;
        Pointer rx = new Pointer(0);
        Pointer ry = new Pointer(0);
        for (int s = HILBERT_RESOLUTION / 2; s > 0; s /= 2) {
            rx.value = (x & s) > 0 ? 1 : 0;
            ry.value = (y & s) > 0 ? 1 : 0;
            d += s * s * ((3 * rx.value) ^ ry.value);
            rot(s, rx, ry);
        }
        return d;
    }

    /**
     * Función auxiliar para el cálculo de la curva de Hilbert.
     *
     * @param s Resolución actual para el cálculo.
     * @param rx Puntero para el valor x.
     * @param ry Puntero para el valor y.
     */
    private void rot(int s, Pointer rx, Pointer ry) {
        if (ry.value == 0) {
            if (rx.value == 1) {
                rx.value = HILBERT_RESOLUTION - 1 - rx.value;
                ry.value = HILBERT_RESOLUTION - 1 - ry.value;
            }
            int t = rx.value;
            rx.value = ry.value;
            ry.value = t;
        }
    }
}