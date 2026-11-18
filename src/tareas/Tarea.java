package tareas;

import java.time.LocalDate;

/** Una tarea. Se guarda en una linea: id, estado, vencimiento, creada, texto (separados por tab). */
public record Tarea(int id, boolean hecha, LocalDate vence, LocalDate creada, String texto) {

    public String aLinea() {
        return id + "\t" + (hecha ? "1" : "0") + "\t" + (vence == null ? "-" : vence) + "\t" + creada + "\t" + texto;
    }

    public static Tarea deLinea(String linea) {
        String[] p = linea.split("\t", 5);
        if (p.length < 5) throw new IllegalArgumentException("Linea invalida: " + linea);
        return new Tarea(Integer.parseInt(p[0]), p[1].equals("1"),
                p[2].equals("-") ? null : LocalDate.parse(p[2]), LocalDate.parse(p[3]), p[4]);
    }

    public Tarea conHecha(boolean h) {
        return new Tarea(id, h, vence, creada, texto);
    }

    public boolean vencida(LocalDate hoy) {
        return !hecha && vence != null && vence.isBefore(hoy);
    }
}
