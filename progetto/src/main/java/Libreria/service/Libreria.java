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

    public void aggiungiLibro(Libro libro) {
        libri.add(libro);
        System.out.println("Il libro \"" + libro.getTitolo() + "\" è stato aggiunto");
        notificaObservers();
    }

    public List<Libro> getLibri() {
        return libri;
    }

    public void modifica_info(String isbn, int nuova_valutazione, StatoDellaLettura nuovo_status) {
        Iterator<Libro> it = crea_iterator();
        while (it.hasNext()) {
            Libro l = it.next();
            if (l.getCodiceISBN().equals(isbn)) {
                l.modifica_status(nuovo_status);
                if (l.getStatus().equals(StatoDellaLettura.LETTO)) {
                    l.modifica_valutazione(nuova_valutazione);
                }
            }
        }
        notificaObservers();
    }

    public void rimuovi_libro(String isbn) {
        Iterator<Libro> it = crea_iterator();
        while (it.hasNext()) {
            Libro l = it.next();
            if (l.getCodiceISBN().equals(isbn)) {
                it.remove();
                System.out.println("Il libro \"" + l.getTitolo() + "\" è stato rimosso");
                notificaObservers();
                return;
            }
        }
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

    // Persistenza delegata a LibreriaStorage
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
