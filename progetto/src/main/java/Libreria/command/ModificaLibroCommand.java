package Libreria.command;

import Libreria.iterazione.Iterator;
import Libreria.iterazione.LibreriaIterator;
import Libreria.service.*;


public class ModificaLibroCommand implements Command {
    private Libreria libreria;
    private String isbn;
    private int nuovaValutazione;
    private StatoDellaLettura nuovoStatus;
    // Campi per undo
    private int vecchiaValutazione;
    private StatoDellaLettura vecchioStatus;


    public ModificaLibroCommand(Libreria libreria, String isbn, int nuovaValutazione, StatoDellaLettura nuovoStatus) {
        this.libreria = libreria;
        this.isbn = isbn;
        this.nuovaValutazione = nuovaValutazione;
        this.nuovoStatus = nuovoStatus;
    }

    @Override
    public void esegui() {
        Iterator<Libro> it =  libreria.creaIterator();
        while (it.hasNext()) {
            Libro l = it.next();
            if (l.getCodiceISBN().equals(isbn)) {
                vecchiaValutazione = l.getValutazione();
                vecchioStatus = l.getStatus();
                libreria.modifica_info(isbn, nuovaValutazione, nuovoStatus);
                return; // usciamo appena modificato
            }
        }
        System.out.println("[Command] Libro non trovato: " + isbn);
    }

    public void annulla() {
        Iterator<Libro> it = libreria.creaIterator();
        while (it.hasNext()) {
            Libro l = it.next();
            if (l.getCodiceISBN().equals(isbn)) {
                libreria.modifica_info(isbn, vecchiaValutazione, vecchioStatus);
                return;
            }
        }
    }
}
