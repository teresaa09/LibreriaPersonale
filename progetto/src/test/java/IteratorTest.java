import Libreria.iterazione.*;
import Libreria.service.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class IteratorTest {

    private LibreriaIterator iterator;
    private List<Libro> libri;

    @BeforeEach
    void setUp() {
        libri = new ArrayList<>();
        libri.add(new Libro("Libro A", "Autore A", "978-A", Generi.ROMANZO, 0, StatoDellaLettura.DA_LEGGERE));
        libri.add(new Libro("Libro B", "Autore B", "978-B", Generi.GIALLO, 0, StatoDellaLettura.DA_LEGGERE));
        iterator = new LibreriaIterator(libri);
    }

    // =================== TEST POSITIVI ===================
    @Test
    @DisplayName("Test iterazione completa")
    public void testIterazioneCompleta() {
        int count = 0;
        while (iterator.hasNext()) {
            assertNotNull(iterator.next());
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Test rimozione tramite iterator")
    public void testRimozione() {
        assertTrue(iterator.hasNext());
        iterator.next();
        iterator.remove();
        assertEquals(1, libri.size());
    }

    // =================== TEST NEGATIVI ===================
    @Test
    @DisplayName("NEGATIVO: Remove senza next")
    public void testRemoveSenzaNext() {
        assertThrows(IllegalStateException.class, iterator::remove);
    }

    @Test
    @DisplayName("NEGATIVO: Next oltre limite")
    public void testNextOltreLimite() {
        while (iterator.hasNext()) {
            iterator.next();
        }
        assertThrows(IllegalStateException.class, iterator::next);
    }

    @Test
    @DisplayName("NEGATIVO: Doppio remove")
    public void testDoppioRemove() {
        iterator.next();
        iterator.remove();
        assertThrows(IllegalStateException.class, iterator::remove);
    }
}
