package Libreria.ordinamento;

import Libreria.service.Libro;
import java.util.*;


public class OrdinaPerTitolo implements Ordinamento{
    @Override
    public void ordina(ArrayList<Libro> lista) {
        Comparator<Libro> compara_titoli = new Comparator<Libro>() {
            public int compare(Libro l1, Libro l2) {
                return l1.getTitolo().compareToIgnoreCase(l2.getTitolo());
            }
        };
        lista.sort(compara_titoli);
    }
}
