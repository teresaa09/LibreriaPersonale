package Libreria.GUI;

// importa tutto dal pacchetto service
import Libreria.service.Libro;
import Libreria.service.Generi;
import Libreria.service.StatoDellaLettura;
import Libreria.service.Libreria;

// importa comandi e invoker
import Libreria.command.*;

// import per observer se ti serve
import Libreria.observer.*;

// import per iteratore se ti serve
import Libreria.iterazione.*;

// import Swing
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
/**
 * GUI Swing minimale per la gestione della libreria.
 * Dipendenze: model.Libreria, model.Libro, model.Generi, model.StatoDellaLettura
 */
public class LibreriaGUI extends JFrame {
    private final Invoker invoker = new Invoker();
    private final Libreria libreria = new Libreria();

    // Tabella
    private final LibreriaTableModel tableModel = new LibreriaTableModel();
    private JTable tabella;

    // Ricerca/filtri/ordinamento
    private JTextField campoRicerca;
    private JComboBox<String> tipoRicerca;   // Titolo / Autore / ISBN
    private JComboBox<String> filtroGenere;  // Tutti / Generi
    private JComboBox<String> filtroStato;   // Tutti / Stati
    private JComboBox<String> comboOrdina;   // Titolo / Autore / Genere / Valutazione / Stato / ISBN

    // Statistiche
    private JLabel lblTot, lblLetti, lblInLettura, lblDaLeggere;

