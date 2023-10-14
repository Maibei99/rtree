package structures;

import pointers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.nio.ByteBuffer;

public class Rectangle {
    public static final int MAX_SIZE = 500000;
    public int x1;
    public int y1;
    public int x2;
    public int y2;
    public List<Rectangle> children;
    public long id; // Identificador único para el archivo

    public Rectangle(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.children = new ArrayList<>(); // Por defecto es nulo
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public int centerX() {
        return (x1 + x2) / 2;
    }

    public int centerY() {
        return (y1 + y2) / 2;
    }

    public boolean intersects(Rectangle other) {
        return !(other.x2 < this.x1 ||
                other.x1 > this.x2 ||
                other.y2 < this.y1 ||
                other.y1 > this.y2);
    }

    public int hilbertValue(){
        int x = centerX();
        int y = centerY();
        int d = 0;
        Pointer rx = new Pointer(0);
        Pointer ry = new Pointer(0);
        for (int s = MAX_SIZE /2; s > 0; s/= 2) {
            if ((x & s) > 0) {
                rx.value = 1;
            }
            if ((y & s) > 0) {
                ry.value = 1;
            }
            d += s * s * ((3 * rx.value) ^ ry.value);
            rot(x, y, rx, ry);
        }
        return d;
    }


    public void rot(int x, int y, Pointer rx, Pointer ry){
        int t = 0;
        if (ry.value == 0) {
            if (rx.value == 1) {
                rx.value = MAX_SIZE - 1 - rx.value;
                ry.value = MAX_SIZE - 1 - ry.value;
            }
            t = rx.value;
        }
        rx.value =  ry.value;
        ry.value = t;
    }


    public static Rectangle generateRandomRectangle(int maxSize) {
        Random random = new Random();
        int x1 = random.nextInt(MAX_SIZE + 1);
        int y1 = random.nextInt(MAX_SIZE + 1);
        int width = random.nextInt(maxSize + 1);
        int height = random.nextInt(maxSize + 1);
        return new Rectangle(x1, y1, x1 + width, y1 + height);
    }

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(24 + (children.size() * 16)); // 24 porque añadimos la cantidad de hijos
        buffer.putLong(id);
        buffer.putInt(x1);
        buffer.putInt(y1);
        buffer.putInt(x2);
        buffer.putInt(y2);
        buffer.putInt(children.size()); // Aquí añadimos la cantidad de hijos
        for (Rectangle child : children) {
            buffer.putInt(child.x1);
            buffer.putInt(child.y1);
            buffer.putInt(child.x2);
            buffer.putInt(child.y2);
        }
        return buffer.array();
    }


    public static Rectangle fromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long id = buffer.getLong();
        int x1 = buffer.getInt();
        int y1 = buffer.getInt();
        int x2 = buffer.getInt();
        int y2 = buffer.getInt();
        int childrenCount = buffer.getInt(); // Leer la cantidad de hijos
        Rectangle rect = new Rectangle(x1, y1, x2, y2);
        rect.id = id;
        for (int i = 0; i < childrenCount; i++) { // Usar childrenCount en lugar de M
            x1 = buffer.getInt();
            y1 = buffer.getInt();
            x2 = buffer.getInt();
            y2 = buffer.getInt();
            rect.children.add(new Rectangle(x1, y1, x2, y2));
        }
        return rect;
    }

}

//convierte (x,y) a d
    /*int xy2d (int n, int x, int y) {
        int rx, ry, s, d=0;
        for (s = n/2; s > 0; s /= 2) {
            rx = (x & s) > 0;
            ry = (y & s) > 0;
            d += s * s * ((3 * rx) ^ ry);
            rot(s, &x, &y, rx, ry);
        }
        return d;
    }
//rotar/voltear un cuadrante apropiadamente
    void rot(int n, int *x, int *y, int rx, int ry) {
        int t;
        if (ry == 0) {
            if (rx == 1) {
            *x = n-1 - *x;
            *y = n-1 - *y;
            }
            t  = *x;
        *x = *y;
        *y = t;
        }
    }
   */
