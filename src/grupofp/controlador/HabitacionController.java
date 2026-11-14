package grupofp.controlador;

import grupofp.dao.HabitacionDAO;
import grupofp.modelo.DatosInvalidosException;
import grupofp.modelo.Habitacion;
import grupofp.modelo.HabitacionNoDisponibleException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class HabitacionController {
    private final HabitacionDAO habitacionDAO;
    private final int idHotel;

    public HabitacionController(HabitacionDAO habitacionDAO, int idHotel) {
        this.habitacionDAO = habitacionDAO;
        this.idHotel = idHotel;
    }

    public void registrarHabitacion(Habitacion habitacion) throws DatosInvalidosException {
        validarHabitacion(habitacion);
        try {
            habitacionDAO.crearHabitacion(habitacion, idHotel);
        } catch (SQLException e) {
            throw new DatosInvalidosException("No se pudo registrar la habitación: " + e.getMessage());
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

    public List<Habitacion> buscarHabitacionesPorCategoria(String categoria) throws DatosInvalidosException {
        try {
            return habitacionDAO.buscarPorCategoriaStoredProcedure(categoria);
        } catch (SQLException e) {
            throw new DatosInvalidosException("Error al ejecutar la búsqueda por categoría: " + e.getMessage());
        }
    }

    public List<Habitacion> obtenerTodasLasHabitaciones() throws DatosInvalidosException {
        try {
            return habitacionDAO.obtenerTodasLasHabitaciones();
        } catch (SQLException e) {
            throw new DatosInvalidosException("Error al obtener el listado de habitaciones: " + e.getMessage());
        }
    }

    public List<Habitacion> obtenerHabitacionesDelHotel() throws DatosInvalidosException {
        try {
            return habitacionDAO.obtenerHabitacionesPorHotel(idHotel);
        } catch (SQLException e) {
            throw new DatosInvalidosException("Error al obtener las habitaciones del hotel: " + e.getMessage());
        }
    }

    public List<Habitacion> obtenerHabitacionesDisponibles(LocalDate fechaEntrada, LocalDate fechaSalida) throws DatosInvalidosException {
        validarRangoFechas(fechaEntrada, fechaSalida);
        try {
            return habitacionDAO.obtenerHabitacionesDisponibles(idHotel, fechaEntrada, fechaSalida);
        } catch (SQLException e) {
            throw new DatosInvalidosException("Error al consultar habitaciones disponibles: " + e.getMessage());
        }
    }

    public List<Habitacion> obtenerHabitacionesPorCapacidad(int cantidadPersonas) throws DatosInvalidosException {
        if (cantidadPersonas <= 0) {
            throw new DatosInvalidosException("El número de personas debe ser mayor que cero.");
        }
        try {
            return habitacionDAO.obtenerHabitacionesPorCapacidad(idHotel, cantidadPersonas);
        } catch (SQLException e) {
            throw new DatosInvalidosException("Error al filtrar habitaciones por capacidad: " + e.getMessage());
        }
    }

    public void actualizarHabitacion(Habitacion habitacion) throws DatosInvalidosException {
        validarHabitacion(habitacion);
        try {
            habitacionDAO.actualizarHabitacion(habitacion, idHotel);
        } catch (SQLException e) {
            throw new DatosInvalidosException("No se pudo actualizar la habitación: " + e.getMessage());
        }
    }

    public void eliminarHabitacion(String numero) throws DatosInvalidosException {
        try {
            habitacionDAO.eliminarHabitacion(numero, idHotel);
        } catch (SQLException e) {
            throw new DatosInvalidosException("No se pudo eliminar la habitación (comprueba si tiene reservas asociadas): " + e.getMessage());
        }
    }

    private void validarHabitacion(Habitacion habitacion) throws DatosInvalidosException {
        if (habitacion.getNumero() == null || habitacion.getNumero().isBlank()) {
            throw new DatosInvalidosException("El número de habitación es obligatorio.");
        }
        if (habitacion.getCapacidad() <= 0 || habitacion.getPrecioNoche() <= 0) {
            throw new DatosInvalidosException("La capacidad y el precio por noche deben ser mayores que cero.");
        }
    }

    private void validarRangoFechas(LocalDate fechaEntrada, LocalDate fechaSalida) throws DatosInvalidosException {
        if (fechaEntrada == null || fechaSalida == null) {
            throw new DatosInvalidosException("Las fechas de entrada y salida son obligatorias.");
        }
        if (!fechaSalida.isAfter(fechaEntrada)) {
            throw new DatosInvalidosException("La fecha de salida debe ser posterior a la fecha de entrada.");
        }
    }
}