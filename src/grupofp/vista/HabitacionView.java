package grupofp.vista;

import grupofp.controlador.HabitacionController;
import grupofp.modelo.DatosInvalidosException;
import grupofp.modelo.Habitacion;
import grupofp.modelo.HabitacionEstandar;
import grupofp.modelo.HabitacionNoDisponibleException;
import grupofp.modelo.HabitacionSuperior;
import grupofp.modelo.Suite;

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
            System.out.println("0. Volver");
            opcion = consola.pedirEntero("Elige una opcion: ");

            switch (opcion) {
                case 1 -> registrarHabitacion();
                case 2 -> buscarHabitacion();
                case 0 -> { }
                default -> System.out.println("Opcion no valida.");
            }
        } while (opcion != 0);
    }

    private void registrarHabitacion() {
        System.out.println("Tipo de habitacion: 1. Estandar  2. Superior  3. Suite");
        int tipo = consola.pedirEntero("Tipo: ");
        String numero = consola.pedirTexto("Numero de habitacion: ");
        int capacidad = consola.pedirEntero("Capacidad (personas): ");
        double precio = consola.pedirDecimal("Precio por noche (EUR): ");

        Habitacion habitacion;
        switch (tipo) {
            case 1 -> habitacion = new HabitacionEstandar(numero, capacidad, precio, consola.pedirTexto("Tipo de cama: "));
            case 2 -> habitacion = new HabitacionSuperior(numero, capacidad, precio, consola.pedirSiNo("Tiene balcon? (s/n): "));
            case 3 -> habitacion = new Suite(numero, capacidad, precio, consola.pedirSiNo("Tiene jacuzzi? (s/n): "));
            default -> {
                System.out.println("Tipo no valido.");
                return;
            }
        }

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

    private void mostrarHabitacion(Habitacion h) {
        System.out.println("Habitacion " + h.getNumero()
                + " | Categoria: " + h.getCategoria()
                + " | Capacidad: " + h.getCapacidad()
                + " | Precio/noche: " + h.getPrecioNoche() + " EUR");
    }
}