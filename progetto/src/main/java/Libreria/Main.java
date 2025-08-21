package Libreria;

import Libreria.service.*;
// Prova

public class Main {
    public static void main(String[] args) throws Exception{
        Libreria libreria = new Libreria();

        libreria.caricaDaFile();

        Libro libro1 = new Libro("Il Signore degli Anelli", "J.R.R. Tolkien");
        libro1.setCodiceISBN("9780261102385");
        libro1.setGenere(Generi.FANTASY);

        Libro libro2 = new Libro("1984", "George Orwell");
        libro2.setCodiceISBN("9780451524935");
        libro2.setGenere(Generi.GIALLO);
        libro2.setStatus(StatoDellaLettura.LETTO);
        libro2.setValutazione(5);

        libreria.aggiungiLibro(libro1);
        libreria.aggiungiLibro(libro2);

        System.out.println("\nLibri in libreria:");
        for (Libro l : libreria.getLibri()) {
            System.out.println(l);
        }

        libreria.modifica_info("9780261102385", 4, StatoDellaLettura.LETTO);

        libreria.rimuovi_libro("9780451524935");

        libreria.salvaSuFile();

    }
}