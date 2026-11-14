package grupofp.dao;

import grupofp.modelo.Habitacion;
import grupofp.modelo.HabitacionEstandar;
import grupofp.modelo.HabitacionSuperior;
import grupofp.modelo.Suite;

import java.util.ArrayList;
import java.util.List;

public class HabitacionDAOImpl implements HabitacionDAO {

    @Override
    public void crearHabitacion(Habitacion habitacion, int idHotel) {
        String sql = "INSERT INTO habitaciones (numero, capacidad, precio_noche, categoria, detalle_extra, id_hotel) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection conn = ConexionDB.getConexion();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, habitacion.getNumero());
            pstmt.setInt(2, habitacion.getCapacidad());
            pstmt.setDouble(3, habitacion.getPrecioNoche());
            pstmt.setString(4, habitacion.getCategoria());
            pstmt.setString(5, obtenerDetalleExtra(habitacion));
            pstmt.setInt(6, idHotel);

            pstmt.executeUpdate();
            System.out.println("Habitación creada con éxito en la BBDD");
        } catch (java.sql.SQLException e) {
            System.err.println("Error al crear la habitación: " + e.getMessage());
        }
    }

    @Override
    public Habitacion obtenerHabitacion(String numero) {
        String sql = "SELECT * FROM habitaciones WHERE numero = ?";
        Habitacion habitacion = null;

        try (java.sql.Connection conn = ConexionDB.getConexion();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, numero);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    habitacion = construirHabitacion(rs);
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al obtener la habitación: " + e.getMessage());
        }
        return habitacion;
    }

    @Override
    public List<Habitacion> obtenerTodasLasHabitaciones() {
        String sql = "SELECT * FROM habitaciones";
        List<Habitacion> lista = new ArrayList<>();

        try (java.sql.Connection conn = ConexionDB.getConexion();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                lista.add(construirHabitacion(rs));
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al obtener habitaciones: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizarHabitacion(Habitacion habitacion) {
        String sql = "UPDATE habitaciones SET capacidad = ?, precio_noche = ?, categoria = ?, detalle_extra = ? "
                + "WHERE numero = ?";

        try (java.sql.Connection conn = ConexionDB.getConexion();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, habitacion.getCapacidad());
            pstmt.setDouble(2, habitacion.getPrecioNoche());
            pstmt.setString(3, habitacion.getCategoria());
            pstmt.setString(4, obtenerDetalleExtra(habitacion));
            pstmt.setString(5, habitacion.getNumero());

            pstmt.executeUpdate();
            System.out.println("Habitación actualizada.");
        } catch (java.sql.SQLException e) {
            System.err.println("Error al actualizar la habitación: " + e.getMessage());
        }
    }

    @Override
    public void eliminarHabitacion(String numero) {
        String sql = "DELETE FROM habitaciones WHERE numero = ?";

        try (java.sql.Connection conn = ConexionDB.getConexion();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, numero);
            pstmt.executeUpdate();
            System.out.println("Habitación eliminada.");
        } catch (java.sql.SQLException e) {
            System.err.println("Error al eliminar la habitación: " + e.getMessage());
        }
    }

    @Override
    public List<Habitacion> buscarPorCategoriaStoredProcedure(String categoria) {
        String sql = "{CALL BuscarHabitacionesPorCategoria(?)}";
        List<Habitacion> lista = new ArrayList<>();

        try (java.sql.Connection conn = ConexionDB.getConexion();
             java.sql.CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, categoria);
            try (java.sql.ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(construirHabitacion(rs));
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error en el procedimiento almacenado: " + e.getMessage());
        }
        return lista;
    }

    // Convierte las propiedades propias de cada tipo de habitación en el texto de la columna detalle_extra.
    private String obtenerDetalleExtra(Habitacion habitacion) {
        if (habitacion instanceof HabitacionEstandar) {
            return ((HabitacionEstandar) habitacion).getTipoCama();
        } else if (habitacion instanceof HabitacionSuperior) {
            return String.valueOf(((HabitacionSuperior) habitacion).isTieneBalcon());
        } else if (habitacion instanceof Suite) {
            return String.valueOf(((Suite) habitacion).isTieneJacuzzi());
        }
        return null;
    }

    // Crea el objeto del tipo correcto (Suite, Superior o Estándar) a partir de una fila de la tabla.
    private Habitacion construirHabitacion(java.sql.ResultSet rs) throws java.sql.SQLException {
        String numero = rs.getString("numero");
        int capacidad = rs.getInt("capacidad");
        double precio = rs.getDouble("precio_noche");
        String categoria = rs.getString("categoria");
        String detalle = rs.getString("detalle_extra");

        if ("Suite".equalsIgnoreCase(categoria)) {
            return new Suite(numero, capacidad, precio, Boolean.parseBoolean(detalle));
        } else if ("Superior".equalsIgnoreCase(categoria)) {
            return new HabitacionSuperior(numero, capacidad, precio, Boolean.parseBoolean(detalle));
        }
        return new HabitacionEstandar(numero, capacidad, precio, detalle);
    }
}
