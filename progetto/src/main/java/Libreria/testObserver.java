package Libreria;

import Libreria.observer.*;
import Libreria.service.*;

public class testObserver {
    public static void main(String[] args) {
        Libreria libreria = new Libreria();

        Observer stats = new StatisticheObserver(libreria);
        Observer log = new LoggingObserver("MAIN");

        libreria.aggiungiObserver(stats);
        libreria.aggiungiObserver(log);

        Libro libro1 = new Libro("1984", "George Orwell");
        libreria.aggiungiLibro(libro1);
        libreria.modifica_info(libro1.getCodiceISBN(), 5, StatoDellaLettura.LETTO);
    }
}
