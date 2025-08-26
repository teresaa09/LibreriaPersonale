package Libreria.ordinamento;

import Libreria.service.Libro;

import java.util.ArrayList;
import java.util.Comparator;
import Libreria.service.StatoDellaLettura;

public class OrdinaPerStato implements Ordinamento {
    @Override
    public void ordina(ArrayList<Libro> lista) {
        Comparator<Libro> compara_status = new Comparator<Libro>() {
            public int compare(Libro l1, Libro l2) {
                return Integer.compare(priorità(l1.getStatus()),priorità(l2.getStatus()));
            }
        };
        lista.sort(compara_status);
    }

    private int priorità(StatoDellaLettura status){
        switch(status){
            case DA_LEGGERE:
                return 0;
            case IN_LETTURA:
                return 1;
            case LETTO:
                return 2;
        }
        return 7;
    }

}
