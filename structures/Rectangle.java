package structures;
import java.util.ArrayList;
import java.util.List;
import pointers.*;

public class Rectangle {
    public int x1;
    public int y1;
    public int x2;
    public int y2;
    public List<Rectangle> children;

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
        for (int s = 500000/2; s > 0; s/= 2) {
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


    private void rot(int x, int y, Pointer rx, Pointer ry){
        int t = 0;
        if (ry.value == 0) {
            if (rx.value == 1) {
                rx.value = 500000 - 1 - rx.value;
                ry.value = 500000 - 1 - ry.value;
            }
            t = rx.value;
        }
        rx.value =  ry.value;
        ry.value = t;
    }
}