    public LibreriaGUI() {
        super("Libreria – Swing GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(creaToolBar(), BorderLayout.NORTH);
        add(creaCentro(), BorderLayout.CENTER);
        add(creaBarraStatistiche(), BorderLayout.SOUTH);

        setSize(1100, 700);
        setLocationRelativeTo(null);

        // salvataggio automatico in chiusura
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                try { libreria.salvaSuFile(); } catch (Exception ignored) {}
            }
        });
    }

    private JToolBar creaToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);

        JButton btnAggiungi = new JButton("➕ Aggiungi");
        btnAggiungi.addActionListener(e -> mostraDialogAggiungi());

        JButton btnModifica = new JButton("✏️ Modifica");
        btnModifica.addActionListener(e -> mostraDialogModifica());

        JButton btnRimuovi = new JButton("🗑️ Rimuovi");
        btnRimuovi.addActionListener(e -> rimuoviSelezionato());

        JButton btnSalva = new JButton("💾 Salva");
        btnSalva.addActionListener(e -> {
            try { libreria.salvaSuFile();
                JOptionPane.showMessageDialog(this, "Salvato!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Errore salvataggio: " + ex.getMessage(),
                        "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCarica = new JButton("📂 Carica");
        btnCarica.addActionListener(e -> {
            try { libreria.caricaDaFile();
                tableModel.setLibri(new ArrayList<>(libreria.getLibri()));
                aggiornaStatistiche();
                JOptionPane.showMessageDialog(this, "Caricato!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Errore caricamento: " + ex.getMessage(),
                        "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnUndo = new JButton("↩️ Annulla");
        btnUndo.addActionListener(e -> {
            invoker.annulla_ultimo();
            tableModel.setLibri(new ArrayList<>(libreria.getLibri()));
            aggiornaStatistiche();
        });
        tb.add(btnUndo);

        tb.add(btnAggiungi);
        tb.add(btnModifica);
        tb.add(btnRimuovi);
        tb.addSeparator();
        tb.add(btnSalva);
        tb.add(btnCarica);
        tb.addSeparator();
        tb.add(creaPannelloRicercaFiltriOrdinamento());
        return tb;
    }

    private JPanel creaCentro() {
        JPanel p = new JPanel(new BorderLayout());
        tabella = new JTable(tableModel);
        tabella.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabella.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) mostraDialogModifica();
            }
        });

        // Alterna colore righe
        tabella.setDefaultRenderer(Object.class, new AlternatingRowRenderer());

        JScrollPane sp = new JScrollPane(tabella);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JPanel creaPannelloRicercaFiltriOrdinamento() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Ricerca
        p.add(new JLabel("Cerca:"));
        campoRicerca = new JTextField(14);
        campoRicerca.getDocument().addDocumentListener(new SimpleDocListener(this::applicaRicercaFiltri));
        p.add(campoRicerca);

        tipoRicerca = new JComboBox<>(new String[]{"Titolo", "Autore", "ISBN"});
        tipoRicerca.addActionListener(e -> applicaRicercaFiltri());
        p.add(tipoRicerca);

        p.add(new JLabel(" | Genere:"));
        String[] generi = new String[Generi.values().length + 1];
        generi[0] = "Tutti";
        for (int i = 0; i < Generi.values().length; i++) generi[i + 1] = Generi.values()[i].toString();
        filtroGenere = new JComboBox<>(generi);
        filtroGenere.addActionListener(e -> applicaRicercaFiltri());
        p.add(filtroGenere);

        p.add(new JLabel(" Stato:"));
        String[] stati = new String[StatoDellaLettura.values().length + 1];
        stati[0] = "Tutti";
        for (int i = 0; i < StatoDellaLettura.values().length; i++) stati[i + 1] = StatoDellaLettura.values()[i].toString();
        filtroStato = new JComboBox<>(stati);
        filtroStato.addActionListener(e -> applicaRicercaFiltri());
        p.add(filtroStato);

        p.add(new JLabel(" Ordina:"));
        comboOrdina = new JComboBox<>(new String[]{"Titolo", "Autore", "Genere", "Valutazione", "Stato", "ISBN"});
        comboOrdina.addActionListener(e -> applicaOrdinamentoInTabella());
        p.add(comboOrdina);

        JButton btnReset = new JButton("🔄 Reset");
        btnReset.addActionListener(e -> {
            campoRicerca.setText("");
            tipoRicerca.setSelectedIndex(0);
            filtroGenere.setSelectedIndex(0);
            filtroStato.setSelectedIndex(0);
            comboOrdina.setSelectedIndex(0);
            tableModel.setLibri(new ArrayList<>(libreria.getLibri()));
            aggiornaStatistiche();
        });
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

        p.add(lblTot);     p.add(new JLabel(" | "));
        p.add(lblLetti);   p.add(new JLabel(" | "));
        p.add(lblInLettura); p.add(new JLabel(" | "));
        p.add(lblDaLeggere);

        return p;
    }

    /* ==========================
       Azioni GUI
       ========================== */

    private void mostraDialogAggiungi() {
        JDialog d = new JDialog(this, "Aggiungi libro", true);
        d.setLayout(new BorderLayout());

        JTextField tTitolo = new JTextField(20);
        JTextField tAutore = new JTextField(20);
        JTextField tIsbn = new JTextField(20);
        JComboBox<Generi> cbGenere = new JComboBox<>(Generi.values());
        JComboBox<StatoDellaLettura> cbStato = new JComboBox<>(StatoDellaLettura.values());
        JSlider sVal = new JSlider(0, 5, 0);
        sVal.setPaintTicks(true); sVal.setMajorTickSpacing(1); sVal.setPaintLabels(true);

        JPanel form = new JPanel(new GridLayout(0,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        form.add(new JLabel("Titolo:")); form.add(tTitolo);
        form.add(new JLabel("Autore:")); form.add(tAutore);
        form.add(new JLabel("ISBN:"));   form.add(tIsbn);
        form.add(new JLabel("Genere:")); form.add(cbGenere);
        form.add(new JLabel("Stato:"));  form.add(cbStato);
        form.add(new JLabel("Valutazione:")); form.add(sVal);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("Aggiungi");
        JButton ann = new JButton("Annulla");
        btns.add(ok); btns.add(ann);

        ok.addActionListener(e -> {
            if (tTitolo.getText().trim().isEmpty() || tAutore.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(d, "Titolo e Autore sono obbligatori.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Libro libro = new Libro(
                        tTitolo.getText().trim(),
                        tAutore.getText().trim(),
                        tIsbn.getText().trim().isEmpty() ? null : tIsbn.getText().trim(),
                        (Generi) cbGenere.getSelectedItem(),
                        sVal.getValue(),
                        (StatoDellaLettura) cbStato.getSelectedItem()
                );
                Command cmd = new AggiungiLibroCommand(libreria, libro);
                invoker.esegui(cmd);
                tableModel.setLibri(new ArrayList<>(libreria.getLibri()));
                aggiornaStatistiche();
                d.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Seleziona un libro da modificare.", "Avviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Libro sel = tableModel.getLibroAt(r);

        JDialog d = new JDialog(this, "Modifica: " + sel.getTitolo(), true);
        d.setLayout(new BorderLayout());

        JComboBox<StatoDellaLettura> cbStato = new JComboBox<>(StatoDellaLettura.values());
        cbStato.setSelectedItem(sel.getStatus());
        JSlider sVal = new JSlider(0, 5, sel.getValutazione());
        sVal.setPaintTicks(true); sVal.setMajorTickSpacing(1); sVal.setPaintLabels(true);

        JPanel form = new JPanel(new GridLayout(0,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        form.add(new JLabel("Stato:")); form.add(cbStato);
        form.add(new JLabel("Valutazione:")); form.add(sVal);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("Salva");
        JButton ann = new JButton("Annulla");
        btns.add(ok); btns.add(ann);

        ok.addActionListener(e -> {
            try {
                Command cmd = new ModificaLibroCommand(
                        libreria,
                        sel.getCodiceISBN(),
                        sVal.getValue(),
                        (StatoDellaLettura) cbStato.getSelectedItem()
                );
                invoker.esegui(cmd);
                tableModel.setLibri(new ArrayList<>(libreria.getLibri()));
                aggiornaStatistiche();
                d.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Seleziona un libro da rimuovere.", "Avviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Libro sel = tableModel.getLibroAt(r);
        int conf = JOptionPane.showConfirmDialog(this,
                "Rimuovere \"" + sel.getTitolo() + "\" di " + sel.getAutore() + "?", "Conferma",
                JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            Command cmd = new RimuoviLibroCommand(libreria, sel.getCodiceISBN());
            invoker.esegui(cmd);
            tableModel.setLibri(new ArrayList<>(libreria.getLibri()));
            aggiornaStatistiche();

        }
    }

    private void applicaRicercaFiltri() {
        List<Libro> base = new ArrayList<>(libreria.getLibri());

        // Ricerca
        String q = campoRicerca.getText().trim();
        if (!q.isEmpty()) {
            String tipo = (String) tipoRicerca.getSelectedItem();
            if ("Autore".equals(tipo)) {
                base = libreria.cercaPerAutore(q);
            } else if ("ISBN".equals(tipo)) {
                base = libreria.cercaPerISBN(q);
            } else {
                base = libreria.cercaPerTitolo(q);
            }
        }

        // Filtro genere
        String gSel = (String) filtroGenere.getSelectedItem();
        if (!"Tutti".equals(gSel)) {
            Generi g = Generi.valueOf(gSel);
            base = base.stream().filter(l -> l.getGenere() == g).collect(Collectors.toList());
        }

        // Filtro stato
        String sSel = (String) filtroStato.getSelectedItem();
        if (!"Tutti".equals(sSel)) {
            StatoDellaLettura s = StatoDellaLettura.valueOf(sSel);
            base = Libreria.filtra_status_switch(base, s);
        }

        tableModel.setLibri(base);
        applicaOrdinamentoInTabella(); // mantieni criterio scelto
        aggiornaStatistiche(base);
    }

    private void applicaOrdinamentoInTabella() {
        String criterio = (String) comboOrdina.getSelectedItem();
        tableModel.ordinaLocalmente(criterio);
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

    /* ==========================
       Table model
       ========================== */

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
                case 2: return l.getCodiceISBN();
                case 3: return l.getGenere() != null ? l.getGenere().toString() : "";
                case 4:
                    if (l.getStatus() == StatoDellaLettura.DA_LEGGERE) return "📚 Da leggere";
                    if (l.getStatus() == StatoDellaLettura.IN_LETTURA) return "📖 In lettura";
                    return "✅ Letto";
                case 5:
                    int v = l.getValutazione();
                    if (v <= 0) return "Non valutato";
                    StringBuilder sb = new StringBuilder();
                    for (int i=0;i<v;i++) sb.append('★');
                    for (int i=v;i<5;i++) sb.append('☆');
                    return sb + " (" + v + ")";
            }
            return "";
        }

        void ordinaLocalmente(String criterio) {
            // Ordinamento locale senza dipendere dalle strategie del modello
            dati.sort((a,b) -> {
                if ("Autore".equals(criterio)) {
                    return safeCmp(a.getAutore(), b.getAutore());
                } else if ("Genere".equals(criterio)) {
                    String ga = a.getGenere() == null ? "" : a.getGenere().toString();
                    String gb = b.getGenere() == null ? "" : b.getGenere().toString();
                    return safeCmp(ga, gb);
                } else if ("Valutazione".equals(criterio)) {
                    return Integer.compare(a.getValutazione(), b.getValutazione());
                } else if ("Stato".equals(criterio)) {
                    return Integer.compare(priority(a.getStatus()), priority(b.getStatus()));
                } else if ("ISBN".equals(criterio)) {
                    return safeCmp(a.getCodiceISBN(), b.getCodiceISBN());
                } else { // Titolo
                    return safeCmp(a.getTitolo(), b.getTitolo());
                }
            });
            fireTableDataChanged();
        }

        private static int priority(StatoDellaLettura s) {
            if (s == StatoDellaLettura.DA_LEGGERE) return 0;
            if (s == StatoDellaLettura.IN_LETTURA) return 1;
            return 2; // LETTO
        }

        private static int safeCmp(String a, String b) {
            String sa = a == null ? "" : a.toLowerCase();
            String sb = b == null ? "" : b.toLowerCase();
            return sa.compareTo(sb);
        }
    }

    /* ==========================
       Renderer per righe alternate
       ========================== */
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

    /* ==========================
       Utility: DocumentListener compatto
       ========================== */
    private static class SimpleDocListener implements javax.swing.event.DocumentListener {
        private final Runnable r;
        SimpleDocListener(Runnable r) { this.r = r; }
        public void insertUpdate(javax.swing.event.DocumentEvent e) { r.run(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { r.run(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { r.run(); }
    }

    /* ==========================
       Main
       ========================== */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibreriaGUI().setVisible(true));
    }
}

