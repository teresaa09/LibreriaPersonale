package Libreria.observer;

import Libreria.service.Libreria;
import Libreria.service.StatoDellaLettura;

public class StatisticheObserver implements Observer {
    private Libreria libreria;

    public StatisticheObserver(Libreria libreria) { this.libreria = libreria; }

    @Override
    public void aggiorna(String messaggio) {
        long letti = libreria.getLibri().stream().filter(l -> l.getStatus() == StatoDellaLettura.LETTO).count();
        long daLeggere = libreria.getLibri().stream().filter(l -> l.getStatus() == StatoDellaLettura.DA_LEGGERE).count();
        System.out.println("[STATS] " + messaggio + " | Letti: " + letti + ", Da leggere: " + daLeggere);
    }
}
