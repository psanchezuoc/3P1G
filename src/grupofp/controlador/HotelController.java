package grupofp.controlador;

import grupofp.dao.HotelDAO;
import grupofp.modelo.DatosInvalidosException;
import grupofp.modelo.Hotel;

import java.sql.SQLException;
import java.util.List;

public class HotelController {
    private final HotelDAO hotelDAO;

    public HotelController(HotelDAO hotelDAO) {
        this.hotelDAO = hotelDAO;
    }

    public void registrarHotel(Hotel hotel) throws DatosInvalidosException {
        validarHotel(hotel);
        try {
            hotelDAO.crearHotel(hotel);
        } catch (SQLException e) {
            throw new DatosInvalidosException("No se pudo registrar el hotel: " + e.getMessage());
        }
    }

    public Hotel buscarHotelPorId(int idHotel) throws DatosInvalidosException {
        try {
            Hotel hotel = hotelDAO.obtenerHotel(idHotel);
            if (hotel == null) {
                throw new DatosInvalidosException("No existe ningún hotel con ID: " + idHotel);
            }
            return hotel;
        } catch (SQLException e) {
            throw new DatosInvalidosException("Error al consultar el hotel: " + e.getMessage());
        }
    }

    public List<Hotel> obtenerTodosLosHoteles() throws DatosInvalidosException {
        try {
            return hotelDAO.obtenerTodosLosHoteles();
        } catch (SQLException e) {
            throw new DatosInvalidosException("Error al obtener el listado de hoteles: " + e.getMessage());
        }
    }

    public List<Hotel> buscarHotelesPorCiudad(String ciudad) throws DatosInvalidosException {
        if (ciudad == null || ciudad.isBlank()) {
            throw new DatosInvalidosException("La ciudad es obligatoria.");
        }
        try {
            return hotelDAO.buscarPorCiudadStoredProcedure(ciudad);
        } catch (SQLException e) {
            throw new DatosInvalidosException("Error al ejecutar la búsqueda por ciudad: " + e.getMessage());
        }
    }

    public void actualizarHotel(Hotel hotel) throws DatosInvalidosException {
        validarHotel(hotel);
        try {
            hotelDAO.actualizarHotel(hotel);
        } catch (SQLException e) {
            throw new DatosInvalidosException("No se pudo actualizar el hotel: " + e.getMessage());
        }
    }

    public void eliminarHotel(int idHotel) throws DatosInvalidosException {
        try {
            hotelDAO.eliminarHotel(idHotel);
        } catch (SQLException e) {
            throw new DatosInvalidosException("No se pudo eliminar el hotel: " + e.getMessage());
        }
    }

    private void validarHotel(Hotel hotel) throws DatosInvalidosException {
        if (hotel.getNombre() == null || hotel.getNombre().isBlank()
                || hotel.getCiudad() == null || hotel.getCiudad().isBlank()) {
            throw new DatosInvalidosException("El nombre y la ciudad del hotel son obligatorios.");
        }
        if (hotel.getNombre().length() > 100 || hotel.getCiudad().length() > 100) {
            throw new DatosInvalidosException("El nombre y la ciudad no pueden superar los 100 caracteres.");
        }
    }
}
