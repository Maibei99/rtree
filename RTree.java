import structures.Rectangle;

import java.io.IOException;
import java.util.*;

/**
 * Clase que representa un árbol R-Tree.
 *
 */
public class RTree {
    /**
     * Raiz del árbol.
     */
    Rectangle root;
    /**
     * Cantidad de datos (rectángulos) en el árbol.
     */
    int n;
    /**
     * Cantidad máxima de hijos de cada nodo del árbol.
     */
    int M;
    /**
     * Cantidad de accesos a disco realizados durante la última búsqueda.
     */
    int diskAccessCount;

    /**
     * Constructor de la clase. Recibe como parámetro la cantidad máxima de hijos
     * de cada nodo del árbol.
     * @param M Cantidad máxima de hijos de cada nodo del árbol.
     */
    public RTree(int M) {
        this.M = M;
        this.diskAccessCount = 0;
        this.n = 0;
    }

    /**
     * Getter de la cantidad de accesos a disco realizados durante la última búsqueda.
     * @return Cantidad de accesos a disco realizados durante la última búsqueda.
     */
    public int getDiskAccessCount() {
        return diskAccessCount;
    }

    /**
     * Construye un árbol R-Tree usando el algoritmo NearestX. El árbol se construye
     * sobre el conjunto de rectángulos que se pasan como parámetro. Primero, ordena
     * los rectángulos según su coordenada X del centro. Luego, construye el árbol
     * iterativamente, desde las hojas, agrupando en rectángulos de tamaño M.
     *
     * @param rectangles Conjunto de rectángulos sobre el cual se construye el árbol.
     *
     */
    public void buildNearestXTree(List<Rectangle> rectangles) {
        // Se ordenan los rectángulos según la coordenada X del centro del rectángulo
        Collections.sort(rectangles, Comparator.comparing(Rectangle::centerX));
        this.n = rectangles.size();
        this.root = buildNodes(rectangles);
    }

    /**
     * Construye un árbol R-Tree usando el algoritmo Hilbert. El árbol se construye
     * sobre el conjunto de rectángulos que se pasan como parámetro. Primero, ordena
     * los rectángulos según su valor Hilbert. Luego, construye el árbol iterativamente,
     * desde las hojas, agrupando en rectángulos de tamaño M.
     * @param rectangles Conjunto de rectángulos sobre el cual se construye el árbol.
     */
    public void buildHilbertTree(List<Rectangle> rectangles) {
        Collections.sort(rectangles, Comparator.comparing(Rectangle :: hilbertValue));
        this.n = rectangles.size();
        this.root = buildNodes(rectangles);
    }

    /**
     * Construye un árbol R-Tree usando el algoritmo STR. El árbol se construye
     * sobre el conjunto de rectángulos que se pasan como parámetro.
     * @param rectangles Conjunto de rectángulos sobre el cual se construye el árbol.
     */
    public void buildSTRTree(List<Rectangle> rectangles){
        this.n = rectangles.size();
        this.root = buildNodesSTR(rectangles);
    }

    /**
     * Construye un árbol R-Tree usando el algoritmo STR. El árbol se construye
     * sobre el conjunto de rectángulos que se pasan como parámetro. De forma iterativa,
     * ordena los rectángulos según so centro X, luego los agrupa en grupos de tamaño
     * M*S, donde S es la raíz cuadrada de la cantidad de rectángulos sobre M. Luego,
     * ordena los grupos según su centro Y, y los agrupa en grupos de tamaño M. El proceso
     * se repite hasta que quede un único grupo, que representa la raíz del árbol.
     * @param r Conjunto de rectángulos sobre el cual se construye el árbol.
     * @return Rectángulo que representa la raíz del árbol.
     */
    private Rectangle buildNodesSTR(List<Rectangle> r){
        if (r.size() == 0) {
            return new Rectangle(0,0,0,0);
        }
        // Array para ir guardando el nivel actual
        List<Rectangle> rectangles = r;
        // Buffer para guardar el nivel próximo
        List<Rectangle> level = new ArrayList<>();
        int s;
        while (rectangles.size() > M) {
            s = (int) Math.round(Math.sqrt((double) rectangles.size() / M));
            if (s * M < rectangles.size()) {
                s++;
            }
            Collections.sort(rectangles, Comparator.comparing(Rectangle :: centerX));
            for (int i = 0; i < rectangles.size(); i += s*M) {
                int end = Math.min(i + M*s, rectangles.size());
                List<Rectangle> group = rectangles.subList(i, end);

                Collections.sort(group, Comparator.comparing(Rectangle :: centerY));
                for (int j = 0; j < group.size(); j += M) {
                    int end2 = Math.min(j + M, group.size());
                    List<Rectangle> subGroup = group.subList(j, end2);
                    Rectangle R = computeMBR(subGroup);
                    R.children = subGroup;
                    level.add(R);
                }
            }
            rectangles = level;
            level = new ArrayList<>();
        }

        Rectangle node = computeMBR(rectangles);
        node.children = rectangles;
        return node;
    }

