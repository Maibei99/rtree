package pointers;

import java.io.Serializable;

/**
 * Clase para representar un puntero. Se utiliza para poder implementar
 * el algoritmo de Hilbert, que estaba originalmente en escrito en C.
 */
public class Pointer {
    public int value;

    public Pointer(int value) {
        this.value = value;
    }
}
