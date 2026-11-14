package grupofp.controlador;

import grupofp.dao.ClienteDAO;
import grupofp.modelo.Cliente;
import grupofp.modelo.ClienteNoEncontradoException;
import grupofp.modelo.DatosInvalidosException;

import java.sql.SQLException;
import java.util.List;

public class ClienteController {
    private final ClienteDAO clienteDAO;

    public ClienteController(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public void registrarCliente(Cliente cliente) throws DatosInvalidosException {
        if (cliente.getNombre() == null || cliente.getNombre().isBlank()
                || cliente.getApellido() == null || cliente.getApellido().isBlank()
                || cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            throw new DatosInvalidosException("Nombre, apellido y email son obligatorios.");
        }

        try {
            clienteDAO.crearCliente(cliente);
        } catch (SQLException e) {
            throw new DatosInvalidosException("No se pudo registrar el cliente: " + e.getMessage());
        }
    }

    public Cliente buscarCliente(String email) throws ClienteNoEncontradoException {
        try {
            Cliente cliente = clienteDAO.obtenerClientePorEmail(email);
            if (cliente == null) {
                throw new ClienteNoEncontradoException("No existe ningún cliente con el email: " + email);
            }
            return cliente;
        } catch (SQLException e) {
            throw new ClienteNoEncontradoException("Error al consultar el cliente: " + e.getMessage());
        }
    }

    public Cliente buscarClientePorId(int id) throws ClienteNoEncontradoException {
        try {
            Cliente cliente = clienteDAO.obtenerCliente(id);
            if (cliente == null) {
                throw new ClienteNoEncontradoException("No existe ningún cliente con ID: " + id);
            }
            return cliente;
        } catch (SQLException e) {
            throw new ClienteNoEncontradoException("Error al consultar el cliente por ID: " + e.getMessage());
        }
    }

    public List<Cliente> buscarClientesPorApellido(String apellido) throws DatosInvalidosException {
        try {
            return clienteDAO.buscarPorApellidoStoredProcedure(apellido);
        } catch (SQLException e) {
            throw new DatosInvalidosException("Error al ejecutar la búsqueda por apellido: " + e.getMessage());
        }
    }

    public List<Cliente> obtenerTodosLosClientes() throws DatosInvalidosException {
        try {
            return clienteDAO.obtenerTodosLosClientes();
        } catch (SQLException e) {
            throw new DatosInvalidosException("Error al obtener el listado de clientes: " + e.getMessage());
        }
    }

    public void actualizarCliente(Cliente cliente) throws DatosInvalidosException {
        if (cliente.getNombre() == null || cliente.getNombre().isBlank()
                || cliente.getApellido() == null || cliente.getApellido().isBlank()
                || cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            throw new DatosInvalidosException("Nombre, apellido y email son obligatorios.");
        }

        try {
            clienteDAO.actualizarCliente(cliente);
        } catch (SQLException e) {
            throw new DatosInvalidosException("No se pudo actualizar el cliente: " + e.getMessage());
        }
    }

    public void eliminarCliente(int id) throws DatosInvalidosException {
        try {
            clienteDAO.eliminarCliente(id);
        } catch (SQLException e) {
            throw new DatosInvalidosException("No se pudo eliminar el cliente (comprueba si tiene reservas asociadas): " + e.getMessage());
        }
    }
}