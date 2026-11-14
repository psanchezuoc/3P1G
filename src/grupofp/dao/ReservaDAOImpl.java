package grupofp.dao;

import grupofp.modelo.Cliente;
import grupofp.modelo.Habitacion;
import grupofp.modelo.HabitacionEstandar;
import grupofp.modelo.HabitacionSuperior;
import grupofp.modelo.Hotel;
import grupofp.modelo.Reserva;
import grupofp.modelo.Suite;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAOImpl implements ReservaDAO {

    private static final String SELECT_BASE =
            "SELECT r.id_reserva, r.fecha_entrada, r.fecha_salida, r.noches, r.cantidad_personas, r.importe_total, "
                    + "c.id_cliente, c.nombre AS nombre_cliente, c.apellido, c.email, "
                    + "h.id_hotel, h.nombre AS nombre_hotel, h.ciudad "
                    + "FROM reservas r "
                    + "INNER JOIN clientes c ON r.id_cliente = c.id_cliente "
                    + "INNER JOIN hoteles h ON r.id_hotel = h.id_hotel ";

    @Override
    public void crearReserva(Reserva reserva) throws SQLException {
        String sqlReserva = "INSERT INTO reservas (id_cliente, id_hotel, fecha_entrada, fecha_salida, noches, cantidad_personas, importe_total) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO reserva_habitacion (id_reserva, id_habitacion) VALUES (?, ?)";

        Connection conn = ConexionDB.getConexion();
        try {
            conn.setAutoCommit(false);

            for (Habitacion hab : reserva.getHabitaciones()) {
                int idHabitacion = obtenerIdHabitacion(conn, hab.getNumero(), reserva.getHotel().getIdHotel());
                if (estaHabitacionOcupada(conn, idHabitacion, reserva.getFechaEntrada(), reserva.getFechaSalida(), 0)) {
                    throw new SQLException("La habitación " + hab.getNumero() + " ya está ocupada entre "
                            + reserva.getFechaEntrada() + " y " + reserva.getFechaSalida() + ".");
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sqlReserva, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, reserva.getCliente().getIdCliente());
                pstmt.setInt(2, reserva.getHotel().getIdHotel());
                pstmt.setDate(3, Date.valueOf(reserva.getFechaEntrada()));
                pstmt.setDate(4, Date.valueOf(reserva.getFechaSalida()));
                pstmt.setInt(5, reserva.getNoches());
                pstmt.setInt(6, reserva.getCantidadPersonas());
                pstmt.setDouble(7, reserva.calcularImporte());
                pstmt.executeUpdate();

                try (ResultSet rsKeys = pstmt.getGeneratedKeys()) {
                    if (rsKeys.next()) {
                        reserva.setIdReserva(rsKeys.getInt(1));
                    } else {
                        throw new SQLException("No se pudo recuperar el ID generado para la reserva.");
                    }
                }
            }

            try (PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle)) {
                for (Habitacion hab : reserva.getHabitaciones()) {
                    int idHabitacion = obtenerIdHabitacion(conn, hab.getNumero(), reserva.getHotel().getIdHotel());
                    pstmtDetalle.setInt(1, reserva.getIdReserva());
                    pstmtDetalle.setInt(2, idHabitacion);
                    pstmtDetalle.executeUpdate();
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
    public Reserva obtenerReserva(int idReserva) throws SQLException {
        String sql = SELECT_BASE + "WHERE r.id_reserva = ?";
        Connection conn = ConexionDB.getConexion();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idReserva);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return construirReserva(rs, conn);
                }
            }
        }
        return null;
    }

    @Override
    public List<Reserva> obtenerTodasLasReservas() throws SQLException {
        String sql = SELECT_BASE + "ORDER BY r.fecha_entrada, r.id_reserva";
        return ejecutarConsultaLista(sql, null);
    }

    @Override
    public List<Reserva> obtenerReservasPorCliente(int idCliente) throws SQLException {
        String sql = SELECT_BASE + "WHERE r.id_cliente = ? ORDER BY r.fecha_entrada, r.id_reserva";
        return ejecutarConsultaLista(sql, pstmt -> pstmt.setInt(1, idCliente));
    }

    @Override
    public List<Reserva> obtenerReservasPorHotel(int idHotel) throws SQLException {
        String sql = SELECT_BASE + "WHERE r.id_hotel = ? ORDER BY r.fecha_entrada, r.id_reserva";
        return ejecutarConsultaLista(sql, pstmt -> pstmt.setInt(1, idHotel));
    }

    @Override
    public List<Reserva> obtenerReservasPorFechaEntrada(LocalDate fechaEntrada) throws SQLException {
        String sql = SELECT_BASE + "WHERE r.fecha_entrada = ? ORDER BY r.id_reserva";
        return ejecutarConsultaLista(sql, pstmt -> pstmt.setDate(1, Date.valueOf(fechaEntrada)));
    }

    @Override
    public List<Reserva> obtenerReservasPorFechaSalida(LocalDate fechaSalida) throws SQLException {
        String sql = SELECT_BASE + "WHERE r.fecha_salida = ? ORDER BY r.id_reserva";
        return ejecutarConsultaLista(sql, pstmt -> pstmt.setDate(1, Date.valueOf(fechaSalida)));
    }

    @Override
    public void actualizarReserva(Reserva reserva) throws SQLException {
        String sqlUpdate = "UPDATE reservas SET fecha_entrada = ?, fecha_salida = ?, noches = ?, "
                + "cantidad_personas = ?, importe_total = ? WHERE id_reserva = ?";
        String sqlDeleteDetalle = "DELETE FROM reserva_habitacion WHERE id_reserva = ?";
        String sqlInsertDetalle = "INSERT INTO reserva_habitacion (id_reserva, id_habitacion) VALUES (?, ?)";

        Connection conn = ConexionDB.getConexion();
        try {
            conn.setAutoCommit(false);

            for (Habitacion hab : reserva.getHabitaciones()) {
                int idHabitacion = obtenerIdHabitacion(conn, hab.getNumero(), reserva.getHotel().getIdHotel());
                if (estaHabitacionOcupada(conn, idHabitacion, reserva.getFechaEntrada(), reserva.getFechaSalida(), reserva.getIdReserva())) {
                    throw new SQLException("La habitación " + hab.getNumero() + " ya está ocupada entre "
                            + reserva.getFechaEntrada() + " y " + reserva.getFechaSalida() + ".");
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
                pstmt.setDate(1, Date.valueOf(reserva.getFechaEntrada()));
                pstmt.setDate(2, Date.valueOf(reserva.getFechaSalida()));
                pstmt.setInt(3, reserva.getNoches());
                pstmt.setInt(4, reserva.getCantidadPersonas());
                pstmt.setDouble(5, reserva.calcularImporte());
                pstmt.setInt(6, reserva.getIdReserva());
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmtDel = conn.prepareStatement(sqlDeleteDetalle)) {
                pstmtDel.setInt(1, reserva.getIdReserva());
                pstmtDel.executeUpdate();
            }

            try (PreparedStatement pstmtIns = conn.prepareStatement(sqlInsertDetalle)) {
                for (Habitacion hab : reserva.getHabitaciones()) {
                    int idHabitacion = obtenerIdHabitacion(conn, hab.getNumero(), reserva.getHotel().getIdHotel());
                    pstmtIns.setInt(1, reserva.getIdReserva());
                    pstmtIns.setInt(2, idHabitacion);
                    pstmtIns.executeUpdate();
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
    public void eliminarReserva(int idReserva) throws SQLException {
        String sql = "DELETE FROM reservas WHERE id_reserva = ?";
        Connection conn = ConexionDB.getConexion();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idReserva);
            pstmt.executeUpdate();
        }
    }

    @FunctionalInterface
    private interface ParametroSetter {
        void set(PreparedStatement pstmt) throws SQLException;
    }

    private List<Reserva> ejecutarConsultaLista(String sql, ParametroSetter setter) throws SQLException {
        Connection conn = ConexionDB.getConexion();
        List<Reserva> lista = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (setter != null) {
                setter.set(pstmt);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(construirReserva(rs, conn));
                }
            }
        }
        return lista;
    }

    private int obtenerIdHabitacion(Connection conn, String numero, int idHotel) throws SQLException {
        String sql = "SELECT id_habitacion FROM habitaciones WHERE numero = ? AND id_hotel = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, numero);
            pstmt.setInt(2, idHotel);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_habitacion");
                }
            }
        }
        throw new SQLException("No se encontró la habitación " + numero + " en el hotel " + idHotel + ".");
    }

    private boolean estaHabitacionOcupada(Connection conn, int idHabitacion, LocalDate entrada, LocalDate salida, int idReservaExcluir) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reserva_habitacion rh "
                + "INNER JOIN reservas r ON rh.id_reserva = r.id_reserva "
                + "WHERE rh.id_habitacion = ? AND r.id_reserva <> ? "
                + "AND r.fecha_entrada < ? AND r.fecha_salida > ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idHabitacion);
            pstmt.setInt(2, idReservaExcluir);
            pstmt.setDate(3, Date.valueOf(salida));
            pstmt.setDate(4, Date.valueOf(entrada));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private Reserva construirReserva(ResultSet rs, Connection conn) throws SQLException {
        int idReserva = rs.getInt("id_reserva");
        LocalDate fechaEntrada = rs.getDate("fecha_entrada").toLocalDate();
        LocalDate fechaSalida = rs.getDate("fecha_salida").toLocalDate();
        int noches = rs.getInt("noches");
        int cantidadPersonas = rs.getInt("cantidad_personas");
        double importeTotal = rs.getDouble("importe_total");

        Cliente cliente = new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("nombre_cliente"),
                rs.getString("apellido"),
                rs.getString("email")
        );

        Hotel hotel = new Hotel(
                rs.getInt("id_hotel"),
                rs.getString("nombre_hotel"),
                rs.getString("ciudad")
        );

        List<Habitacion> habitaciones = obtenerHabitacionesDeReserva(idReserva, conn);
        double precioNoche = (noches > 0 && !habitaciones.isEmpty())
                ? importeTotal / (noches * habitaciones.size())
                : 0.0;

        return new Reserva(idReserva, fechaEntrada, fechaSalida, cantidadPersonas,
                precioNoche, habitaciones.size(), cliente, hotel, habitaciones);
    }

    private List<Habitacion> obtenerHabitacionesDeReserva(int idReserva, Connection conn) throws SQLException {
        String sql = "SELECT h.* FROM habitaciones h "
                + "INNER JOIN reserva_habitacion rh ON h.id_habitacion = rh.id_habitacion "
                + "WHERE rh.id_reserva = ?";
        List<Habitacion> habitaciones = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idReserva);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String numero = rs.getString("numero");
                    int capacidad = rs.getInt("capacidad");
                    double precio = rs.getDouble("precio_noche");
                    String categoria = rs.getString("categoria");
                    String detalle = rs.getString("detalle_extra");

                    Habitacion h = switch (categoria) {
                        case "Superior" -> new HabitacionSuperior(numero, capacidad, precio,
                                "Con balcón".equalsIgnoreCase(detalle) || Boolean.parseBoolean(detalle));
                        case "Suite" -> new Suite(numero, capacidad, precio,
                                "Con jacuzzi".equalsIgnoreCase(detalle) || Boolean.parseBoolean(detalle));
                        default -> new HabitacionEstandar(numero, capacidad, precio, detalle);
                    };
                    habitaciones.add(h);
                }
            }
        }
        return habitaciones;
    }
}