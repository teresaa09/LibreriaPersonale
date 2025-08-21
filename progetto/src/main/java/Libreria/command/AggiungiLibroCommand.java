package Libreria.command;

import Libreria.service.Libro;
import Libreria.service.Libreria;

public class AggiungiLibroCommand implements Command {
    private Libreria libreria;
    private Libro libro;

    public AggiungiLibroCommand(Libreria libreria, Libro libro) {
        this.libreria = libreria;
        this.libro = libro;
    }

    @Override
    public void esegui() {
        libreria.aggiungiLibro(libro);
    }

    @Override
    public void annulla() {
        libreria.rimuovi_libro(libro.getCodiceISBN());
    }
}