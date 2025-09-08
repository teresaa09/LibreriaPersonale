import Libreria.ordinamento.*;
import Libreria.service.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class OrdinamentoTest {

    private ArrayList<Libro> libri;

    @BeforeEach
    void setUp() {
        libri = new ArrayList<>();
        libri.add(new Libro("Zebra Book", "Charlie Author", "978-3", Generi.ROMANZO, 1, StatoDellaLettura.LETTO));
        libri.add(new Libro("Alpha Book", "Alpha Author", "978-1", Generi.FANTASY, 5, StatoDellaLettura.DA_LEGGERE));
        libri.add(new Libro("Beta Book", "Beta Author", "978-2", Generi.GIALLO, 3, StatoDellaLettura.IN_LETTURA));
    }

    // =================== TEST POSITIVI ===================
    @Test
    @DisplayName("Test ordinamento per titolo")
    public void testOrdinaPerTitolo() {
        new OrdinaPerTitolo().ordina(libri);
        assertEquals("Alpha Book", libri.get(0).getTitolo());
        assertEquals("Beta Book", libri.get(1).getTitolo());
        assertEquals("Zebra Book", libri.get(2).getTitolo());
    }

    @Test
    @DisplayName("Test ordinamento per autore")
    public void testOrdinaPerAutore() {
        new OrdinaPerAutore().ordina(libri);
        assertEquals("Alpha Author", libri.get(0).getAutore());
    }

    @Test
    @DisplayName("Test ordinamento per valutazione")
    public void testOrdinaPerValutazione() {
        new OrdinaPerValutazione().ordina(libri);
        assertEquals(1, libri.get(0).getValutazione());
        assertEquals(5, libri.get(2).getValutazione());
    }

    // =================== TEST NEGATIVI ===================
    @Test
    @DisplayName("NEGATIVO: Ordinamento lista vuota")
    public void testOrdinamentoListaVuota() {
        ArrayList<Libro> listaVuota = new ArrayList<>();
        assertDoesNotThrow(() -> new OrdinaPerTitolo().ordina(listaVuota));
        assertTrue(listaVuota.isEmpty());
    }

    @Test
    @DisplayName("NEGATIVO: Ordinamento con elementi null")
    public void testOrdinamentoConNull() {
        libri.add(null);
        assertThrows(NullPointerException.class, () -> new OrdinaPerTitolo().ordina(libri));
    }
}