package Libreria.observer;

public class LoggingObserver implements Observer {
    private String nome;

    public LoggingObserver(String nome) { this.nome = nome; }

    @Override
    public void aggiorna(String messaggio) {
        System.out.println("[LOG-" + nome + "] " + messaggio);
    }
}
