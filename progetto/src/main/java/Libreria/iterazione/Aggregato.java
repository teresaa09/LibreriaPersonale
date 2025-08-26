package Libreria.iterazione;

public interface Aggregato<T> {
    Iterator<T> creaIterator();
}