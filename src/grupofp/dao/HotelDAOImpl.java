package grupofp.dao;

import grupofp.modelo.Hotel;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación MySQL del DAO de hoteles (Persona 5).
 * - Alta mediante el procedimiento almacenado InsertarHotel (devuelve el ID generado).
 * - Búsqueda por ciudad mediante el procedimiento almacenado BuscarHotelesPorCiudad.
 * - Baja dentro de una transacción: se borran sus habitaciones y después el hotel.
 * Todas las consultas usan PreparedStatement/CallableStatement para evitar SQL Injection.
 */
public class HotelDAOImpl implements HotelDAO {

    @Override
    public void crearHotel(Hotel hotel) throws SQLException {
        String sql = "{CALL InsertarHotel(?, ?)}";
        Connection conn = ConexionDB.getConexion();

        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, hotel.getNombre());
            cstmt.setString(2, hotel.getCiudad());
            try (ResultSet rs = cstmt.executeQuery()) {
                if (rs.next()) {
                    hotel.setIdHotel(rs.getInt("id_hotel"));
                } else {
                    throw new SQLException("No se pudo recuperar el ID generado para el hotel.");
                }
            }
        }
    }

    @Override
    public Hotel obtenerHotel(int idHotel) throws SQLException {
        String sql = "SELECT id_hotel, nombre, ciudad FROM hoteles WHERE id_hotel = ?";
        Connection conn = ConexionDB.getConexion();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idHotel);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return construirHotel(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Hotel> obtenerTodosLosHoteles() throws SQLException {
        String sql = "SELECT id_hotel, nombre, ciudad FROM hoteles ORDER BY id_hotel";
        Connection conn = ConexionDB.getConexion();
        List<Hotel> lista = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(construirHotel(rs));
            }
        }
        return lista;
    }

    @Override
    public void actualizarHotel(Hotel hotel) throws SQLException {
        String sql = "UPDATE hoteles SET nombre = ?, ciudad = ? WHERE id_hotel = ?";
        Connection conn = ConexionDB.getConexion();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hotel.getNombre());
            pstmt.setString(2, hotel.getCiudad());
            pstmt.setInt(3, hotel.getIdHotel());
            if (pstmt.executeUpdate() == 0) {
                throw new SQLException("No existe ningún hotel con ID " + hotel.getIdHotel() + ".");
            }
        }
    }

    @Override
    public void eliminarHotel(int idHotel) throws SQLException {
        String sqlReservas = "SELECT COUNT(*) FROM reservas WHERE id_hotel = ?";
        String sqlHabitaciones = "DELETE FROM habitaciones WHERE id_hotel = ?";
        String sqlHotel = "DELETE FROM hoteles WHERE id_hotel = ?";

        Connection conn = ConexionDB.getConexion();
        try {
            conn.setAutoCommit(false);

            // 1. No se permite borrar un hotel que tenga reservas registradas.
            try (PreparedStatement pstmt = conn.prepareStatement(sqlReservas)) {
                pstmt.setInt(1, idHotel);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new SQLException("El hotel tiene " + rs.getInt(1)
                                + " reserva(s) registradas. Elimínalas antes de borrar el hotel.");
                    }
                }
            }

            // 2. Se eliminan sus habitaciones.
            try (PreparedStatement pstmt = conn.prepareStatement(sqlHabitaciones)) {
                pstmt.setInt(1, idHotel);
                pstmt.executeUpdate();
            }

            // 3. Se elimina el hotel. Si no existe, se deshace todo.
            try (PreparedStatement pstmt = conn.prepareStatement(sqlHotel)) {
                pstmt.setInt(1, idHotel);
                if (pstmt.executeUpdate() == 0) {
                    throw new SQLException("No existe ningún hotel con ID " + idHotel + ".");
                }
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @Override
    public List<Hotel> buscarPorCiudadStoredProcedure(String ciudad) throws SQLException {
        String sql = "{CALL BuscarHotelesPorCiudad(?)}";
        Connection conn = ConexionDB.getConexion();
        List<Hotel> lista = new ArrayList<>();

        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, ciudad);
            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(construirHotel(rs));
                }
            }
        }
        return lista;
    }

    private Hotel construirHotel(ResultSet rs) throws SQLException {
        return new Hotel(rs.getInt("id_hotel"), rs.getString("nombre"), rs.getString("ciudad"));
    }
}
