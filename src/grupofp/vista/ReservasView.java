package grupofp.vista;

import grupofp.controlador.ReservasController;
import grupofp.modelo.Cliente;
import grupofp.modelo.ClienteNoEncontradoException;
import grupofp.modelo.DatosInvalidosException;
import grupofp.modelo.Habitacion;
import grupofp.modelo.HabitacionNoDisponibleException;
import grupofp.modelo.Hotel;
import grupofp.modelo.Reserva;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservasView {
    private final ReservasController controlador;
    private final Consola consola;
    private final Hotel hotel;

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
            System.out.println("3. Buscar reservas por cliente");
            System.out.println("4. Buscar reservas por hotel");
            System.out.println("5. Buscar reservas por fecha de entrada");
            System.out.println("6. Buscar reservas por fecha de salida");
            System.out.println("7. Actualizar reserva");
            System.out.println("8. Eliminar reserva");
            System.out.println("0. Volver");
            opcion = consola.pedirEntero("Elige una opcion: ");

            switch (opcion) {
                case 1 -> crearReserva();
                case 2 -> listarReservas();
                case 3 -> listarReservasPorCliente();
                case 4 -> listarReservasPorHotel();
                case 5 -> listarReservasPorFechaEntrada();
                case 6 -> listarReservasPorFechaSalida();
                case 7 -> actualizarReserva();
                case 8 -> eliminarReserva();
                case 0 -> { }
                default -> System.out.println("Opcion no valida.");
            }
        } while (opcion != 0);
    }

    private void crearReserva() {
        try {
            Cliente cliente = controlador.buscarCliente(consola.pedirTexto("Email del cliente: "));
            LocalDate fechaEntrada = consola.pedirFecha("Fecha de entrada (AAAA-MM-DD): ");
            LocalDate fechaSalida = consola.pedirFecha("Fecha de salida (AAAA-MM-DD): ");

            List<Habitacion> disponibles = controlador.obtenerHabitacionesDisponibles(fechaEntrada, fechaSalida);
            if (disponibles.isEmpty()) {
                System.out.println("No hay habitaciones disponibles entre " + fechaEntrada + " y " + fechaSalida + ".");
                return;
            }

            System.out.println("Habitaciones disponibles:");
            for (Habitacion h : disponibles) {
                System.out.println(" - Hab. " + h.getNumero() + " (" + h.getCategoria()
                        + ", cap: " + h.getCapacidad() + " pers., " + h.getPrecioNoche() + " EUR/noche)");
            }

            Habitacion habitacion = controlador.buscarHabitacion(consola.pedirTexto("Numero de habitacion: "));
            int cantidadPersonas = consola.pedirEntero("Numero de personas: ");

            List<Habitacion> habitaciones = new ArrayList<>();
            habitaciones.add(habitacion);

            Reserva reserva = new Reserva(0, fechaEntrada, fechaSalida, cantidadPersonas,
                    habitacion.getPrecioNoche(), habitaciones.size(), cliente, hotel, habitaciones);

            controlador.registrarReserva(reserva);
            System.out.println("Reserva #" + reserva.getIdReserva()
                    + " creada (" + reserva.getNoches() + " noches). Importe total: " + reserva.calcularImporte() + " EUR");

        } catch (ClienteNoEncontradoException | HabitacionNoDisponibleException | DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarReservas() {
        try {
            imprimirListaReservas(controlador.obtenerTodasLasReservas(), "No hay reservas registradas.");
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarReservasPorCliente() {
        String email = consola.pedirTexto("Email del cliente: ");
        try {
            Cliente cliente = controlador.buscarCliente(email);
            imprimirListaReservas(controlador.obtenerReservasPorCliente(cliente.getIdCliente()),
                    "El cliente " + cliente + " no tiene reservas registradas.");
        } catch (ClienteNoEncontradoException | DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarReservasPorHotel() {
        int idHotelConsulta = consola.pedirEntero("ID del hotel (actual: " + hotel.getIdHotel() + "): ");
        try {
            imprimirListaReservas(controlador.obtenerReservasPorHotel(idHotelConsulta),
                    "No hay reservas registradas para el hotel con ID " + idHotelConsulta + ".");
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarReservasPorFechaEntrada() {
        LocalDate fechaEntrada = consola.pedirFecha("Fecha de entrada (AAAA-MM-DD): ");
        try {
            imprimirListaReservas(controlador.obtenerReservasPorFechaEntrada(fechaEntrada),
                    "No hay reservas con fecha de entrada " + fechaEntrada + ".");
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarReservasPorFechaSalida() {
        LocalDate fechaSalida = consola.pedirFecha("Fecha de salida (AAAA-MM-DD): ");
        try {
            imprimirListaReservas(controlador.obtenerReservasPorFechaSalida(fechaSalida),
                    "No hay reservas con fecha de salida " + fechaSalida + ".");
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void actualizarReserva() {
        int idReserva = consola.pedirEntero("ID de la reserva a actualizar: ");
        try {
            Reserva existente = controlador.obtenerReservaPorId(idReserva);
            System.out.print("Reserva actual -> ");
            mostrarReserva(existente);

            LocalDate nuevaEntrada = consola.pedirFecha("Nueva fecha de entrada (AAAA-MM-DD): ");
            LocalDate nuevaSalida = consola.pedirFecha("Nueva fecha de salida (AAAA-MM-DD): ");

            List<Habitacion> disponibles = controlador.obtenerHabitacionesDisponibles(nuevaEntrada, nuevaSalida);
            System.out.println("Habitaciones disponibles en las nuevas fechas:");
            for (Habitacion h : disponibles) {
                System.out.println(" - Hab. " + h.getNumero() + " (" + h.getCategoria()
                        + ", cap: " + h.getCapacidad() + " pers., " + h.getPrecioNoche() + " EUR/noche)");
            }

            Habitacion nuevaHabitacion = controlador.buscarHabitacion(consola.pedirTexto("Nuevo numero de habitacion: "));
            int nuevasPersonas = consola.pedirEntero("Nuevo numero de personas: ");

            List<Habitacion> nuevasHabitaciones = new ArrayList<>();
            nuevasHabitaciones.add(nuevaHabitacion);

            Reserva actualizada = new Reserva(idReserva, nuevaEntrada, nuevaSalida, nuevasPersonas,
                    nuevaHabitacion.getPrecioNoche(), nuevasHabitaciones.size(),
                    existente.getCliente(), existente.getHotel(), nuevasHabitaciones);

            controlador.actualizarReserva(actualizada);
            System.out.println("Reserva #" + idReserva + " actualizada. Nuevo total: " + actualizada.calcularImporte() + " EUR");
        } catch (HabitacionNoDisponibleException | DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarReserva() {
        int idReserva = consola.pedirEntero("ID de la reserva a eliminar: ");
        try {
            controlador.obtenerReservaPorId(idReserva);
            controlador.eliminarReserva(idReserva);
            System.out.println("Reserva #" + idReserva + " eliminada correctamente.");
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void imprimirListaReservas(List<Reserva> reservas, String mensajeVacio) {
        if (reservas.isEmpty()) {
            System.out.println(mensajeVacio);
            return;
        }
        for (Reserva r : reservas) {
            mostrarReserva(r);
        }
    }

    private void mostrarReserva(Reserva r) {
        System.out.println("Reserva #" + r.getIdReserva()
                + " | Hotel: " + r.getHotel().getNombre()
                + " | Cliente: " + r.getCliente()
                + " | Entrada: " + r.getFechaEntrada()
                + " | Salida: " + r.getFechaSalida()
                + " (" + r.getNoches() + " noches)"
                + " | Personas: " + r.getCantidadPersonas()
                + " | Total: " + r.calcularImporte() + " EUR");
    }
}