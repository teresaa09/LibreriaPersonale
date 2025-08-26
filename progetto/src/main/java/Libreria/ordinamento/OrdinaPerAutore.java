package Libreria.ordinamento;

import Libreria.service.Libro;

import java.util.ArrayList;
import java.util.Comparator;

public class OrdinaPerAutore implements Ordinamento{
    @Override
    public void ordina(ArrayList<Libro> lista) {

        Comparator<Libro> compara_autori = new Comparator<Libro>() {
            public int compare(Libro l1, Libro l2) {
                return l1.getAutore().compareToIgnoreCase(l2.getAutore());
            }
        };
        lista.sort(compara_autori);
    }
}
