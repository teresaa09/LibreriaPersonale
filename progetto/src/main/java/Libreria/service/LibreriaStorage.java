package Libreria.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LibreriaStorage {

    private static final String FILE_PATH = "data/libreria.json"; // cartella sicura
    private static final ObjectMapper mapper = new ObjectMapper();

    // salva la lista di libri su file JSON
    public static void salva(List<Libro> libri) throws IOException {
        System.out.println("Salvo " + libri.size() + " libri nel JSON...");
        Path path = Paths.get(FILE_PATH);

        // crea la cartella se non esiste
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(libri);
        Files.write(path, json.getBytes(StandardCharsets.UTF_8));
        System.out.println("Libreria salvata su file JSON: " + path.toAbsolutePath());
        System.out.println("Contenuto JSON salvato:\n" + json);

    }

    // Carica la lista di libri dal file JSON
    public static List<Libro> carica() {
        try {
            Path path = Paths.get(FILE_PATH);

            if (!Files.exists(path)) {
                System.out.println("Nessun file trovato, creo una nuova libreria.");
                return new ArrayList<>();
            }

            byte[] jsonData = Files.readAllBytes(path);
            CollectionType listType = mapper.getTypeFactory()
                    .constructCollectionType(ArrayList.class, Libro.class);

            List<Libro> libri = mapper.readValue(jsonData, listType);
            System.out.println("Libreria caricata dal file JSON: " + path.toAbsolutePath());
            return libri;

        } catch (IOException e) {
            System.err.println("Errore durante la lettura del file JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}