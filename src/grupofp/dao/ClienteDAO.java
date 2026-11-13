package grupofp.dao;

import grupofp.modelo.Cliente;

import java.util.List;

public interface ClienteDAO {
    void crearCliente(Cliente cliente);

    Cliente obtenerCliente(int id);
    List<Cliente> obtenerTodosLosClientes();

    void actualizarCliente(Cliente cliente);

    void eliminarCliente(int id);

    void buscarPorApellidoStoredProcedure(String apellido);
}
