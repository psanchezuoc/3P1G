package grupofp.controlador;

import grupofp.modelo.Cliente;
import grupofp.modelo.ClienteNoEncontradoException;
import grupofp.modelo.Datos;
import grupofp.modelo.DatosInvalidosException;

public class ClienteController {
    private final Datos datos;

    public ClienteController(Datos datos) {
        this.datos = datos;
    }

    public void registrarCliente(Cliente cliente) throws DatosInvalidosException {
        datos.registrarCliente(cliente);
    }

    public Cliente buscarCliente(String email) throws ClienteNoEncontradoException {
        return datos.buscarCliente(email);
    }
}