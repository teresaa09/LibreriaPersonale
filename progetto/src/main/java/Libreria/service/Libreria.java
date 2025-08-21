package Libreria.service;

import Libreria.iterazione.Aggregato;
import Libreria.iterazione.Iterator;
import Libreria.iterazione.Libreria_iterator;
import Libreria.observer.Subject;

import java.util.ArrayList;
import java.util.List;

public class Libreria extends Subject implements Aggregato {

    private List<Libro> libri;

    public Libreria() {
        this.libri = new ArrayList<>();
    }

    public boolean aggiungiLibro(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("Il libro non può essere null");
        }

        // Controlla duplicati tramite ISBN
        if (libro.getCodiceISBN() != null &&
                libri.stream().anyMatch(l -> libro.getCodiceISBN().equals(l.getCodiceISBN()))) {
            System.out.println("Libro con ISBN " + libro.getCodiceISBN() + " già presente!");
            return false;
        }

        libri.add(libro);
        System.out.println("Il libro \"" + libro.getTitolo() + "\" è stato aggiunto");
        notificaObservers("Aggiunto: " + libro.getTitolo());
        return true;
    }

    public List<Libro> getLibri() {
        return libri;
    }

    public void modifica_info(String isbn, int nuova_valutazione, StatoDellaLettura nuovo_status) {
        if (isbn == null) return;
        Iterator<Libro> it = (Iterator<Libro>) crea_iterator();
        Libro l = null;
        while (it.hasNext()) {
            l = (Libro) it.next();
            if (l.getCodiceISBN().equals(isbn)) {
                l.modifica_status(nuovo_status);
                if (l.getStatus().equals(StatoDellaLettura.LETTO)) {
                    l.modifica_valutazione(nuova_valutazione);
                }
            }
        }
        if (l != null) {
            notificaObservers("Libro modificato: " + l.getTitolo());
        } else {
            System.out.println("Nessun libro trovato con ISBN " + isbn);
        }
    }

    public void rimuovi_libro(String isbn) {
        if (isbn == null) return;
        Iterator<Libro> it = crea_iterator();
        Libro l = null;

        while (it.hasNext()) {
            l = it.next();
            if (l.getCodiceISBN().equals(isbn)) {
                it.remove();
                System.out.println("Il libro \"" + l.getTitolo() + "\" è stato rimosso");
                notificaObservers("Libro rimosso: " + l.getTitolo());
                return;
            }
        }

        // Nessun libro trovato
        System.out.println("Nessun libro trovato con ISBN " + isbn);
    }



    public List<Libro> filtra_genere(Generi genere) {
        List<Libro> risultati = new ArrayList<>();
        Iterator<Libro> it = crea_iterator();
        while (it.hasNext()) {
            Libro libro = it.next();
            if (libro.getGenere().equals(genere)) {
                risultati.add(libro);
            }
        }
        return risultati;
    }

    public static List<Libro> filtra_status_switch(List<Libro> libri, StatoDellaLettura status) {
        List<Libro> risultati = new ArrayList<>();

        for (Libro libro : libri) {
            switch (status) {
                case LETTO:
                    if (libro.getStatus() == StatoDellaLettura.LETTO) {
                        risultati.add(libro);
                    }
                    break;
                case IN_LETTURA:
                    if (libro.getStatus() == StatoDellaLettura.IN_LETTURA) {
                        risultati.add(libro);
                    }
                    break;
                case DA_LEGGERE:
                    if (libro.getStatus() == StatoDellaLettura.DA_LEGGERE) {
                        risultati.add(libro);
                    }
                    break;
            }
        }
        return risultati;
    }


    public void salvaSuFile() throws Exception {
        LibreriaStorage.salva(libri);
    }

    public void caricaDaFile() {
        this.libri = LibreriaStorage.carica();
    }

    @Override
    public Iterator<Libro> crea_iterator() {
        return new Libreria_iterator(libri);
    }
}
