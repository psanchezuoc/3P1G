package grupofp.vista;

import grupofp.controlador.ClienteController;
import grupofp.controlador.HabitacionController;
import grupofp.controlador.ReservasController;
import grupofp.modelo.Datos;
import grupofp.modelo.Hotel;

public class MenuView {
    private final Consola consola;
    private final Hotel hotel;
    private final Datos datos;

    private final ClienteView clienteView;
    private final HabitacionView habitacionView;
    private final ReservasView reservasView;

    public MenuView() {
        this.consola = new Consola();
        this.hotel = new Hotel(1, "Hotel BCN", "Barcelona");
        this.datos = new Datos(); // La base de datos centralizada

        // Creamos los controladores inyectándoles los mismos datos
        ClienteController clienteCtrl = new ClienteController(datos);
        HabitacionController habitacionCtrl = new HabitacionController(datos);
        ReservasController reservasCtrl = new ReservasController(datos);

        // Conectamos cada vista estrictamente con su controlador
        this.clienteView = new ClienteView(clienteCtrl, consola);
        this.habitacionView = new HabitacionView(habitacionCtrl, consola);
        this.reservasView = new ReservasView(reservasCtrl, consola, hotel);
    }

    // Aquí debajo mantienes tu método iniciar() intacto...

    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n========== " + hotel.getNombre().toUpperCase() + " ==========");
            System.out.println("1. Gestion de clientes");
            System.out.println("2. Gestion de habitaciones");
            System.out.println("3. Gestion de reservas");
            System.out.println("0. Salir");
            opcion = consola.pedirEntero("Elige una opcion: ");

            switch (opcion) {
                case 1 -> clienteView.mostrarMenu();
                case 2 -> habitacionView.mostrarMenu();
                case 3 -> reservasView.mostrarMenu();
                case 0 -> System.out.println("Hasta pronto!");
                default -> System.out.println("Opcion no valida.");
            }
        } while (opcion != 0);
    }
}