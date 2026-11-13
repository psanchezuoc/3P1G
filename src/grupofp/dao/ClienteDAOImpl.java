package grupofp.dao;

import grupofp.modelo.Cliente;

import java.util.List;

public class ClienteDAOImpl implements ClienteDAO{
    @Override
    public void crearCliente(Cliente cliente) {
        String sql = "INSERT INTO clientes (nombre, apellido, email) VALUES (?, ?, ?)";

        try (java.sql.Connection conn = ConexionDB.getConexion();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellido());
            pstmt.setString(3, cliente.getEmail());

            pstmt.executeUpdate();
            System.out.println("Cliente creado con éxito en la BBDD");
        } catch (java.sql.SQLException e){
            System.err.println("Error al crear el cliente "+e.getMessage());
        }
    }

    @Override
    public Cliente obtenerCliente(int id) {
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";
        Cliente cliente = null;

        try(java.sql.Connection conn = ConexionDB.getConexion();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, id);
            try(java.sql.ResultSet rs = pstmt.executeQuery()){
                if (rs.next()) {
                    int idCliente = rs.getInt("id_cliente");
                    String nombre = rs.getString("nombre");
                    String apellido = rs.getString("apellido");
                    String email = rs.getString("email");
                    cliente = new Cliente(id, nombre, apellido, email);
                }
            }
        }catch (java.sql.SQLException e){
            System.err.println("Error al obtener el cliente: "+e.getMessage());
        }
        return cliente;
    }

    @Override
    public List<Cliente> obtenerTodosLosClientes() {
        String sql = "SELECT * FROM clientes";
        List<Cliente> lista = new java.util.ArrayList<>();

        try (java.sql.Connection conn = ConexionDB.getConexion();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int idCliente = rs.getInt("id_cliente");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String email = rs.getString("email");
                Cliente c = new Cliente(idCliente, nombre, apellido, email);
                lista.add(c);
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al obtener clientes: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizarCliente(Cliente cliente) {
        String sql = "UPDATE clientes SET nombre = ?, apellido = ?, email = ? WHERE id_cliente = ?";

        try (java.sql.Connection conn = ConexionDB.getConexion();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellido());
            pstmt.setString(3, cliente.getEmail());
            pstmt.setInt(4, cliente.getIdCliente()); // Asegúrate de que este 'getId' es correcto

            pstmt.executeUpdate();
            System.out.println("Cliente actualizado.");
        } catch (java.sql.SQLException e) {
            System.err.println("Error al actualizar el cliente: " + e.getMessage());
        }
    }

    @Override
    public void eliminarCliente(int id) {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";

        try (java.sql.Connection conn = ConexionDB.getConexion();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Cliente eliminado.");
        } catch (java.sql.SQLException e) {
            System.err.println("Error al eliminar el cliente: " + e.getMessage());
        }
    }

    @Override
    public void buscarPorApellidoStoredProcedure(String apellido) {
        String sql = "{CALL BuscarClientePorApellido(?)}";

        try (java.sql.Connection conn = ConexionDB.getConexion();
             java.sql.CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, apellido);
            try (java.sql.ResultSet rs = cstmt.executeQuery()) {
                System.out.println("Resultados de la búsqueda por apellido '" + apellido + "':");
                while (rs.next()) {
                    System.out.println("- " + rs.getString("nombre") + " " + rs.getString("email"));
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error en el procedimiento almacenado: " + e.getMessage());
        }
    }
}
