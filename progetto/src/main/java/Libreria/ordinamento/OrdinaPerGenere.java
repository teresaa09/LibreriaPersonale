package Libreria.ordinamento;

import Libreria.service.Libro;

import java.util.ArrayList;
import java.util.Comparator;

public class OrdinaPerGenere implements Ordinamento{
    @Override
    public void ordina(ArrayList<Libro> lista) {
        Comparator<Libro> comparaGeneri = new Comparator<Libro>() {
            @Override
            public int compare(Libro l1, Libro l2) {
                return l1.getGenere().compareTo(l2.getGenere());
            }
        };
        lista.sort(comparaGeneri);
    }
}