    /**
     * Construye un árbol R-Tree usando la lista de rectángulos ordenados que se pasan como
     * parámetro. Los rectángulos pueden estar ordenados según su centro X, si se llama desde
     * el método buildNearestXTree, o según su valor Hilbert, si se llama desde el método
     * buildHilbertTree. De forma iterativa, agrupa los rectángulos en grupos de tamaño M,
     * y luego calcula el MBR de cada grupo. El proceso se repite hasta que quede un único
     * grupo, que representa la raíz del árbol.
     * @param sortedRectangles Lista de rectángulos ordenados según su centro X o su valor
     *                         Hilbert.
     * @return Rectángulo que representa la raíz del árbol.
     */
    private Rectangle buildNodes(List<Rectangle> sortedRectangles) {
        if (sortedRectangles.size() == 0) {
            return new Rectangle(0,0,0,0);
        }

        // Array para ir guardando los niveles
        List<Rectangle> rectangles = sortedRectangles;
        // Array para guardar los rectángulos que se formarán
        List<Rectangle> children = new ArrayList<>();

        while (rectangles.size() > M) {
            for (int i = 0; i < rectangles.size(); i += M) {
                int end = Math.min(i + M, rectangles.size());
                List<Rectangle> group = rectangles.subList(i, end);
                Rectangle R = computeMBR(group);
                R.children = group;
                R.isLeaf = false;
                children.add(R);
            }
            rectangles = children;
            children = new ArrayList<>();
        }

        Rectangle node = computeMBR(rectangles);
        node.children = rectangles;

        return node;
    }

    /**
     * Calcula el MBR de un conjunto de rectángulos.
     * @param rectangles Conjunto de rectángulos.
     * @return Rectángulo que representa el MBR del conjunto de rectángulos.
     */
    private Rectangle computeMBR(List<Rectangle> rectangles) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        for (Rectangle r : rectangles) {
            minX = Math.min(minX, r.x1);
            minX = Math.min(minX, r.x2);

            minY = Math.min(minY, r.y1);
            minY = Math.min(minY, r.y2);

            maxX = Math.max(maxX, r.x1);
            maxX = Math.max(maxX, r.x2);

            maxY = Math.max(maxY, r.y1);
            maxY = Math.max(maxY, r.y2);
        }

