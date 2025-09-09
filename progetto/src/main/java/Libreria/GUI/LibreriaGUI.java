package Libreria.GUI;

import Libreria.service.Libro;
import Libreria.service.Generi;
import Libreria.service.StatoDellaLettura;
import Libreria.service.Libreria;

import Libreria.command.*;
import Libreria.observer.*;
import Libreria.ordinamento.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class LibreriaGUI extends JFrame implements Observer {
    private final Invoker invoker = new Invoker();
    private final Libreria libreria = new Libreria();

    private final LibreriaTableModel tableModel = new LibreriaTableModel();
    private JTable tabella;

    private JTextField campoRicerca;
    private JComboBox<String> tipoRicerca;
    private JComboBox<String> filtroGenere;
    private JComboBox<String> filtroStato;
    private JComboBox<String> comboOrdina;

    private JLabel lblTot, lblLetti, lblInLettura, lblDaLeggere;

    private LoggingObserver loggingObserver;
    private StatisticheObserver statisticheObserver;

    public LibreriaGUI() {
        super("Libreria – Swing GUI con Design Patterns");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setupObservers();

        add(creaToolBar(), BorderLayout.NORTH);
        add(creaCentro(), BorderLayout.CENTER);
        add(creaBarraStatistiche(), BorderLayout.SOUTH);

        setSize(1200, 800);
        setLocationRelativeTo(null);



        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    libreria.salvaSuFile();
                    System.out.println("Dati salvati automaticamente alla chiusura");
                } catch (Exception ex) {
                    System.err.println("Errore nel salvataggio: " + ex.getMessage());
                }
            }
        });
    }


    private void setupObservers() {

        libreria.aggiungiObserver(this);

        loggingObserver = new LoggingObserver("GUI");
        libreria.aggiungiObserver(loggingObserver);

        statisticheObserver = new StatisticheObserver(libreria);
        libreria.aggiungiObserver(statisticheObserver);
    }


    @Override
    public void aggiorna(String messaggio) {
        SwingUtilities.invokeLater(() -> {
            // Aggiorna la tabella
            aggiornaTabella();
            aggiornaStatistiche();
        });
    }

    private void aggiornaTabella() {
        List<Libro> libriCorrente = getCurrentFilteredBooks();
        tableModel.setLibri(libriCorrente);
    }

    private List<Libro> getCurrentFilteredBooks() {
        List<Libro> base = new ArrayList<>(libreria.getLibri());

        String query = campoRicerca != null ? campoRicerca.getText().trim() : "";
        if (!query.isEmpty() && tipoRicerca != null) {
            String tipo = (String) tipoRicerca.getSelectedItem();
            if ("Autore".equals(tipo)) {
                base = libreria.cercaPerAutore(query);
            } else if ("ISBN".equals(tipo)) {
                base = libreria.cercaPerISBN(query);
            } else {
                base = libreria.cercaPerTitolo(query);
            }
        }

        // Filtro genere
        if (filtroGenere != null) {
            String gSel = (String) filtroGenere.getSelectedItem();
            if (!"Tutti".equals(gSel)) {
                Generi g = Generi.valueOf(gSel);
                base = base.stream().filter(l -> l.getGenere() == g).collect(Collectors.toList());
            }
        }

        // Filtro stato
        if (filtroStato != null) {
            String sSel = (String) filtroStato.getSelectedItem();
            if (!"Tutti".equals(sSel)) {
                StatoDellaLettura s = StatoDellaLettura.valueOf(sSel);
                base = Libreria.filtra_status_switch(base, s);
            }
        }

        return base;
    }

    private JToolBar creaToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);

        JButton btnAggiungi = new JButton("Aggiungi Libro");
        btnAggiungi.addActionListener(e -> mostraDialogAggiungi());

        JButton btnModifica = new JButton("Modifica Libro");
        btnModifica.addActionListener(e -> mostraDialogModifica());

        JButton btnRimuovi = new JButton("Rimuovi Libro");
        btnRimuovi.addActionListener(e -> rimuoviSelezionato());

        JButton btnSalva = new JButton("Salva su File");
        btnSalva.addActionListener(e -> salvaDati());

        JButton btnCarica = new JButton("Carica da File");
        btnCarica.addActionListener(e -> caricaDati());

        JButton btnUndo = new JButton("Annulla Ultimo");
        btnUndo.addActionListener(e -> annullaUltimoComando());

        tb.add(btnAggiungi);
        tb.add(btnModifica);
        tb.add(btnRimuovi);
        tb.addSeparator();
        tb.add(btnSalva);
        tb.add(btnCarica);
        tb.addSeparator();
        tb.add(btnUndo);
        tb.addSeparator();
        tb.add(creaPannelloRicercaFiltriOrdinamento());

        return tb;
    }

    private JPanel creaCentro() {
        JPanel p = new JPanel(new BorderLayout());
        tabella = new JTable(tableModel);
        tabella.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabella.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) mostraDialogModifica();
            }
        });

        // Renderer per righe alternate
        tabella.setDefaultRenderer(Object.class, new AlternatingRowRenderer());

        JScrollPane sp = new JScrollPane(tabella);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JPanel creaPannelloRicercaFiltriOrdinamento() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Ricerca
        p.add(new JLabel("Cerca:"));
        campoRicerca = new JTextField(12);
        campoRicerca.getDocument().addDocumentListener(new SimpleDocListener(this::applicaRicercaFiltri));
        p.add(campoRicerca);

        tipoRicerca = new JComboBox<>(new String[]{"Titolo", "Autore", "ISBN"});
        tipoRicerca.addActionListener(e -> applicaRicercaFiltri());
        p.add(tipoRicerca);

        p.add(new JLabel(" | Genere:"));
        String[] generi = new String[Generi.values().length + 1];
        generi[0] = "Tutti";
        for (int i = 0; i < Generi.values().length; i++) {
            generi[i + 1] = Generi.values()[i].toString();
        }
        filtroGenere = new JComboBox<>(generi);
        filtroGenere.addActionListener(e -> applicaRicercaFiltri());
        p.add(filtroGenere);

        p.add(new JLabel(" Stato:"));
        String[] stati = new String[StatoDellaLettura.values().length + 1];
        stati[0] = "Tutti";
        for (int i = 0; i < StatoDellaLettura.values().length; i++) {
            stati[i + 1] = StatoDellaLettura.values()[i].toString();
        }
        filtroStato = new JComboBox<>(stati);
        filtroStato.addActionListener(e -> applicaRicercaFiltri());
        p.add(filtroStato);

        p.add(new JLabel(" Ordina:"));
        comboOrdina = new JComboBox<>(new String[]{"Titolo", "Autore", "Genere", "Valutazione", "Stato", "ISBN"});
        comboOrdina.addActionListener(e -> Ordinamento());
        p.add(comboOrdina);

        JButton btnReset = new JButton("Reset Filtri");
        btnReset.addActionListener(e -> resetFiltri());
        p.add(btnReset);

        return p;
    }

    private JPanel creaBarraStatistiche() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBorder(BorderFactory.createTitledBorder("Statistiche"));

        lblTot = new JLabel("Totale: 0");
        lblLetti = new JLabel("Letti: 0");
        lblInLettura = new JLabel("In lettura: 0");
        lblDaLeggere = new JLabel("Da leggere: 0");

        lblLetti.setForeground(new Color(0, 140, 0));
        lblInLettura.setForeground(new Color(200, 120, 0));
        lblDaLeggere.setForeground(new Color(0, 90, 200));

        p.add(lblTot);
        p.add(new JLabel(" | "));
        p.add(lblLetti);
        p.add(new JLabel(" | "));
        p.add(lblInLettura);
        p.add(new JLabel(" | "));
        p.add(lblDaLeggere);

        return p;
    }

    private void mostraDialogAggiungi() {
        JDialog d = new JDialog(this, "Aggiungi nuovo libro", true);
        d.setLayout(new BorderLayout());

        JTextField tTitolo = new JTextField(20);
        JTextField tAutore = new JTextField(20);
        JTextField tIsbn = new JTextField(20);
        JComboBox<Generi> cbGenere = new JComboBox<>(Generi.values());
        JComboBox<StatoDellaLettura> cbStato = new JComboBox<>(StatoDellaLettura.values());
        JSlider sVal = new JSlider(0, 5, 0);
        sVal.setPaintTicks(true);
        sVal.setMajorTickSpacing(1);
        sVal.setPaintLabels(true);

        JPanel form = new JPanel(new GridLayout(0,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        form.add(new JLabel("Titolo*:")); form.add(tTitolo);
        form.add(new JLabel("Autore*:")); form.add(tAutore);
        form.add(new JLabel("ISBN:"));   form.add(tIsbn);
        form.add(new JLabel("Genere:")); form.add(cbGenere);
        form.add(new JLabel("Stato:"));  form.add(cbStato);
        form.add(new JLabel("Valutazione (0-5):")); form.add(sVal);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("Aggiungi");
        JButton ann = new JButton("Annulla");
        btns.add(ok);
        btns.add(ann);

        ok.addActionListener(e -> {
            if (tTitolo.getText().trim().isEmpty() || tAutore.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(d, "Titolo e Autore sono obbligatori.",
                        "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                StatoDellaLettura stato = (StatoDellaLettura) cbStato.getSelectedItem();
                int valutazione = sVal.getValue();

                if (stato == StatoDellaLettura.DA_LEGGERE && valutazione > 0) {
                    JOptionPane.showMessageDialog(d,
                            "Non puoi valutare un libro nello stato 'Da Leggere'",
                            "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Libro libro = new Libro(
                        tTitolo.getText().trim(),
                        tAutore.getText().trim(),
                        tIsbn.getText().trim().isEmpty() ? null : tIsbn.getText().trim(),
                        (Generi) cbGenere.getSelectedItem(),
                        valutazione,
                        stato
                );

                Command cmd = new AggiungiLibroCommand(libreria, libro);
                invoker.esegui(cmd);

                d.dispose();
                JOptionPane.showMessageDialog(this, "Libro aggiunto con successo!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Errore: " + ex.getMessage(),
                        "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });

        ann.addActionListener(e -> d.dispose());

        d.add(form, BorderLayout.CENTER);
        d.add(btns, BorderLayout.SOUTH);
        d.pack();
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private void mostraDialogModifica() {
        int r = tabella.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Seleziona un libro da modificare.",
                    "Avviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Libro sel = tableModel.getLibroAt(r);
        if (sel.getCodiceISBN() == null || sel.getCodiceISBN().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Impossibile modificare: libro senza ISBN valido.",
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog d = new JDialog(this, "Modifica: " + sel.getTitolo(), true);
        d.setLayout(new BorderLayout());

        JComboBox<StatoDellaLettura> cbStato = new JComboBox<>(StatoDellaLettura.values());
        cbStato.setSelectedItem(sel.getStatus());

        JSlider sVal = new JSlider(0, 5, sel.getValutazione());
        sVal.setPaintTicks(true);
        sVal.setMajorTickSpacing(1);
        sVal.setPaintLabels(true);

        cbStato.addActionListener(e -> {
            if (cbStato.getSelectedItem() == StatoDellaLettura.DA_LEGGERE) {
                sVal.setValue(0);
                sVal.setEnabled(false);
            } else {
                sVal.setEnabled(true);
            }
        });

        JPanel form = new JPanel(new GridLayout(0,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        form.add(new JLabel("Nuovo Stato:"));
        form.add(cbStato);
        form.add(new JLabel("Nuova Valutazione:"));
        form.add(sVal);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("Salva Modifiche");
        JButton ann = new JButton("Annulla");
        btns.add(ok);
        btns.add(ann);

        ok.addActionListener(e -> {
            try {
                StatoDellaLettura nuovoStato = (StatoDellaLettura) cbStato.getSelectedItem();
                int nuovaValutazione = sVal.getValue();
                Command cmd = new ModificaLibroCommand(
                        libreria,
                        sel.getCodiceISBN(),
                        nuovaValutazione,
                        nuovoStato
                );
                invoker.esegui(cmd);

                d.dispose();
                JOptionPane.showMessageDialog(this, "Libro modificato con successo!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Errore: " + ex.getMessage(),
                        "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });

        ann.addActionListener(e -> d.dispose());

        d.add(form, BorderLayout.CENTER);
        d.add(btns, BorderLayout.SOUTH);
        d.pack();
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private void rimuoviSelezionato() {
        int r = tabella.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Seleziona un libro da rimuovere.",
                    "Avviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Libro sel = tableModel.getLibroAt(r);
        int conf = JOptionPane.showConfirmDialog(this,
                "Rimuovere \"" + sel.getTitolo() + "\" di " + sel.getAutore() + "?",
                "Conferma rimozione", JOptionPane.YES_NO_OPTION);

        if (conf == JOptionPane.YES_OPTION) {
            Command cmd = new RimuoviLibroCommand(libreria, sel.getCodiceISBN());
            invoker.esegui(cmd);
            JOptionPane.showMessageDialog(this, "Libro rimosso con successo!");
        }
    }

    private void annullaUltimoComando() {
        invoker.annulla_ultimo();
        JOptionPane.showMessageDialog(this, "Ultimo comando annullato!");
    }

    private void salvaDati() {
        try {
            libreria.salvaSuFile();
            JOptionPane.showMessageDialog(this, "Dati salvati con successo!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Errore nel salvataggio: " + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void caricaDati() {
        try {
            libreria.caricaDaFile();
            aggiornaTabella();
            aggiornaStatistiche();
            JOptionPane.showMessageDialog(this, "Dati caricati con successo!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Errore nel caricamento: " + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applicaRicercaFiltri() {
        aggiornaTabella();
        aggiornaStatistiche(getCurrentFilteredBooks());
    }

    private void Ordinamento() {
        String criterio = (String) comboOrdina.getSelectedItem();

        Ordinamento strategiaOrdinamento;
        switch (criterio) {
            case "Autore":
                strategiaOrdinamento = new OrdinaPerAutore();
                break;
            case "Genere":
                strategiaOrdinamento = new OrdinaPerGenere();
                break;
            case "Valutazione":
                strategiaOrdinamento = new OrdinaPerValutazione();
                break;
            case "Stato":
                strategiaOrdinamento = new OrdinaPerStato();
                break;
            case "ISBN":
                strategiaOrdinamento = new OrdinaPerCodice();
                break;
            default:
                strategiaOrdinamento = new OrdinaPerTitolo();
                break;
        }
        libreria.ordina(strategiaOrdinamento);
        aggiornaTabella();
    }


    private void resetFiltri() {
        campoRicerca.setText("");
        tipoRicerca.setSelectedIndex(0);
        filtroGenere.setSelectedIndex(0);
        filtroStato.setSelectedIndex(0);
        comboOrdina.setSelectedIndex(0);
        aggiornaTabella();
        aggiornaStatistiche();
    }

    private void aggiornaStatistiche() {
        aggiornaStatistiche(libreria.getLibri());
    }

    private void aggiornaStatistiche(List<Libro> lista) {
        int tot = lista.size();
        long letti = lista.stream().filter(l -> l.getStatus() == StatoDellaLettura.LETTO).count();
        long inLett = lista.stream().filter(l -> l.getStatus() == StatoDellaLettura.IN_LETTURA).count();
        long daLegg = lista.stream().filter(l -> l.getStatus() == StatoDellaLettura.DA_LEGGERE).count();

        lblTot.setText("Totale: " + tot);
        lblLetti.setText("Letti: " + letti);
        lblInLettura.setText("In lettura: " + inLett);
        lblDaLeggere.setText("Da leggere: " + daLegg);
    }

    private static class LibreriaTableModel extends AbstractTableModel {
        private final String[] COLS = {"Titolo", "Autore", "ISBN", "Genere", "Stato", "Valutazione"};
        private List<Libro> dati = new ArrayList<>();

        public void setLibri(List<Libro> libri) {
            this.dati = new ArrayList<>(libri);
            fireTableDataChanged();
        }

        public Libro getLibroAt(int row) {
            return dati.get(row);
        }

        @Override public int getRowCount() { return dati.size(); }
        @Override public int getColumnCount() { return COLS.length; }
        @Override public String getColumnName(int c) { return COLS[c]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Libro l = dati.get(rowIndex);
            switch (columnIndex) {
                case 0: return l.getTitolo();
                case 1: return l.getAutore();
                case 2: return l.getCodiceISBN() != null ? l.getCodiceISBN() : "N/A";
                case 3: return l.getGenere() != null ? l.getGenere().toString() : "N/A";
                case 4:
                    if (l.getStatus() == StatoDellaLettura.DA_LEGGERE) return "Da leggere";
                    if (l.getStatus() == StatoDellaLettura.IN_LETTURA) return "In lettura";
                    return "Letto";
                case 5:
                    int v = l.getValutazione();
                    if (v <= 0) return "Non valutato";
                    return String.valueOf(v);
            }
            return "";
        }

    }

    private static class AlternatingRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245,245,245));
            }
            return c;
        }
    }

    private static class SimpleDocListener implements javax.swing.event.DocumentListener {
        private final Runnable r;
        SimpleDocListener(Runnable r) { this.r = r; }
        public void insertUpdate(javax.swing.event.DocumentEvent e) { r.run(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { r.run(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { r.run(); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibreriaGUI().setVisible(true));
    }
}