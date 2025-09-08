package Libreria.iterazione;

import Libreria.service.Libro;
import java.util.List;

public class LibreriaIterator implements Iterator<Libro> {

    private List<Libro> lista;
    private int corr = 0;
    private int rimosso = -1;

    public LibreriaIterator(List<Libro> lista) {
        this.lista = lista;
    }

    @Override
    public boolean hasNext() {
        return corr < lista.size();
    }

    @Override
    public Libro next() {
        if (!hasNext()) {
            throw new IllegalStateException("Scansione lista conclusa");
        }
        rimosso = corr;
        return lista.get(corr++);
    }

    @Override
    public void remove() {
        if (rimosso == -1) {
            throw new IllegalStateException("next() non è stato ancora chiamato o remove() già usato");
        }
        lista.remove(rimosso);
        corr = rimosso;
        rimosso = -1;
    }
}