package Libreria.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Libro {
    private String titolo;
    private String autore;
    private String codiceISBN;
    private Generi genere;
    private int valutazione;
    private StatoDellaLettura status;

    // Costruttore per JSON
    public Libro(@JsonProperty("titolo") String titolo,
                 @JsonProperty("autore") String autore,
                 @JsonProperty("codiceISBN") String codiceISBN,
                 @JsonProperty("genere") Generi genere,
                 @JsonProperty("valutazione") int valutazione,
                 @JsonProperty("status") StatoDellaLettura status) {
        this.titolo = titolo;
        this.autore = autore;
        this.codiceISBN = codiceISBN;
        this.genere = genere;
        this.valutazione = valutazione;
        this.status = status;
    }

    // Costruttore vuoto
    public Libro() {
        this.genere = Generi.ROMANZO;
        this.status = StatoDellaLettura.DA_LEGGERE;
        this.valutazione = 0;
    }
    public Libro(String titolo, String autore, StatoDellaLettura status, int valutazione) {
        this.titolo = titolo;
        this.autore = autore;
        this.status = status;

        if (status == StatoDellaLettura.DA_LEGGERE && valutazione > 0) {
            throw new IllegalStateException("Impossibile valutare un libro nello stato 'DA_LEGGERE'");
        }
        if (valutazione < 0 || valutazione > 5) {
            throw new IllegalArgumentException("La valutazione deve essere tra 0 e 5");
        }
        this.valutazione = valutazione;
    }

    // Costruttore semplificato
    public Libro(String titolo, String autore) {
        this.titolo = titolo;
        this.autore = autore;
        this.genere = Generi.ROMANZO;  // Default
        this.status = StatoDellaLettura.DA_LEGGERE;  // Default
        this.valutazione = 0;
    }

    // GETTER e SETTER
    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getAutore() { return autore; }
    public void setAutore(String autore) { this.autore = autore; }

    public String getCodiceISBN() { return codiceISBN; }

    public void setCodiceISBN(String codiceISBN) { this.codiceISBN = codiceISBN; }

    public Generi getGenere() { return genere; }

    public void setGenere(Generi genere) { this.genere = genere; }

    public int getValutazione() { return valutazione; }

    public void setValutazione(int valutazione) {
        if (this.status == StatoDellaLettura.DA_LEGGERE && valutazione > 0) {
            throw new IllegalStateException("Impossibile valutare un libro nello stato 'DA_LEGGERE'");
        }
        if (valutazione < 0 || valutazione > 5) {
            throw new IllegalArgumentException("La valutazione deve essere tra 0 e 5");
        }
        this.valutazione = valutazione;
    }

    public StatoDellaLettura getStatus() { return status; }
    public void setStatus(StatoDellaLettura status) { this.status = status; }

    public void modifica_valutazione(int nuovaValutazione) {
        setValutazione(nuovaValutazione); // Usa il setter per validare
        System.out.println("Valutazione del libro \"" + titolo + "\" modificata in: " + nuovaValutazione);
    }

    public void modifica_status(StatoDellaLettura nuovoStatus) {
        this.status = nuovoStatus;
        System.out.println("Stato della lettura \"" + titolo + "\" modificato in: " + nuovoStatus);
    }

    @Override
    public String toString() {
        return titolo + " di " + autore + " [" + genere + "] - Valutazione: " + valutazione;
    }
}


