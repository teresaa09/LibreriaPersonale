import Libreria.command.*;
import Libreria.service.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class CommandTest {

    private Libreria libreria;
    private Invoker invoker;
    private Libro testLibro;

    @BeforeEach
    void setUp() {
        libreria = new Libreria();
        invoker = new Invoker();
        testLibro = new Libro("Test Book", "Test Author", "978-TEST",
                Generi.ROMANZO, 0, StatoDellaLettura.DA_LEGGERE);
    }

    // =================== TEST POSITIVI ===================
    @Test
    @DisplayName("Test comando aggiungi e undo")
    public void testAggiungiEUndo() {
        AggiungiLibroCommand command = new AggiungiLibroCommand(libreria, testLibro);

        invoker.esegui(command);
        assertEquals(1, libreria.getLibri().size());

        invoker.annulla_ultimo();
        assertEquals(0, libreria.getLibri().size());
    }

    @Test
    @DisplayName("Test comando rimuovi e undo")
    public void testRimuoviEUndo() {
        libreria.aggiungiLibro(testLibro);
        RimuoviLibroCommand command = new RimuoviLibroCommand(libreria, "978-TEST");

        invoker.esegui(command);
        assertEquals(0, libreria.getLibri().size());

        invoker.annulla_ultimo();
        assertEquals(1, libreria.getLibri().size());
    }

    @Test
    @DisplayName("Test comando modifica")
    public void testModifica() {
        libreria.aggiungiLibro(testLibro);
        ModificaLibroCommand command = new ModificaLibroCommand(libreria, "978-TEST", 4, StatoDellaLettura.LETTO);

        invoker.esegui(command);
        assertEquals(4, libreria.getLibri().get(0).getValutazione());
    }

    // =================== TEST NEGATIVI ===================
    @Test
    @DisplayName("NEGATIVO: Undo senza comandi")
    public void testUndoVuoto() {
        assertDoesNotThrow(() -> invoker.annulla_ultimo());
    }

    @Test
    @DisplayName("NEGATIVO: Comando su libro inesistente")
    public void testComandoLibroInesistente() {
        RimuoviLibroCommand command = new RimuoviLibroCommand(libreria, "978-INESISTENTE");
        assertDoesNotThrow(() -> invoker.esegui(command));

        ModificaLibroCommand commandMod = new ModificaLibroCommand(libreria, "978-INESISTENTE", 5, StatoDellaLettura.LETTO);
        assertDoesNotThrow(() -> invoker.esegui(commandMod));
    }
}
