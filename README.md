# tareas-cli-java

Gestor de tareas por linea de comandos escrito en Java 21, sin dependencias externas. **Es un proyecto de practica** para mi portfolio, no pretende reemplazar a una app de tareas real.

## Que hace

- `agregar <texto> [--vence AAAA-MM-DD]`: crea una tarea, con fecha de vencimiento opcional.
- `listar [--todas|--hechas]`: muestra las pendientes (por defecto) ordenadas por vencimiento y marca las vencidas.
- `hecha <id>`: marca una tarea como hecha.
- `borrar <id>`: elimina una tarea.
- `resumen`: total, hechas (con porcentaje), pendientes, vencidas y las que vencen en los proximos 7 dias.

Las tareas se guardan en un archivo de texto (TSV) en `~/.tareas.tsv`, o en la ruta de la variable de entorno `TAREAS_ARCHIVO`.

## Como correrlo

Requiere JDK 17 o superior (probado con OpenJDK 21).

```bash
./tareas agregar Comprar pan --vence 2026-12-01
./tareas listar
./tareas hecha 1
./tareas resumen
```

El script `tareas` compila a `out/` la primera vez (borra `out/` para recompilar).

## Pruebas

Un chequeo simple sin framework:

```bash
javac -d out src/tareas/*.java test/Pruebas.java && java -cp out Pruebas
```

## Estructura

- `src/tareas/Tarea.java`: modelo (record) y serializacion de una linea.
- `src/tareas/Repositorio.java`: lectura y escritura del archivo.
- `src/tareas/Main.java`: comandos y salida.
