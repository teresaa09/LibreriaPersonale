package Libreria.observer;

import java.util.HashSet;
import java.util.Set;

public abstract class Subject {

    private final Set<Observer> observers = new HashSet<>();

    public void aggiungiObserver(Observer obs) {
        observers.add(obs); // non permette duplicati
    }

    public void rimuoviObserver(Observer obs) {
        observers.remove(obs);
    }

    public void notificaObservers(String messaggio) {
        for (Observer obs : observers) {
            obs.aggiorna(messaggio);
        }
    }
}