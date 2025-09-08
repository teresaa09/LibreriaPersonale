import Libreria.observer.*;
import Libreria.service.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ObserverTest {

    private Libreria libreria;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        libreria = new Libreria();
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // =================== TEST POSITIVI ===================
    @Test
    @DisplayName("Test notifica logging observer")
    public void testLoggingObserver() {
        LoggingObserver logger = new LoggingObserver("TEST");
        libreria.aggiungiObserver(logger);

        Libro libro = new Libro("Test", "Author", "978-TEST",
                Generi.ROMANZO, 0, StatoDellaLettura.DA_LEGGERE);
        libreria.aggiungiLibro(libro);

        String output = outputStream.toString();
        assertTrue(output.contains("[LOG-TEST]"));
        assertTrue(output.contains("Aggiunto: Test"));
    }

    @Test
    @DisplayName("Test statistiche observer")
    public void testStatisticheObserver() {
        StatisticheObserver stats = new StatisticheObserver(libreria);
        libreria.aggiungiObserver(stats);

        Libro libro = new Libro("Test", "Author", "978-TEST",
                Generi.ROMANZO, 5, StatoDellaLettura.LETTO);
        libreria.aggiungiLibro(libro);

        String output = outputStream.toString();
        assertTrue(output.contains("[STATS]"));
        assertTrue(output.contains("Letti: 1"));
    }

    @Test
    @DisplayName("Test rimozione observer")
    public void testRimozioneObserver() {
        LoggingObserver logger = new LoggingObserver("TEST");
        libreria.aggiungiObserver(logger);
        libreria.rimuoviObserver(logger);

        Libro libro = new Libro("Test", "Author", "978-TEST",
                Generi.ROMANZO, 0, StatoDellaLettura.DA_LEGGERE);
        libreria.aggiungiLibro(libro);

        String output = outputStream.toString();
        assertFalse(output.contains("[LOG-TEST]"));
    }

    // =================== TEST NEGATIVI ===================
    @Test
    @DisplayName("NEGATIVO: Aggiunta observer null")
    public void testAggiungiObserverNull() {
        assertDoesNotThrow(() -> libreria.aggiungiObserver(null));
    }

    @Test
    @DisplayName("NEGATIVO: Rimozione observer inesistente")
    public void testRimozioneObserverInesistente() {
        LoggingObserver logger = new LoggingObserver("TEST");
        assertDoesNotThrow(() -> libreria.rimuoviObserver(logger));
    }

    @Test
    @DisplayName("NEGATIVO: Notifica con messaggio null")
    public void testNotificaMessaggioNull() {
        LoggingObserver logger = new LoggingObserver("TEST");
        libreria.aggiungiObserver(logger);

        assertDoesNotThrow(() -> libreria.notificaObservers(null));
    }
}
