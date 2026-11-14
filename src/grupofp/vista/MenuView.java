package grupofp.vista;

import grupofp.controlador.ClienteController;
import grupofp.controlador.HabitacionController;
import grupofp.controlador.ReservasController;
import grupofp.dao.ClienteDAO;
import grupofp.dao.ConexionDB;
import grupofp.dao.DAOFactory;
import grupofp.dao.HabitacionDAO;
import grupofp.dao.ReservaDAO;
import grupofp.modelo.Hotel;

public class MenuView {
    private final Consola consola;
    private final Hotel hotel;

    private final ClienteView clienteView;
    private final HabitacionView habitacionView;
    private final ReservasView reservasView;

    public MenuView() {
        this.consola = new Consola();
        this.hotel = new Hotel(1, "Hotel BCN", "Barcelona");

        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        ClienteDAO clienteDAO = factory.getClienteDAO();
        HabitacionDAO habitacionDAO = factory.getHabitacionDAO();
        ReservaDAO reservaDAO = factory.getReservaDAO();

        ClienteController clienteCtrl = new ClienteController(clienteDAO);
        HabitacionController habitacionCtrl = new HabitacionController(habitacionDAO, hotel.getIdHotel());
        ReservasController reservasCtrl = new ReservasController(reservaDAO, clienteDAO, habitacionDAO, hotel.getIdHotel());

        this.clienteView = new ClienteView(clienteCtrl, consola);
        this.habitacionView = new HabitacionView(habitacionCtrl, consola);
        this.reservasView = new ReservasView(reservasCtrl, consola, hotel);
    }

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
                case 0 -> {
                    ConexionDB.cerrarConexion();
                    System.out.println("Conexion cerrada. Hasta pronto!");
                }
                default -> System.out.println("Opcion no valida.");
            }
        } while (opcion != 0);
    }
}