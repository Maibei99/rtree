import structures.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RectangleGenerator {
    // Método para generar un valor aleatorio entre min y max
    private static int generarAleatorio(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    // Método para generar un set de rectángulos con tamaños aleatorios
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

    // Método para generar un set de consultas con tamaños aleatorios
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

    public static void main(String[] args) {
        //int[] nValues = {1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576,
        //        2097152, 4194304, 8388608, 16777216, 33554432};
        /*
        for (int n : nValues) {
            List<Rectangle> rectangulos = generarSetRectangulos(n);
            List<Rectangle> consultas = generarSetConsultas(n);

            // Do Stuff
        }
        */

        int n = 1024;

        List<Rectangle> rectangulos = generarSetRectangulos(n);
        List<Rectangle> consultas = generarSetConsultas(100);


        RTree nxTree = new RTree(100);
        nxTree.buildNearestXTree(rectangulos);

        RTree hilbertTree = new RTree(100);
        hilbertTree.buildHilbertTree(rectangulos);

        RTree strTree = new RTree(100);
        strTree.buildSTRTree(rectangulos);

        int[] accesosNX = new int[100];
        int[] accesosHilbert = new int[100];
        int[] accesosSTR = new int[100];

        for (Rectangle consulta : consultas) {
            nxTree.search(consulta);
            accesosNX[consultas.indexOf(consulta)] = nxTree.getDiskAccessCount();
        }

        for (Rectangle consulta : consultas) {
            hilbertTree.search(consulta);
            accesosHilbert[consultas.indexOf(consulta)] = hilbertTree.getDiskAccessCount();
        }

        for (Rectangle consulta : consultas) {
            strTree.search(consulta);
            accesosSTR[consultas.indexOf(consulta)] = strTree.getDiskAccessCount();
        }

        // Obtenemos el promedio de accesos de cada árbol
        double promedioNX = 0;
        double promedioHilbert = 0;
        double promedioSTR = 0;

        for (int i = 0; i < 100; i++) {
            promedioNX += accesosNX[i];
            promedioHilbert += accesosHilbert[i];
            promedioSTR += accesosSTR[i];
        }

        promedioNX /= 100;
        promedioHilbert /= 100;
        promedioSTR /= 100;

        System.out.println("Promedio NX: " + promedioNX);
        System.out.println("Promedio Hilbert: " + promedioHilbert);
        System.out.println("Promedio STR: " + promedioSTR);
    }
}