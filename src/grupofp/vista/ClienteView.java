package grupofp.vista;

import grupofp.controlador.ClienteController;
import grupofp.modelo.Cliente;
import grupofp.modelo.ClienteNoEncontradoException;
import grupofp.modelo.DatosInvalidosException;

import java.util.List;

public class ClienteView {
    private final ClienteController controlador;
    private final Consola consola;

    public ClienteView(ClienteController controlador, Consola consola) {
        this.controlador = controlador;
        this.consola = consola;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n--- CLIENTES ---");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Buscar cliente por email");
            System.out.println("3. Buscar clientes por apellido");
            System.out.println("4. Listar todos los clientes");
            System.out.println("5. Actualizar cliente");
            System.out.println("6. Eliminar cliente");
            System.out.println("0. Volver");
            opcion = consola.pedirEntero("Elige una opcion: ");

            switch (opcion) {
                case 1 -> registrarCliente();
                case 2 -> buscarCliente();
                case 3 -> buscarClientesPorApellido();
                case 4 -> listarClientes();
                case 5 -> actualizarCliente();
                case 6 -> eliminarCliente();
                case 0 -> { }
                default -> System.out.println("Opcion no valida.");
            }
        } while (opcion != 0);
    }

    private void registrarCliente() {
        String nombre = consola.pedirTexto("Nombre: ");
        String apellido = consola.pedirTexto("Apellido: ");
        String email = consola.pedirTexto("Email: ");

        try {
            Cliente nuevoCliente = new Cliente(0, nombre, apellido, email);
            controlador.registrarCliente(nuevoCliente);
            System.out.println("Cliente registrado correctamente con ID: " + nuevoCliente.getIdCliente());
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarCliente() {
        String email = consola.pedirTexto("Email del cliente: ");
        try {
            Cliente c = controlador.buscarCliente(email);
            System.out.println("ID: " + c.getIdCliente() + " | " + c + " | Email: " + c.getEmail());
        } catch (ClienteNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarClientesPorApellido() {
        String apellido = consola.pedirTexto("Apellido a buscar: ");
        try {
            List<Cliente> resultados = controlador.buscarClientesPorApellido(apellido);
            if (resultados.isEmpty()) {
                System.out.println("No se encontraron clientes con el apellido: " + apellido);
                return;
            }
            for (Cliente c : resultados) {
                System.out.println("ID: " + c.getIdCliente() + " | " + c + " | Email: " + c.getEmail());
            }
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarClientes() {
        try {
            List<Cliente> clientes = controlador.obtenerTodosLosClientes();
            if (clientes.isEmpty()) {
                System.out.println("No hay clientes registrados.");
                return;
            }
            for (Cliente c : clientes) {
                System.out.println("ID: " + c.getIdCliente() + " | " + c + " | Email: " + c.getEmail());
            }
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void actualizarCliente() {
        int id = consola.pedirEntero("ID del cliente a actualizar: ");
        try {
            Cliente existente = controlador.buscarClientePorId(id);
            System.out.println("Cliente actual: " + existente + " (" + existente.getEmail() + ")");

            String nuevoNombre = consola.pedirTexto("Nuevo nombre: ");
            String nuevoApellido = consola.pedirTexto("Nuevo apellido: ");
            String nuevoEmail = consola.pedirTexto("Nuevo email: ");

            Cliente actualizado = new Cliente(id, nuevoNombre, nuevoApellido, nuevoEmail);
            controlador.actualizarCliente(actualizado);
            System.out.println("Cliente actualizado correctamente.");
        } catch (ClienteNoEncontradoException | DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarCliente() {
        int id = consola.pedirEntero("ID del cliente a eliminar: ");
        try {
            controlador.buscarClientePorId(id);
            controlador.eliminarCliente(id);
            System.out.println("Cliente eliminado correctamente.");
        } catch (ClienteNoEncontradoException | DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}