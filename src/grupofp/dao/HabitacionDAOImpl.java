package grupofp.dao;

import grupofp.modelo.Habitacion;
import grupofp.modelo.HabitacionEstandar;
import grupofp.modelo.HabitacionSuperior;
import grupofp.modelo.Suite;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HabitacionDAOImpl implements HabitacionDAO {

    @Override
    public void crearHabitacion(Habitacion habitacion, int idHotel) throws SQLException {
        String sql = "INSERT INTO habitaciones (id_hotel, numero, capacidad, precio_noche, categoria, detalle_extra) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = ConexionDB.getConexion();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idHotel);
            pstmt.setString(2, habitacion.getNumero());
            pstmt.setInt(3, habitacion.getCapacidad());
            pstmt.setDouble(4, habitacion.getPrecioNoche());
            pstmt.setString(5, habitacion.getCategoria());
            pstmt.setString(6, obtenerDetalleExtra(habitacion));
            pstmt.executeUpdate();
        }
    }

    @Override
    public Habitacion obtenerHabitacion(String numero, int idHotel) throws SQLException {
        String sql = "SELECT * FROM habitaciones WHERE numero = ? AND id_hotel = ?";
        Connection conn = ConexionDB.getConexion();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, numero);
            pstmt.setInt(2, idHotel);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return construirHabitacion(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Habitacion> obtenerTodasLasHabitaciones() throws SQLException {
        String sql = "SELECT * FROM habitaciones ORDER BY id_hotel, numero";
        Connection conn = ConexionDB.getConexion();
        List<Habitacion> lista = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                lista.add(construirHabitacion(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Habitacion> obtenerHabitacionesPorHotel(int idHotel) throws SQLException {
        String sql = "SELECT * FROM habitaciones WHERE id_hotel = ? ORDER BY numero";
        Connection conn = ConexionDB.getConexion();
        List<Habitacion> lista = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idHotel);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(construirHabitacion(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public List<Habitacion> obtenerHabitacionesDisponibles(int idHotel, LocalDate fechaEntrada, LocalDate fechaSalida) throws SQLException {
        String sql = "SELECT * FROM habitaciones h "
                + "WHERE h.id_hotel = ? "
                + "AND h.id_habitacion NOT IN ( "
                + "    SELECT rh.id_habitacion "
                + "    FROM reserva_habitacion rh "
                + "    INNER JOIN reservas r ON rh.id_reserva = r.id_reserva "
                + "    WHERE r.fecha_entrada < ? AND r.fecha_salida > ? "
                + ") ORDER BY h.numero";

        Connection conn = ConexionDB.getConexion();
        List<Habitacion> lista = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idHotel);
            pstmt.setDate(2, Date.valueOf(fechaSalida));
            pstmt.setDate(3, Date.valueOf(fechaEntrada));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(construirHabitacion(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public List<Habitacion> obtenerHabitacionesPorCapacidad(int idHotel, int cantidadPersonas) throws SQLException {
        String sql = "SELECT * FROM habitaciones WHERE id_hotel = ? AND capacidad >= ? ORDER BY capacidad, numero";
        Connection conn = ConexionDB.getConexion();
        List<Habitacion> lista = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idHotel);
            pstmt.setInt(2, cantidadPersonas);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(construirHabitacion(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public void actualizarHabitacion(Habitacion habitacion, int idHotel) throws SQLException {
        String sql = "UPDATE habitaciones SET capacidad = ?, precio_noche = ?, categoria = ?, detalle_extra = ? "
                + "WHERE numero = ? AND id_hotel = ?";
        Connection conn = ConexionDB.getConexion();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, habitacion.getCapacidad());
            pstmt.setDouble(2, habitacion.getPrecioNoche());
            pstmt.setString(3, habitacion.getCategoria());
            pstmt.setString(4, obtenerDetalleExtra(habitacion));
            pstmt.setString(5, habitacion.getNumero());
            pstmt.setInt(6, idHotel);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void eliminarHabitacion(String numero, int idHotel) throws SQLException {
        String sql = "DELETE FROM habitaciones WHERE numero = ? AND id_hotel = ?";
        Connection conn = ConexionDB.getConexion();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, numero);
            pstmt.setInt(2, idHotel);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Habitacion> buscarPorCategoriaStoredProcedure(String categoria) throws SQLException {
        String sql = "{CALL BuscarHabitacionesPorCategoria(?)}";
        Connection conn = ConexionDB.getConexion();
        List<Habitacion> lista = new ArrayList<>();

        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, categoria);

            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(construirHabitacion(rs));
                }
            }
        }
        return lista;
    }

    private String obtenerDetalleExtra(Habitacion habitacion) {
        if (habitacion instanceof HabitacionEstandar estandar) {
            return estandar.getTipoCama();
        } else if (habitacion instanceof HabitacionSuperior superior) {
            return superior.isTieneBalcon() ? "Con balcón" : "Sin balcón";
        } else if (habitacion instanceof Suite suite) {
            return suite.isTieneJacuzzi() ? "Con jacuzzi" : "Sin jacuzzi";
        }
        return "";
    }

    private Habitacion construirHabitacion(ResultSet rs) throws SQLException {
        String numero = rs.getString("numero");
        int capacidad = rs.getInt("capacidad");
        double precioNoche = rs.getDouble("precio_noche");
        String categoria = rs.getString("categoria");
        String detalleExtra = rs.getString("detalle_extra");

        return switch (categoria) {
            case "Superior" -> new HabitacionSuperior(numero, capacidad, precioNoche,
                    "Con balcón".equalsIgnoreCase(detalleExtra) || Boolean.parseBoolean(detalleExtra));
            case "Suite" -> new Suite(numero, capacidad, precioNoche,
                    "Con jacuzzi".equalsIgnoreCase(detalleExtra) || Boolean.parseBoolean(detalleExtra));
            default -> new HabitacionEstandar(numero, capacidad, precioNoche, detalleExtra);
        };
    }
}