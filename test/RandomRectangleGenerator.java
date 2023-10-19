package test;
import structures.Rectangle;
import rtree.RTree;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase para generar rectángulos aleatorios
 */
public class RandomRectangleGenerator {

    /**
     * Método para generar un número aleatorio entre un rango.
     * @param min Valor mínimo.
     * @param max Valor máximo..
     * @return Entero aleatorio.
     */
    private static int generarAleatorio(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    /**
     * Método para generar un set de rectángulos con tamaños aleatorios. Los rectángulos
     * generados tienen un tamaño máximo de 100x100. Los valores de las coordenadas
     * de los rectángulos están en el rango [0, 500000].
     * @param n Cantidad de rectángulos a generar.
     * @return Lista de rectángulos.
     */
    public static List<Rectangle> generarSetRectangulos(int n) {
        List<Rectangle> rectangulos = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int x1 = generarAleatorio(0, 500000);
            int y1 = generarAleatorio(0, 500000);
            int x2 = Math.min(x1 + generarAleatorio(0, 100), 500000);
            int y2 = Math.min(y1 + generarAleatorio(0, 100), 500000);
            rectangulos.add(new Rectangle(x1, y1, x2, y2));
        }
        return rectangulos;
    }

    /**
     * Método para generar un set de consultas con tamaños aleatorios. Los rectángulos
     * generados tienen un tamaño máximo de 100000x100000. Los valores de las coordenadas
     * de los rectángulos están en el rango [0, 500000].
     * @param n Cantidad de rectángulos a generar.
     * @return Lista de rectángulos.
     */
    public static List<Rectangle> generarSetConsultas(int n) {
        List<Rectangle> consultas = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int x1 = generarAleatorio(0, 500000);
            int y1 = generarAleatorio(0, 500000);
            int x2 = Math.min(x1 + generarAleatorio(0, 100000), 500000);
            int y2 = Math.min(y1 + generarAleatorio(0, 100000), 500000);
            consultas.add(new Rectangle(x1, y1, x2, y2));
        }
        return consultas;
    }
}