        return new Rectangle(minX, minY, maxX, maxY);
    }

    /**
     * Busca en el árbol los rectángulos que intersectan con el rectángulo que se pasa
     * como parámetro.
     * @param query Rectángulo que representa la consulta.
     */
    public void search(Rectangle query) {
        this.diskAccessCount = 0; // Reiniciar el contador cada vez que iniciamos una búsqueda
        recursiveSearch(root, query);
    }


    private void recursiveSearch(Rectangle node, Rectangle query) {

        if (!node.intersects(query)) {
            return ;
        }

        if (node.children.isEmpty()) {
            if (node.intersects(query)) {
                System.out.println("[" + node.x1 + "," + node.y1 + "][" + node.x2 + "," + node.y2 + "]");
            }
            return ;
        }

        if (node.intersects(query)) {
            diskAccessCount++;
        }

        for (Rectangle child : node.children) {
            recursiveSearch(child, query);
        }
    }

    public List<Rectangle> search2(Rectangle query) {
        this.diskAccessCount = 0; // Reiniciar el contador cada vez que iniciamos una búsqueda

        Stack<Rectangle> stack = new Stack<>();
        stack.push(root);

        List<Rectangle> results = new ArrayList<>();

        while (!stack.isEmpty()) {
            Rectangle node = stack.pop();

            if (!node.intersects(query)) {
                continue;
            }

            if (node.isLeaf) {
                results.add(node);
                continue;
            }

            diskAccessCount++;

            for (Rectangle child : node.children) {
                stack.push(child);
            }
        }

        return results;
    }

    public static void main(String[] args) {

        RTree NXTree = new RTree(5);
        RTree hilbertTree = new RTree(5);
        RTree STRTree = new RTree(5);
        List<Rectangle> rectangles4 = Arrays.asList(
                new Rectangle(1,1,1,1),
                new Rectangle(1,2,1,2),
                new Rectangle(1,4,1,4),
                new Rectangle(1,6,1,6),
                new Rectangle(1,10,1,10),

                new Rectangle(2,3,2,3),
                new Rectangle(2,5,2,5),
                new Rectangle(2,6,2,6),
                new Rectangle(2,8,2,8),
                new Rectangle(2,9,2,9),

                new Rectangle(3,2,3,2),
                new Rectangle(3,5,3,5),
                new Rectangle(3,7,3,7),
                new Rectangle(3,8,3,8),
                new Rectangle(3,10,3,10),

                new Rectangle(4,1,4,1),
                new Rectangle(4,3,4,3),
                new Rectangle(4,5,4,5),
                new Rectangle(4,7,4,7),
                new Rectangle(4,9,4,9),

                new Rectangle(5,2,5,2),
                new Rectangle(5,5,5,5),
                new Rectangle(5,6,5,6),
                new Rectangle(5,9,5,9),
                new Rectangle(5,10,5,10),

                new Rectangle(6,1,6,1),
                new Rectangle(6,3,6,3),
                new Rectangle(6,4,6,4),
                new Rectangle(6,7,6,7),
                new Rectangle(6,9,6,9),

                new Rectangle(7,1,7,1),
                new Rectangle(7,2,7,2),
                new Rectangle(7,4,7,4),
                new Rectangle(7,6,7,6),
                new Rectangle(7,8,7,8),

                new Rectangle(8,2,8,2),
                new Rectangle(8,4,8,4),
                new Rectangle(8,6,8,6),
                new Rectangle(8,8,8,8),
                new Rectangle(8,10,8,10),

                new Rectangle(9,1,9,1),
                new Rectangle(9,3,9,3),
                new Rectangle(9,4,9,4),
                new Rectangle(9,6,9,6),
                new Rectangle(9,9,9,9),

                new Rectangle(10, 1, 10, 1),
                new Rectangle(10,3,10,3),
                new Rectangle(10,5,10,5),
                new Rectangle(10,7,10,7),
                new Rectangle(10,9,10,9)
        );
        NXTree.buildNearestXTree(rectangles4);
        hilbertTree.buildHilbertTree(rectangles4);
        STRTree.buildSTRTree(rectangles4);
        Rectangle strQuery = new Rectangle(1,2,4,3);

        System.out.println("Rectangles intersecting with query:");
        List<Rectangle> resultsNX = NXTree.search2(strQuery);
        for (Rectangle r : resultsNX) {
            System.out.println("[" + r.x1 + "," + r.y1 + "][" + r.x2 + "," + r.y2 + "]");
        }
        System.out.println("Disk accesses during search: " + NXTree.diskAccessCount);

        System.out.println("---------------------------------");
        System.out.println("Rectangles intersecting with query:");
        List<Rectangle> resultsHilbert = hilbertTree.search2(strQuery);
        for (Rectangle r : resultsHilbert) {
            System.out.println("[" + r.x1 + "," + r.y1 + "][" + r.x2 + "," + r.y2 + "]");
        }
        System.out.println("Disk accesses during search: " + hilbertTree.diskAccessCount);

        System.out.println("---------------------------------");
        System.out.println("Rectangles intersecting with query:");
        List<Rectangle> resultsSTR = STRTree.search2(strQuery);
        for (Rectangle r : resultsSTR) {
            System.out.println("[" + r.x1 + "," + r.y1 + "][" + r.x2 + "," + r.y2 + "]");
        }
        System.out.println("Disk accesses during search: " + STRTree.diskAccessCount);
    }
}