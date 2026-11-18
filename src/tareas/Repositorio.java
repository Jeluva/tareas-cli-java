package tareas;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

/** Persistencia en un archivo de texto plano. */
public class Repositorio {
    private final Path archivo;
    private final List<Tarea> tareas = new ArrayList<>();

    public Repositorio(Path archivo) throws IOException {
        this.archivo = archivo;
        if (Files.exists(archivo)) {
            for (String l : Files.readAllLines(archivo)) {
                if (!l.isBlank()) tareas.add(Tarea.deLinea(l));
            }
        }
    }

    public List<Tarea> todas() {
        return List.copyOf(tareas);
    }

    public Tarea agregar(String texto, LocalDate vence, LocalDate hoy) {
        int id = tareas.stream().mapToInt(Tarea::id).max().orElse(0) + 1;
        Tarea t = new Tarea(id, false, vence, hoy, texto.replace('\t', ' '));
        tareas.add(t);
        return t;
    }

    public Tarea marcar(int id, boolean hecha) {
        for (int i = 0; i < tareas.size(); i++) {
            if (tareas.get(i).id() == id) {
                Tarea t = tareas.get(i).conHecha(hecha);
                tareas.set(i, t);
                return t;
            }
        }
        throw new NoSuchElementException("No existe la tarea " + id);
    }

    public Tarea borrar(int id) {
        Tarea t = tareas.stream().filter(x -> x.id() == id).findFirst()
                .orElseThrow(() -> new NoSuchElementException("No existe la tarea " + id));
        tareas.remove(t);
        return t;
    }

    public void guardar() throws IOException {
        if (archivo.getParent() != null) Files.createDirectories(archivo.getParent());
        Files.write(archivo, tareas.stream().map(Tarea::aLinea).toList());
    }
}
