package Libreria.ordinamento;

import Libreria.service.Libro;

import java.util.ArrayList;
import java.util.Comparator;


public class OrdinaPerValutazione implements Ordinamento{
    @Override
    public void ordina(ArrayList<Libro> lista) {
        Comparator<Libro> compara_valutazioni = new Comparator<Libro>() {
            public int compare(Libro l1, Libro l2) {
                return Integer.compare(l1.getValutazione(), l2.getValutazione());
            }
        };
        lista.sort(compara_valutazioni);
    }
}
