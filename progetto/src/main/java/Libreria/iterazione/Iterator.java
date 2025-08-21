package Libreria.iterazione;

public interface Iterator<T> {
    boolean hasNext();
    T next();
    void remove();
}