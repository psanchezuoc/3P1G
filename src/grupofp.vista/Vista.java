package grupofp.vista;

import grupofp.controlador.Controlador;
import grupofp.modelo.Cliente;
import grupofp.modelo.ClienteNoEncontradoException;
import grupofp.modelo.DatosInvalidosException;
import grupofp.modelo.Habitacion;
import grupofp.modelo.HabitacionEstandar;
import grupofp.modelo.HabitacionNoDisponibleException;
import grupofp.modelo.HabitacionSuperior;
import grupofp.modelo.Hotel;
import grupofp.modelo.Reserva;
import grupofp.modelo.Suite;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Capa de presentacion (Vista) del patron MVC.
 * Solo se encarga de mostrar menus, pedir datos con Scanner y ensenar resultados.
 * Toda la logica se delega en el Controlador; la Vista nunca accede a Datos directamente.
 */
public class Vista {

    private final Controlador controlador;
    private final Scanner teclado;
    private final Hotel hotel; // De momento la app gestiona un unico hotel

    private int siguienteIdCliente = 1;
    private int siguienteIdReserva = 1;

    public Vista() {
        this.controlador = new Controlador();
        this.teclado = new Scanner(System.in);
        this.hotel = new Hotel(1, "Hotel BCN", "Barcelona");
    }

    // ===================== MENU PRINCIPAL =====================

    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n========== " + hotel.getNombre().toUpperCase() + " ==========");
            System.out.println("1. Gestion de clientes");
            System.out.println("2. Gestion de habitaciones");
            System.out.println("3. Gestion de reservas");
            System.out.println("0. Salir");
            opcion = pedirEntero("Elige una opcion: ");

            switch (opcion) {
                case 1 -> menuClientes();
                case 2 -> menuHabitaciones();
                case 3 -> menuReservas();
                case 0 -> System.out.println("Hasta pronto!");
                default -> System.out.println("Opcion no valida.");
            }
        } while (opcion != 0);
    }

    // ===================== CLIENTES =====================

    private void menuClientes() {
        int opcion;
        do {
            System.out.println("\n--- CLIENTES ---");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Buscar cliente por email");
            System.out.println("0. Volver");
            opcion = pedirEntero("Elige una opcion: ");

            switch (opcion) {
                case 1 -> registrarCliente();
                case 2 -> buscarCliente();
                case 0 -> { }
                default -> System.out.println("Opcion no valida.");
            }
        } while (opcion != 0);
    }

    private void registrarCliente() {
        String nombre = pedirTexto("Nombre: ");
        String apellido = pedirTexto("Apellido: ");
        String email = pedirTexto("Email: ");

        try {
            controlador.registrarCliente(new Cliente(siguienteIdCliente, nombre, apellido, email));
            siguienteIdCliente++;
            System.out.println("Cliente registrado correctamente.");
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarCliente() {
        String email = pedirTexto("Email del cliente: ");
        try {
            Cliente c = controlador.buscarCliente(email);
            System.out.println(c + " | Email: " + c.getEmail() + " | ID: " + c.getIdCliente());
        } catch (ClienteNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===================== HABITACIONES =====================

    private void menuHabitaciones() {
        int opcion;
        do {
            System.out.println("\n--- HABITACIONES ---");
            System.out.println("1. Registrar habitacion");
            System.out.println("2. Buscar habitacion por numero");
            System.out.println("0. Volver");
            opcion = pedirEntero("Elige una opcion: ");

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
        int tipo = pedirEntero("Tipo: ");
        String numero = pedirTexto("Numero de habitacion: ");
        int capacidad = pedirEntero("Capacidad (personas): ");
        double precio = pedirDecimal("Precio por noche (EUR): ");

        Habitacion habitacion;
        switch (tipo) {
            case 1 -> habitacion = new HabitacionEstandar(numero, capacidad, precio,
                    pedirTexto("Tipo de cama: "));
            case 2 -> habitacion = new HabitacionSuperior(numero, capacidad, precio,
                    pedirSiNo("Tiene balcon? (s/n): "));
            case 3 -> habitacion = new Suite(numero, capacidad, precio,
                    pedirSiNo("Tiene jacuzzi? (s/n): "));
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
        String numero = pedirTexto("Numero de habitacion: ");
        try {
            mostrarHabitacion(controlador.buscarHabitacion(numero));
        } catch (HabitacionNoDisponibleException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===================== RESERVAS =====================

    private void menuReservas() {
        int opcion;
        do {
            System.out.println("\n--- RESERVAS ---");
            System.out.println("1. Crear reserva");
            System.out.println("2. Listar todas las reservas");
            System.out.println("0. Volver");
            opcion = pedirEntero("Elige una opcion: ");

            switch (opcion) {
                case 1 -> crearReserva();
                case 2 -> listarReservas();
                case 0 -> { }
                default -> System.out.println("Opcion no valida.");
            }
        } while (opcion != 0);
    }

    private void crearReserva() {
        try {
            Cliente cliente = controlador.buscarCliente(pedirTexto("Email del cliente: "));
            Habitacion habitacion = controlador.buscarHabitacion(pedirTexto("Numero de habitacion: "));
            int noches = pedirEntero("Numero de noches: ");

            List<Habitacion> habitaciones = new ArrayList<>();
            habitaciones.add(habitacion);

            Reserva reserva = new Reserva(siguienteIdReserva, noches, habitacion.getPrecioNoche(),
                    habitaciones.size(), cliente, hotel, habitaciones);

            controlador.registrarReserva(reserva);
            siguienteIdReserva++;
            System.out.println("Reserva creada. Importe total: " + reserva.calcularImporte() + " EUR");

        } catch (ClienteNoEncontradoException | HabitacionNoDisponibleException | DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarReservas() {
        List<Reserva> reservas = controlador.obtenerTodasLasReservas();
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas registradas.");
            return;
        }
        for (Reserva r : reservas) {
            System.out.println("Reserva #" + r.getIdReserva()
                    + " | " + r.getCliente()
                    + " | Noches: " + r.getNoches()
                    + " | Habitaciones: " + r.getNumeroHabitaciones()
                    + " | Total: " + r.calcularImporte() + " EUR");
        }
    }

    // ===================== METODOS AUXILIARES DE ENTRADA =====================

    private void mostrarHabitacion(Habitacion h) {
        System.out.println("Habitacion " + h.getNumero()
                + " | Categoria: " + h.getCategoria()
                + " | Capacidad: " + h.getCapacidad()
                + " | Precio/noche: " + h.getPrecioNoche() + " EUR");
    }

    private String pedirTexto(String mensaje) {
        System.out.print(mensaje);
        return teclado.nextLine().trim();
    }

    private int pedirEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(pedirTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Introduce un numero entero valido.");
            }
        }
    }

    private double pedirDecimal(String mensaje) {
        while (true) {
            try {
                return Double.parseDouble(pedirTexto(mensaje).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Introduce un numero valido (ej. 85.50).");
            }
        }
    }

    private boolean pedirSiNo(String mensaje) {
        while (true) {
            String r = pedirTexto(mensaje).toLowerCase();
            if (r.equals("s")) return true;
            if (r.equals("n")) return false;
            System.out.println("Responde 's' o 'n'.");
        }
    }
}