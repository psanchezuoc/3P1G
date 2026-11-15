package grupofp.dao;

import grupofp.modelo.Hotel;
import java.sql.SQLException;
import java.util.List;

public interface HotelDAO {
    void crearHotel(Hotel hotel) throws SQLException;

    Hotel obtenerHotel(int idHotel) throws SQLException;

    List<Hotel> obtenerTodosLosHoteles() throws SQLException;

    void actualizarHotel(Hotel hotel) throws SQLException;

    void eliminarHotel(int idHotel) throws SQLException;

    List<Hotel> buscarPorCiudadStoredProcedure(String ciudad) throws SQLException;
}
