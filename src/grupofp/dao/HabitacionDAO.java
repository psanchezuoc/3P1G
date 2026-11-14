package grupofp.dao;

import grupofp.modelo.Habitacion;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface HabitacionDAO {
    void crearHabitacion(Habitacion habitacion, int idHotel) throws SQLException;

    Habitacion obtenerHabitacion(String numero, int idHotel) throws SQLException;

    List<Habitacion> obtenerTodasLasHabitaciones() throws SQLException;

    List<Habitacion> obtenerHabitacionesPorHotel(int idHotel) throws SQLException;

    List<Habitacion> obtenerHabitacionesDisponibles(int idHotel, LocalDate fechaEntrada, LocalDate fechaSalida) throws SQLException;

    List<Habitacion> obtenerHabitacionesPorCapacidad(int idHotel, int cantidadPersonas) throws SQLException;

    void actualizarHabitacion(Habitacion habitacion, int idHotel) throws SQLException;

    void eliminarHabitacion(String numero, int idHotel) throws SQLException;

    List<Habitacion> buscarPorCategoriaStoredProcedure(String categoria) throws SQLException;
}