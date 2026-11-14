package grupofp.dao;

import grupofp.modelo.Reserva;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface ReservaDAO {
    void crearReserva(Reserva reserva) throws SQLException;

    Reserva obtenerReserva(int idReserva) throws SQLException;

    List<Reserva> obtenerTodasLasReservas() throws SQLException;

    List<Reserva> obtenerReservasPorCliente(int idCliente) throws SQLException;

    List<Reserva> obtenerReservasPorHotel(int idHotel) throws SQLException;

    List<Reserva> obtenerReservasPorFechaEntrada(LocalDate fechaEntrada) throws SQLException;

    List<Reserva> obtenerReservasPorFechaSalida(LocalDate fechaSalida) throws SQLException;

    void actualizarReserva(Reserva reserva) throws SQLException;

    void eliminarReserva(int idReserva) throws SQLException;
}