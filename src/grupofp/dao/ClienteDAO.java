package grupofp.dao;

import grupofp.modelo.Cliente;
import java.sql.SQLException;
import java.util.List;

public interface ClienteDAO {
    void crearCliente(Cliente cliente) throws SQLException;

    Cliente obtenerCliente(int id) throws SQLException;

    Cliente obtenerClientePorEmail(String email) throws SQLException;

    List<Cliente> obtenerTodosLosClientes() throws SQLException;

    void actualizarCliente(Cliente cliente) throws SQLException;

    void eliminarCliente(int id) throws SQLException;

    List<Cliente> buscarPorApellidoStoredProcedure(String apellido) throws SQLException;
}