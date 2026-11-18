package tareas;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    static final String AYUDA = """
            Uso: tareas <comando> [argumentos]
              agregar <texto> [--vence AAAA-MM-DD]   crea una tarea
              listar [--todas|--hechas]              pendientes por defecto, ordenadas por vencimiento
              hecha <id>                             marca como hecha
              borrar <id>                            elimina la tarea
              resumen                                totales, vencidas y proximas a vencer
            Archivo: ~/.tareas.tsv (o la variable TAREAS_ARCHIVO)
            """;

    public static void main(String[] args) {
        System.exit(ejecutar(args, LocalDate.now(), archivoPorDefecto()));
    }

    static Path archivoPorDefecto() {
        String env = System.getenv("TAREAS_ARCHIVO");
        return env != null ? Path.of(env) : Path.of(System.getProperty("user.home"), ".tareas.tsv");
    }

    static int ejecutar(String[] args, LocalDate hoy, Path archivo) {
        if (args.length == 0 || args[0].equals("-h") || args[0].equals("--help")) {
            System.out.print(AYUDA);
            return args.length == 0 ? 1 : 0;
        }
        try {
            Repositorio repo = new Repositorio(archivo);
            String cmd = args[0];
            switch (cmd) {
                case "agregar" -> {
                    List<String> palabras = new ArrayList<>();
                    LocalDate vence = null;
                    for (int i = 1; i < args.length; i++) {
                        if (args[i].equals("--vence") && i + 1 < args.length) vence = LocalDate.parse(args[++i]);
                        else palabras.add(args[i]);
                    }
                    if (palabras.isEmpty()) throw new IllegalArgumentException("Falta el texto de la tarea");
                    Tarea t = repo.agregar(String.join(" ", palabras), vence, hoy);
                    repo.guardar();
                    System.out.println("Agregada #" + t.id() + ": " + t.texto());
                }
                case "listar" -> listar(repo, args.length > 1 ? args[1] : "", hoy);
                case "hecha" -> {
                    Tarea t = repo.marcar(id(args), true);
                    repo.guardar();
                    System.out.println("Hecha #" + t.id() + ": " + t.texto());
                }
                case "borrar" -> {
                    Tarea t = repo.borrar(id(args));
                    repo.guardar();
                    System.out.println("Borrada #" + t.id() + ": " + t.texto());
                }
                case "resumen" -> resumen(repo, hoy);
                default -> {
                    System.err.println("Comando desconocido: " + cmd + "\n");
                    System.err.print(AYUDA);
                    return 1;
                }
            }
            return 0;
        } catch (DateTimeParseException e) {
            System.err.println("Fecha invalida, usa AAAA-MM-DD");
        } catch (NumberFormatException e) {
            System.err.println("El id tiene que ser un numero");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return 1;
    }

    static int id(String[] args) {
        if (args.length < 2) throw new IllegalArgumentException("Falta el id");
        return Integer.parseInt(args[1]);
    }

    static void listar(Repositorio repo, String filtro, LocalDate hoy) {
        List<Tarea> lista = repo.todas().stream()
                .filter(t -> switch (filtro) {
                    case "--todas" -> true;
                    case "--hechas" -> t.hecha();
                    default -> !t.hecha();
                })
                .sorted(Comparator.comparing((Tarea t) -> t.vence() == null ? LocalDate.MAX : t.vence())
                        .thenComparingInt(Tarea::id))
                .toList();
        if (lista.isEmpty()) {
            System.out.println("(sin tareas)");
            return;
        }
        for (Tarea t : lista) {
            String venc = t.vence() == null ? "" : "  [vence " + t.vence() + (t.vencida(hoy) ? " - VENCIDA" : "") + "]";
            System.out.printf("%3d [%s] %s%s%n", t.id(), t.hecha() ? "x" : " ", t.texto(), venc);
        }
    }

    static void resumen(Repositorio repo, LocalDate hoy) {
        List<Tarea> todas = repo.todas();
        long hechas = todas.stream().filter(Tarea::hecha).count();
        long vencidas = todas.stream().filter(t -> t.vencida(hoy)).count();
        long proximas = todas.stream()
                .filter(t -> !t.hecha() && t.vence() != null && !t.vence().isBefore(hoy) && !t.vence().isAfter(hoy.plusDays(7)))
                .count();
        int pct = todas.isEmpty() ? 0 : (int) Math.round(100.0 * hechas / todas.size());
        System.out.println("Total: " + todas.size() + " | Hechas: " + hechas + " (" + pct + "%) | Pendientes: " + (todas.size() - hechas));
        System.out.println("Vencidas: " + vencidas + " | Vencen en los proximos 7 dias: " + proximas);
    }
}
