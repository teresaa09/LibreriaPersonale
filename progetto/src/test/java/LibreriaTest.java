import Libreria.service.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class LibreriaTest {

    private Libreria libreria;
    private Libro libro1, libro2, libro3;

    @BeforeEach
    void setUp() {
        libreria = new Libreria();
        libro1 = new Libro("1984", "George Orwell", "978-1",
                Generi.ROMANZO, 0, StatoDellaLettura.DA_LEGGERE);
        libro2 = new Libro("Il Nome della Rosa", "Umberto Eco", "978-2",
                Generi.GIALLO, 5, StatoDellaLettura.LETTO);
        libro3 = new Libro("Dune", "Frank Herbert", "978-3",
                Generi.FANTASY, 0, StatoDellaLettura.IN_LETTURA);
    }

    // =================== TEST POSITIVI ===================
    @Test
    @DisplayName("Test aggiunta libro")
    public void testAggiungiLibro() {
        assertTrue(libreria.aggiungiLibro(libro1));
        assertEquals(1, libreria.getLibri().size());
    }

    @Test
    @DisplayName("Test ricerca per titolo")
    public void testCercaPerTitolo() {
        libreria.aggiungiLibro(libro1);
        List<Libro> risultati = libreria.cercaPerTitolo("1984");
        assertEquals(1, risultati.size());
        assertEquals("1984", risultati.get(0).getTitolo());
    }

    @Test
    @DisplayName("Test filtro per genere")
    public void testFiltraGenere() {
        libreria.aggiungiLibro(libro1);
        List<Libro> romanzi = libreria.filtra_genere(Generi.ROMANZO);
        assertEquals(1, romanzi.size());
    }

    // =================== TEST NEGATIVI ===================
    @Test
    @DisplayName("NEGATIVO: Aggiunta libro null")
    public void testAggiungiLibroNull() {
        assertThrows(IllegalArgumentException.class, () -> libreria.aggiungiLibro(null));
    }

    @Test
    @DisplayName("NEGATIVO: Ricerca titolo null/vuoto")
    public void testCercaTitoloInvalido() {
        libreria.aggiungiLibro(libro1);
        assertTrue(libreria.cercaPerTitolo(null).isEmpty());
        assertTrue(libreria.cercaPerTitolo("").isEmpty());
        assertTrue(libreria.cercaPerTitolo("INESISTENTE").isEmpty());
    }

    @Test
    @DisplayName("NEGATIVO: Rimozione ISBN null/inesistente")
    public void testRimozioneInvalida() {
        libreria.aggiungiLibro(libro1);
        int size = libreria.getLibri().size();

        assertDoesNotThrow(() -> libreria.rimuovi_libro(null));
        assertDoesNotThrow(() -> libreria.rimuovi_libro("INESISTENTE"));
        assertEquals(size, libreria.getLibri().size());
    }
}
