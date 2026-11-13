package grupofp.vista;

import grupofp.controlador.ReservasController;
import grupofp.modelo.Cliente;
import grupofp.modelo.ClienteNoEncontradoException;
import grupofp.modelo.DatosInvalidosException;
import grupofp.modelo.Habitacion;
import grupofp.modelo.HabitacionNoDisponibleException;
import grupofp.modelo.Hotel;
import grupofp.modelo.Reserva;

import java.util.ArrayList;
import java.util.List;

public class ReservasView {
    private final ReservasController controlador;
    private final Consola consola;
    private final Hotel hotel;
    private int siguienteIdReserva = 1;

    public ReservasView(ReservasController controlador, Consola consola, Hotel hotel) {
        this.controlador = controlador;
        this.consola = consola;
        this.hotel = hotel;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n--- RESERVAS ---");
            System.out.println("1. Crear reserva");
            System.out.println("2. Listar todas las reservas");
            System.out.println("0. Volver");
            opcion = consola.pedirEntero("Elige una opcion: ");

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
            Cliente cliente = controlador.buscarCliente(consola.pedirTexto("Email del cliente: "));
            Habitacion habitacion = controlador.buscarHabitacion(consola.pedirTexto("Numero de habitacion: "));
            int noches = consola.pedirEntero("Numero de noches: ");

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
}