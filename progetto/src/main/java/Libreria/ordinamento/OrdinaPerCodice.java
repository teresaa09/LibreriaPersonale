package Libreria.ordinamento;

import Libreria.service.Libro;

import java.util.ArrayList;
import java.util.Comparator;

public class OrdinaPerCodice implements Ordinamento {
        @Override
        public void ordina(ArrayList<Libro> lista) {
            Comparator<Libro> compara_codice = new Comparator<Libro>() {
                public int compare(Libro l1, Libro l2) {
                    return l1.getCodiceISBN().compareToIgnoreCase(l2.getCodiceISBN());
                }
            };
            lista.sort(compara_codice);
        }
    }

