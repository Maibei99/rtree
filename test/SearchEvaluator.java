package test;

import rtree.RTree;
import structures.Rectangle;

/**
 * Clase para evaluar una consulta.
 */
public class SearchEvaluator {
    /**
     * Cantidad de accesos a disco.
     */
    private int diskAccessCount;
    /**
     * Tiempo total de ejecución.
     */
    private long totalTime;

    /**
     * Constructor de la clase.
     * @param rTree RTree a evaluar.
     * @param queryRectangle Rectángulo de consulta.
     */
    public SearchEvaluator(RTree rTree, Rectangle queryRectangle) {
        this.diskAccessCount = 0;
        this.totalTime = 0;
        evaluateSearch(rTree, queryRectangle);
    }

    /**
     * Método para evaluar una consulta.
     * @param rTree RTree a evaluar.
     * @param queryRectangle Rectángulo de consulta.
     */
    private void evaluateSearch(RTree rTree, Rectangle queryRectangle) {
        diskAccessCount = 0; // resetear contador
        long startTime = System.currentTimeMillis();

        rTree.search(queryRectangle);

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        totalTime += elapsedTime;
        diskAccessCount = rTree.getDiskAccessCount();
    }

    /**
     * Método para obtener la cantidad de accesos a disco.
     * @return Cantidad de accesos a disco.
     */
    public int getDiskAccessCount() {
        return diskAccessCount;
    }

    /**
     * Método para obtener el tiempo total de ejecución.
     * @return Tiempo total de ejecución.
     */
    public long getTotalTime() {
        return totalTime;
    }
}
