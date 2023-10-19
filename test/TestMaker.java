package test;

import rtree.RTree;
import structures.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase para generar tests. Se generan sets de rectángulos y consultas aleatorios
 * y se evalúan en los distintos RTree.
 */
public class TestMaker {
    /**
     * Cantidad de rectángulos.
     */
    int n;
    /**
     * Cantidad de hijos; depende del tamaño del bloque.
     */
    int m;
    /**
     * Lista de rectángulos.
     */
    List<Rectangle> rectangles;
    /**
     * Lista de consultas.
     */
    List<Rectangle> consultas;

    /**
     * Constructor de la clase.
     * @param n Cantidad de rectángulos.
     * @param m Cantidad de hijos; depende del tamaño del bloque.
     */
    public TestMaker(int n, int m) {
        this.n = n;
        this.m = m;
        this.rectangles = RandomRectangleGenerator.generarSetRectangulos(n);
        this.consultas = RandomRectangleGenerator.generarSetConsultas(100);
    }

    /**
     * Método para evaluar un RTree. Se evalúan las 100 consultas.
     * @param rTree RTree a evaluar.
     * @return Resultados de las pruebas.
     */
    private Results testTree(RTree rTree) {

        int[] diskAccesses = new int[100];
        double[] times = new double[100];

        for (int i = 0; i < 100; i++) {
            Rectangle queryRectangle = consultas.get(i);
            SearchEvaluator test = new SearchEvaluator(rTree, queryRectangle);
            diskAccesses[i] = test.getDiskAccessCount();
            times[i] = test.getTotalTime();
        }

        return new Results(times, diskAccesses, 100);
    }

    /**
     * Método para realizar las pruebas. Se evalúan los tres RTree.
     * @return Lista de resultados. Cada resultado corresponde a un RTree. El orden es:
     * NXTree, HilbertTree, STRTree.
     */
    public List<Results> test() {
        List<Results> results = new ArrayList<>();

        RTree NXTree = new RTree(m);
        NXTree.buildNearestXTree(rectangles);
        results.add(testTree(NXTree));

        RTree HilbertTree = new RTree(m);
        HilbertTree.buildHilbertTree(rectangles);
        results.add(testTree(HilbertTree));

        RTree STRTree = new RTree(m);
        STRTree.buildSTRTree(rectangles);
        results.add(testTree(STRTree));

        return results;
    }
}
