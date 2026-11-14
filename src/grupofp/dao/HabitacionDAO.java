package grupofp.dao;

import grupofp.modelo.Habitacion;

import java.util.List;

public interface HabitacionDAO {
    void crearHabitacion(Habitacion habitacion, int idHotel);

    Habitacion obtenerHabitacion(String numero);

    List<Habitacion> obtenerTodasLasHabitaciones();

    void actualizarHabitacion(Habitacion habitacion);

    void eliminarHabitacion(String numero);

    List<Habitacion> buscarPorCategoriaStoredProcedure(String categoria);
}
