import tareas.*;
import java.nio.file.*;
import java.time.LocalDate;

public class Pruebas {
    static int fallos = 0;
    static void check(boolean ok, String msg) { if (!ok) { fallos++; System.out.println("FALLA: " + msg); } }

    public static void main(String[] a) throws Exception {
        Path f = Files.createTempFile("tareas", ".tsv");
        Files.delete(f);
        LocalDate hoy = LocalDate.of(2026, 1, 10);
        Repositorio r = new Repositorio(f);
        Tarea t1 = r.agregar("comprar pan", LocalDate.of(2026, 1, 5), hoy);
        r.agregar("estudiar\tjava", null, hoy);
        r.marcar(2, true);
        r.guardar();
        Repositorio r2 = new Repositorio(f);
        check(r2.todas().size() == 2, "persistencia");
        check(r2.todas().get(1).hecha(), "marcar hecha");
        check(r2.todas().get(1).texto().equals("estudiar java"), "tab reemplazado");
        check(t1.vencida(hoy), "vencida");
        r2.borrar(1);
        check(r2.agregar("x", null, hoy).id() == 3, "ids no se reutilizan");
        try { r2.borrar(99); check(false, "borrar inexistente"); } catch (java.util.NoSuchElementException e) { }
        System.out.println(fallos == 0 ? "OK: todas las pruebas pasaron" : fallos + " fallos");
        System.exit(fallos == 0 ? 0 : 1);
    }
}
