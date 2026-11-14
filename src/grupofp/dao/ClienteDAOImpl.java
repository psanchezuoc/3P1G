package grupofp.dao;

import grupofp.modelo.Cliente;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public void crearCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (nombre, apellido, email) VALUES (?, ?, ?)";
        Connection conn = ConexionDB.getConexion();

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellido());
            pstmt.setString(3, cliente.getEmail());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setIdCliente(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Cliente obtenerCliente(int id) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";
        Connection conn = ConexionDB.getConexion();
        Cliente cliente = null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int idCliente = rs.getInt("id_cliente");
                    String nombre = rs.getString("nombre");
                    String apellido = rs.getString("apellido");
                    String email = rs.getString("email");
                    cliente = new Cliente(idCliente, nombre, apellido, email);
                }
            }
        }
        return cliente;
    }

    @Override
    public List<Cliente> obtenerTodosLosClientes() throws SQLException {
        String sql = "SELECT * FROM clientes";
        Connection conn = ConexionDB.getConexion();
        List<Cliente> lista = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int idCliente = rs.getInt("id_cliente");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String email = rs.getString("email");
                lista.add(new Cliente(idCliente, nombre, apellido, email));
            }
        }
        return lista;
    }

    @Override
    public void actualizarCliente(Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET nombre = ?, apellido = ?, email = ? WHERE id_cliente = ?";
        Connection conn = ConexionDB.getConexion();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellido());
            pstmt.setString(3, cliente.getEmail());
            pstmt.setInt(4, cliente.getIdCliente());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void eliminarCliente(int id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";
        Connection conn = ConexionDB.getConexion();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Cliente> buscarPorApellidoStoredProcedure(String apellido) throws SQLException {
        String sql = "{CALL BuscarClientePorApellido(?)}";
        Connection conn = ConexionDB.getConexion();
        List<Cliente> lista = new ArrayList<>();

        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, apellido);

            // El procedimiento almacenado debe devolver todas las columnas de la tabla clientes
            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    int idCliente = rs.getInt("id_cliente");
                    String nombre = rs.getString("nombre");
                    String apellidoRes = rs.getString("apellido");
                    String email = rs.getString("email");
                    lista.add(new Cliente(idCliente, nombre, apellidoRes, email));
                }
            }
        }
        return lista;
    }

    @Override
    public Cliente obtenerClientePorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE email = ?";
        Connection conn = ConexionDB.getConexion();
        Cliente cliente = null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int idCliente = rs.getInt("id_cliente");
                    String nombre = rs.getString("nombre");
                    String apellido = rs.getString("apellido");
                    String emailRes = rs.getString("email");
                    cliente = new Cliente(idCliente, nombre, apellido, emailRes);
                }
            }
        }
        return cliente;
    }
}