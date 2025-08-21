package Libreria.command;

import Libreria.service.*;
import java.util.Iterator;

public class RimuoviLibroCommand implements Command {
    private Libreria libreria;
    private String isbn;
    private Libro libroRimosso; // per undo

    public RimuoviLibroCommand(Libreria libreria, String isbn) {
        this.libreria = libreria;
        this.isbn = isbn;
    }

    @Override
    public void esegui() {
        Iterator<Libro> it = (Iterator<Libro>) libreria.crea_iterator();
        while (it.hasNext()) {
            Libro l = it.next();
            if (l.getCodiceISBN().equals(isbn)) {
                libroRimosso = l;
                it.remove();
                libreria.notificaObservers("Libro rimosso: " + l.getTitolo());
                return;
            }
        }
        System.out.println("[Command] Libro non trovato: " + isbn);
    }

    public void annulla() {
        if (libroRimosso != null) {
            libreria.aggiungiLibro(libroRimosso);
        }
    }
}

