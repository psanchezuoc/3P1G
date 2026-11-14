package grupofp.vista;

import grupofp.controlador.HabitacionController;
import grupofp.modelo.DatosInvalidosException;
import grupofp.modelo.Habitacion;
import grupofp.modelo.HabitacionEstandar;
import grupofp.modelo.HabitacionNoDisponibleException;
import grupofp.modelo.HabitacionSuperior;
import grupofp.modelo.Suite;

import java.time.LocalDate;
import java.util.List;

public class HabitacionView {
    private final HabitacionController controlador;
    private final Consola consola;

    public HabitacionView(HabitacionController controlador, Consola consola) {
        this.controlador = controlador;
        this.consola = consola;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n--- HABITACIONES ---");
            System.out.println("1. Registrar habitacion");
            System.out.println("2. Buscar habitacion por numero");
            System.out.println("3. Buscar habitaciones por categoria");
            System.out.println("4. Listar habitaciones de este hotel");
            System.out.println("5. Listar todas las habitaciones de la cadena");
            System.out.println("6. Buscar habitaciones disponibles por fechas");
            System.out.println("7. Filtrar habitaciones por numero de personas");
            System.out.println("8. Actualizar habitacion");
            System.out.println("9. Eliminar habitacion");
            System.out.println("0. Volver");
            opcion = consola.pedirEntero("Elige una opcion: ");

            switch (opcion) {
                case 1 -> registrarHabitacion();
                case 2 -> buscarHabitacion();
                case 3 -> buscarHabitacionesPorCategoria();
                case 4 -> listarHabitacionesHotel();
                case 5 -> listarTodasLasHabitaciones();
                case 6 -> buscarHabitacionesDisponibles();
                case 7 -> filtrarHabitacionesPorCapacidad();
                case 8 -> actualizarHabitacion();
                case 9 -> eliminarHabitacion();
                case 0 -> { }
                default -> System.out.println("Opcion no valida.");
            }
        } while (opcion != 0);
    }

    private void registrarHabitacion() {
        String numero = consola.pedirTexto("Numero de habitacion: ");
        Habitacion habitacion = pedirDatosHabitacion(numero);
        if (habitacion == null) return;

        try {
            controlador.registrarHabitacion(habitacion);
            System.out.println("Habitacion " + numero + " (" + habitacion.getCategoria() + ") registrada.");
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarHabitacion() {
        String numero = consola.pedirTexto("Numero de habitacion: ");
        try {
            mostrarHabitacion(controlador.buscarHabitacion(numero));
        } catch (HabitacionNoDisponibleException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarHabitacionesPorCategoria() {
        String categoria = consola.pedirTexto("Categoria (Estándar / Superior / Suite): ");
        try {
            imprimirListaHabitaciones(controlador.buscarHabitacionesPorCategoria(categoria),
                    "No hay habitaciones registradas en la categoria: " + categoria);
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarHabitacionesHotel() {
        try {
            imprimirListaHabitaciones(controlador.obtenerHabitacionesDelHotel(),
                    "No hay habitaciones registradas en este hotel.");
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarTodasLasHabitaciones() {
        try {
            imprimirListaHabitaciones(controlador.obtenerTodasLasHabitaciones(),
                    "No hay habitaciones registradas en la base de datos.");
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarHabitacionesDisponibles() {
        LocalDate entrada = consola.pedirFecha("Fecha de entrada (AAAA-MM-DD): ");
        LocalDate salida = consola.pedirFecha("Fecha de salida (AAAA-MM-DD): ");
        try {
            imprimirListaHabitaciones(controlador.obtenerHabitacionesDisponibles(entrada, salida),
                    "No hay habitaciones disponibles entre " + entrada + " y " + salida + ".");
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void filtrarHabitacionesPorCapacidad() {
        int personas = consola.pedirEntero("Numero minimo de personas: ");
        try {
            imprimirListaHabitaciones(controlador.obtenerHabitacionesPorCapacidad(personas),
                    "No hay habitaciones con capacidad para " + personas + " o mas personas.");
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void actualizarHabitacion() {
        String numero = consola.pedirTexto("Numero de habitacion a actualizar: ");
        try {
            Habitacion actual = controlador.buscarHabitacion(numero);
            System.out.print("Datos actuales -> ");
            mostrarHabitacion(actual);

            Habitacion actualizada = pedirDatosHabitacion(numero);
            if (actualizada == null) return;

            controlador.actualizarHabitacion(actualizada);
            System.out.println("Habitacion " + numero + " actualizada correctamente.");
        } catch (HabitacionNoDisponibleException | DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarHabitacion() {
        String numero = consola.pedirTexto("Numero de habitacion a eliminar: ");
        try {
            controlador.buscarHabitacion(numero);
            controlador.eliminarHabitacion(numero);
            System.out.println("Habitacion " + numero + " eliminada correctamente.");
        } catch (HabitacionNoDisponibleException | DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private Habitacion pedirDatosHabitacion(String numero) {
        System.out.println("Tipo de habitacion: 1. Estandar  2. Superior  3. Suite");
        int tipo = consola.pedirEntero("Tipo: ");
        int capacidad = consola.pedirEntero("Capacidad (personas): ");
        double precio = consola.pedirDecimal("Precio por noche (EUR): ");

        return switch (tipo) {
            case 1 -> new HabitacionEstandar(numero, capacidad, precio, consola.pedirTexto("Tipo de cama: "));
            case 2 -> new HabitacionSuperior(numero, capacidad, precio, consola.pedirSiNo("Tiene balcon? (s/n): "));
            case 3 -> new Suite(numero, capacidad, precio, consola.pedirSiNo("Tiene jacuzzi? (s/n): "));
            default -> {
                System.out.println("Tipo no valido.");
                yield null;
            }
        };
    }

    private void imprimirListaHabitaciones(List<Habitacion> habitaciones, String mensajeVacio) {
        if (habitaciones.isEmpty()) {
            System.out.println(mensajeVacio);
            return;
        }
        for (Habitacion h : habitaciones) {
            mostrarHabitacion(h);
        }
    }

    private void mostrarHabitacion(Habitacion h) {
        System.out.println("Habitacion " + h.getNumero()
                + " | Categoria: " + h.getCategoria()
                + " | Capacidad: " + h.getCapacidad()
                + " | Precio/noche: " + h.getPrecioNoche() + " EUR");
    }
}