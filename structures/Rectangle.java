package structures;
import java.util.ArrayList;
import java.util.List;
import pointers.*;

/**
 * Clase para representar un rectángulo. Se utiliza para representar los datos
 * y los nodos del RTree.
 */
public class Rectangle {
    /**
     * Coordenada x del punto inferior izquierdo.
     */
    public int x1;
    /**
     * Coordenada y del punto inferior izquierdo.
     */
    public int y1;
    /**
     * Coordenada x del punto superior derecho.
     */
    public int x2;
    /**
     * Coordenada y del punto superior derecho.
     */
    public int y2;
    /**
     * Lista de hijos. Si es un nodo hoja, es nulo.
     */
    public List<Rectangle> children;
    /**
     * Booleano que indica si es un nodo hoja.
     */
    public boolean isData;


    /**
     * Constructor de la clase. Si las coordenadas no corresponde, las arregla.
     * @param x1 Coordenada x del punto inferior izquierdo.
     * @param y1 Coordenada y del punto inferior izquierdo.
     * @param x2 Coordenada x del punto superior derecho.
     * @param y2 Coordenada y del punto superior derecho.
     */
    public Rectangle(int x1, int y1, int x2, int y2) {
        int minX = Math.min(x2, x1);
        int minY = Math.min(y2, y1);
        int maxX = Math.max(x2, x1);
        int maxY = Math.max(y2, y1);

        this.x1 = minX;
        this.y1 = minY;
        this.x2 = maxX;
        this.y2 = maxY;
        this.children = new ArrayList<>(); // Por defecto es nulo
        this.isData = true;
    }

    /**
     * Método para obtener el centro en la coordenada X.
     * @return Centro en la coordenada X.
     */
    public int centerX() {
        return (x1 + x2) / 2;
    }

    /**
     * Método para obtener el centro en la coordenada Y.
     * @return Centro en la coordenada Y.
     */
    public int centerY() {
        return (y1 + y2) / 2;
    }

    /**
     * Método para identificar si dos rectángulos se intersectan.
     * @param other Otro rectángulo.
     * @return Booleano que indica si se intersectan.
     */
    public boolean intersects(Rectangle other) {
        return !(other.x2 < this.x1 ||
                other.x1 > this.x2 ||
                other.y2 < this.y1 ||
                other.y1 > this.y2);
    }


    /**
     * Método para obtener la distancia del centro X del rectángulo a la curva de Hilbert
     * más grande que cubra la grilla. La grilla es de 500000x500000.
     * @return Distancia del centro X a la curva de Hilbert.
     */
    public int hilbertValue(){
        Pointer x = new Pointer(centerX());
        Pointer y = new Pointer(centerY());
        int d = 0;
        int rx = 0;
        int ry = 0;
        for (int s = 500000/2; s > 0; s/= 2) {
            if ((x.value & s) > 0) {
                rx = 1;
            }
            if ((y.value & s) > 0) {
                ry = 1;
            }
            d += s * s * ((3 * rx) ^ ry);
            rot(s, x, y, rx, ry);
        }
        return d;
    }

    /**
     * Método auxiliar para calcular la distancia a la curva de Hilbert.
     * @param n Tamaño de la grilla.
     * @param x Coordenada x del centro.
     * @param y Coordenada y del centro.
     * @param rx Valor booleano que indica si se debe rotar.
     * @param ry Valor booleano que indica si se debe rotar.
     */
    //rotar/voltear un cuadrante apropiadamente
    private void rot(int n, Pointer x, Pointer y, int rx, int ry) {
        int t = 0;
        if (ry == 0) {
            if (rx == 1) {
            x.value = n-1 - x.value;
            y.value = n-1 - y.value;
            }
            t  = x.value;
        x.value = y.value;
        y.value = t;
        }
    }

}