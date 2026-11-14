package grupofp.controlador;

import grupofp.dao.ClienteDAO;
import grupofp.dao.HabitacionDAO;
import grupofp.dao.ReservaDAO;
import grupofp.modelo.Cliente;
import grupofp.modelo.ClienteNoEncontradoException;
import grupofp.modelo.DatosInvalidosException;
import grupofp.modelo.Habitacion;
import grupofp.modelo.HabitacionNoDisponibleException;
import grupofp.modelo.Reserva;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReservasController {
    private final ReservaDAO reservaDAO;
    private final ClienteDAO clienteDAO;
    private final HabitacionDAO habitacionDAO;
    private final int idHotel;

    public ReservasController(ReservaDAO reservaDAO, ClienteDAO clienteDAO, HabitacionDAO habitacionDAO, int idHotel) {
        this.reservaDAO = reservaDAO;
        this.clienteDAO = clienteDAO;
        this.habitacionDAO = habitacionDAO;
        this.idHotel = idHotel;
    }

    public Cliente buscarCliente(String email) throws ClienteNoEncontradoException {
        try {
            Cliente cliente = clienteDAO.obtenerClientePorEmail(email);
            if (cliente == null) {
                throw new ClienteNoEncontradoException("No existe ningún cliente con el email: " + email);
            }
            return cliente;
        } catch (SQLException e) {
            throw new ClienteNoEncontradoException("Error al consultar el cliente: " + e.getMessage());
        }
    }

    public Habitacion buscarHabitacion(String numero) throws HabitacionNoDisponibleException {
        try {
            Habitacion habitacion = habitacionDAO.obtenerHabitacion(numero, idHotel);
            if (habitacion == null) {
                throw new HabitacionNoDisponibleException("No existe la habitación " + numero + " en este hotel.");
            }
            return habitacion;
        } catch (SQLException e) {
            throw new HabitacionNoDisponibleException("Error al consultar la habitación: " + e.getMessage());
        }
    }

    public List<Habitacion> obtenerHabitacionesDisponibles(LocalDate fechaEntrada, LocalDate fechaSalida) throws DatosInvalidosException {
        if (fechaEntrada == null || fechaSalida == null || !fechaSalida.isAfter(fechaEntrada)) {
            throw new DatosInvalidosException("La fecha de salida debe ser posterior a la fecha de entrada.");
        }
        try {
            return habitacionDAO.obtenerHabitacionesDisponibles(idHotel, fechaEntrada, fechaSalida);
        } catch (SQLException e) {
            throw new DatosInvalidosException("Error al consultar disponibilidad: " + e.getMessage());
        }
    }

    public void registrarReserva(Reserva reserva) throws DatosInvalidosException {
        validarReserva(reserva);
        try {
            reservaDAO.crearReserva(reserva);
        } catch (SQLException e) {
            throw new DatosInvalidosException("No se pudo registrar la reserva: " + e.getMessage());
        }
    }

    public Reserva obtenerReservaPorId(int idReserva) throws DatosInvalidosException {
        try {
            Reserva reserva = reservaDAO.obtenerReserva(idReserva);
            if (reserva == null) {
                throw new DatosInvalidosException("No existe ninguna reserva con ID: " + idReserva);
            }
            return reserva;
        } catch (SQLException e) {
            throw new DatosInvalidosException("Error al consultar la reserva: " + e.getMessage());
        }
    }

    public List<Reserva> obtenerTodasLasReservas() throws DatosInvalidosException {
        try {
            return reservaDAO.obtenerTodasLasReservas();
        } catch (SQLException e) {
            throw new DatosInvalidosException("Error al obtener el listado de reservas: " + e.getMessage());
        }
    }

    public List<Reserva> obtenerReservasPorCliente(int idCliente) throws DatosInvalidosException {
        try {
            return reservaDAO.obtenerReservasPorCliente(idCliente);
        } catch (SQLException e) {
            throw new DatosInvalidosException("Error al obtener las reservas del cliente: " + e.getMessage());
        }
    }

    public List<Reserva> obtenerReservasPorHotel(int idHotelConsulta) throws DatosInvalidosException {
        try {
            return reservaDAO.obtenerReservasPorHotel(idHotelConsulta);
        } catch (SQLException e) {
            throw new DatosInvalidosException("Error al obtener las reservas del hotel: " + e.getMessage());
        }
    }

    public List<Reserva> obtenerReservasPorFechaEntrada(LocalDate fechaEntrada) throws DatosInvalidosException {
        if (fechaEntrada == null) {
            throw new DatosInvalidosException("La fecha de entrada es obligatoria.");
        }
        try {
            return reservaDAO.obtenerReservasPorFechaEntrada(fechaEntrada);
        } catch (SQLException e) {
            throw new DatosInvalidosException("Error al buscar reservas por fecha de entrada: " + e.getMessage());
        }
    }

    public List<Reserva> obtenerReservasPorFechaSalida(LocalDate fechaSalida) throws DatosInvalidosException {
        if (fechaSalida == null) {
            throw new DatosInvalidosException("La fecha de salida es obligatoria.");
        }
        try {
            return reservaDAO.obtenerReservasPorFechaSalida(fechaSalida);
        } catch (SQLException e) {
            throw new DatosInvalidosException("Error al buscar reservas por fecha de salida: " + e.getMessage());
        }
    }

    public void actualizarReserva(Reserva reserva) throws DatosInvalidosException {
        validarReserva(reserva);
        try {
            reservaDAO.actualizarReserva(reserva);
        } catch (SQLException e) {
            throw new DatosInvalidosException("No se pudo actualizar la reserva: " + e.getMessage());
        }
    }

    public void eliminarReserva(int idReserva) throws DatosInvalidosException {
        try {
            reservaDAO.eliminarReserva(idReserva);
        } catch (SQLException e) {
            throw new DatosInvalidosException("No se pudo eliminar la reserva: " + e.getMessage());
        }
    }

    private void validarReserva(Reserva reserva) throws DatosInvalidosException {
        if (reserva.getFechaEntrada() == null || reserva.getFechaSalida() == null) {
            throw new DatosInvalidosException("Las fechas de entrada y salida son obligatorias.");
        }
        if (!reserva.getFechaSalida().isAfter(reserva.getFechaEntrada())) {
            throw new DatosInvalidosException("La fecha de salida debe ser posterior a la fecha de entrada.");
        }
        if (reserva.getNoches() <= 0) {
            throw new DatosInvalidosException("El número de noches debe ser mayor que cero.");
        }
        if (reserva.getCantidadPersonas() <= 0) {
            throw new DatosInvalidosException("El número de personas debe ser mayor que cero.");
        }
        if (reserva.getHabitaciones() == null || reserva.getHabitaciones().isEmpty()) {
            throw new DatosInvalidosException("La reserva debe incluir al menos una habitación.");
        }

        int capacidadTotal = 0;
        for (Habitacion h : reserva.getHabitaciones()) {
            capacidadTotal += h.getCapacidad();
        }
        if (reserva.getCantidadPersonas() > capacidadTotal) {
            throw new DatosInvalidosException("El número de personas (" + reserva.getCantidadPersonas()
                    + ") supera la capacidad de las habitaciones seleccionadas (" + capacidadTotal + ").");
        }
    }
}