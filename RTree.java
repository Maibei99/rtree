import org.w3c.dom.css.Rect;
import structures.Rectangle;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.*;

class RTree {
    // Dado un conjunto R de n rectángulos
    Rectangle root;
    int n;
    int M; // Cantidad máxima de hijos de cada nodo del árbol
    int diskAccessCount;

    public RTree(int M, String filename) throws IOException {
        this.M = M;
    }
    public RTree(int M) {
        this.M = M;
    }

    // Construcción usando NearestX
    // Construcción usando NearestX
    public void buildNearestXTree(List<Rectangle> rectangles) {
        // Se ordenan los rectángulos según la coordenada X del centro del rectángulo
        Collections.sort(rectangles, Comparator.comparing(Rectangle::centerX));
        this.n = rectangles.size();
        this.root = buildNodes(rectangles);
    }

    // Construcción usando Hilbert
    public void buildHilbertTree(List<Rectangle> rectangles) {
        Collections.sort(rectangles, Comparator.comparing(Rectangle :: hilbertValue));
        this.n = rectangles.size();
        this.root = buildNodes(rectangles);
    }

    // Construcción usando STR
    public void buildSTRtree(List<Rectangle> rectangles){
        this.n = rectangles.size();
        this.root = buildNodesSTR(rectangles);
    }

    private Rectangle buildNodesSTR(List<Rectangle> r){
        if (r.size() == 0) {
            return new Rectangle(0,0,0,0);
        }
        Collections.sort(r, Comparator.comparing(Rectangle :: centerX));

        // Array para ir guardando el nivel actual
        List<Rectangle> rectangles = r;
        // Buffer para guardar el nivel próximo
        List<Rectangle> level = new ArrayList<>();

        int s = (int) Math.round(Math.sqrt((double) rectangles.size() / M));
        while (rectangles.size() > M) {
            s = (int) Math.round(Math.sqrt((double) rectangles.size() / M));

            if (s * M < rectangles.size()) {
                s++;
            }

            for (int i = 0; i < rectangles.size(); i += s*M) {
                int end = Math.min(i + M*s, rectangles.size());
                System.out.println("end:" + end);
                List<Rectangle> group = rectangles.subList(i, end);

                Collections.sort(group, Comparator.comparing(Rectangle :: centerY));
                for (int j = 0; j < group.size(); j += M) {
                    int end2 = Math.min(j + M, group.size());
                    List<Rectangle> subGroup = group.subList(j, end2);
                    Rectangle R = computeMBR(subGroup);
                    System.out.println("s:"+ s);
                    System.out.println("[" + R.x1 + "," + R.y1 + "][" + R.x2 + "," + R.y2 + "]");
                    R.children = subGroup;
                    level.add(R);
                }
                rectangles = level;
                level = new ArrayList<>();
            }
        }

        Rectangle node = computeMBR(rectangles);
        node.children = rectangles;
        return node;
    }

    // juntamos en grupos de tamaño M
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
                children.add(R);
            }
            rectangles = children;
            children = new ArrayList<>();
        }

        Rectangle node = computeMBR(rectangles);
        node.children = rectangles;

        return node;
    }

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

    public List<Rectangle> search(Rectangle query) {
        this.diskAccessCount = 0; // Reiniciar el contador cada vez que iniciamos una búsqueda
        return recursiveSearch(root, query, new ArrayList<>());
    }

    private List<Rectangle> recursiveSearch(Rectangle node, Rectangle query, List<Rectangle> result) {

        if (!node.intersects(query)) {
            return result;
        }
        if (node.children.isEmpty()) { // Si es un nodo hoja
            if (node.intersects(query)) {
                result.add(node);
            }
            return result;
        }
        if (node.intersects(query)){
            diskAccessCount++;

        }
        for (Rectangle child : node.children) {
            recursiveSearch(child, query, result);
        }

        return result;
    }

    public static void main(String[] args) {
        RTree tree = new RTree(2);
        List<Rectangle> rectangles = new ArrayList<>();
        rectangles.add(new Rectangle(1, 1, 3, 3));
        rectangles.add(new Rectangle(2, 2, 4, 4));
        rectangles.add(new Rectangle(3, 1, 5, 3));
        rectangles.add(new Rectangle(4, 2, 6, 4));
        tree.buildNearestXTree(rectangles);

        Rectangle query = new Rectangle(2, 2, 5, 4);
        List<Rectangle> results = tree.search(query);

        System.out.println("Rectangles intersecting with query:");
        for (Rectangle r : results) {
            System.out.println("[" + r.x1 + "," + r.y1 + "][" + r.x2 + "," + r.y2 + "]");
        }
        System.out.println("Disk accesses during search: " + tree.diskAccessCount);

        List<Rectangle> rectangles2 = Arrays.asList(
                new Rectangle(0,0,1,1),
                new Rectangle(2,2,3,3),
                new Rectangle(4,4,5,5),
                new Rectangle(6,6,7,7),
                new Rectangle(8,8,9,9)
        );

        RTree rTree = new RTree(2);

        rTree.buildNearestXTree(rectangles2);
        Rectangle query2 = new Rectangle(0,0,4,4);

        List<Rectangle> results2 = rTree.search(query2);

        System.out.println("Rectangles intersecting with query:");
        for (Rectangle r : results2) {
            System.out.println("[" + r.x1 + "," + r.y1 + "][" + r.x2 + "," + r.y2 + "]");
        }
        System.out.println("Disk accesses during search: " + rTree.diskAccessCount);
        System.out.println("---------------------------------");

        RTree hilbertTree = new RTree(2);
        hilbertTree.buildHilbertTree(rectangles2);
        Rectangle query3 = new Rectangle(0,0,4,4);
        List<Rectangle> results3 = hilbertTree.search(query3);

        System.out.println("Rectangles intersecting with query:");
        for (Rectangle r : results3) {
            System.out.println("[" + r.x1 + "," + r.y1 + "][" + r.x2 + "," + r.y2 + "]");
        }
        System.out.println("Disk accesses during search: " + rTree.diskAccessCount);
        System.out.println("---------------------------------");

        RTree STRtree = new RTree(2);
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

        STRtree.buildSTRtree(rectangles4);
        Rectangle strQuery = new Rectangle(1,2,4,3);
        List<Rectangle> results4 = STRtree.search(strQuery);

        System.out.println("STR: Rectangles intersecting with query:");
        for (Rectangle r : results4) {
            System.out.println("[" + r.x1 + "," + r.y1 + "][" + r.x2 + "," + r.y2 + "]");
        }
        System.out.println("Disk accesses during search: " + STRtree.diskAccessCount);


    }
}