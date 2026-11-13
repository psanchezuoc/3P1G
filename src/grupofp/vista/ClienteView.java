package grupofp.vista;

import grupofp.controlador.ClienteController;
import grupofp.modelo.Cliente;
import grupofp.modelo.ClienteNoEncontradoException;
import grupofp.modelo.DatosInvalidosException;

public class ClienteView {
    private final ClienteController controlador;
    private final Consola consola;
    private int siguienteIdCliente = 1;

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
            System.out.println("0. Volver");
            opcion = consola.pedirEntero("Elige una opcion: ");

            switch (opcion) {
                case 1 -> registrarCliente();
                case 2 -> buscarCliente();
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
            controlador.registrarCliente(new Cliente(siguienteIdCliente, nombre, apellido, email));
            siguienteIdCliente++;
            System.out.println("Cliente registrado correctamente.");
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarCliente() {
        String email = consola.pedirTexto("Email del cliente: ");
        try {
            Cliente c = controlador.buscarCliente(email);
            System.out.println(c + " | Email: " + c.getEmail() + " | ID: " + c.getIdCliente());
        } catch (ClienteNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}