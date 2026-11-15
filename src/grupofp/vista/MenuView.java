package grupofp.vista;

import grupofp.controlador.ClienteController;
import grupofp.controlador.HabitacionController;
import grupofp.controlador.HotelController;
import grupofp.controlador.ReservasController;
import grupofp.dao.ClienteDAO;
import grupofp.dao.ConexionDB;
import grupofp.dao.DAOFactory;
import grupofp.dao.HabitacionDAO;
import grupofp.dao.HotelDAO;
import grupofp.dao.ReservaDAO;
import grupofp.modelo.DatosInvalidosException;
import grupofp.modelo.Hotel;

import java.sql.SQLException;

/**
 * Menú principal. Todos los DAO se obtienen a través de la factoría (DAOFactory),
 * de modo que la vista y los controladores no dependen del motor de base de datos.
 * El hotel activo se elige entre los de la base de datos (ya no está escrito en el código).
 * Clientes y hoteles se gestionan sin hotel activo; habitaciones y reservas lo necesitan.
 */
public class MenuView {
    private final Consola consola;

    private final ClienteDAO clienteDAO;
    private final HabitacionDAO habitacionDAO;
    private final ReservaDAO reservaDAO;

    private final HotelController hotelCtrl;
    private final ClienteView clienteView;
    private final HotelView hotelView;

    private Hotel hotel;
    private HabitacionView habitacionView;
    private ReservasView reservasView;

    public MenuView() {
        this.consola = new Consola();

        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        this.clienteDAO = factory.getClienteDAO();
        this.habitacionDAO = factory.getHabitacionDAO();
        this.reservaDAO = factory.getReservaDAO();
        HotelDAO hotelDAO = factory.getHotelDAO();

        this.hotelCtrl = new HotelController(hotelDAO);
        this.clienteView = new ClienteView(new ClienteController(clienteDAO), consola);
        this.hotelView = new HotelView(hotelCtrl, consola);
    }

    public void iniciar() {
        try {
            ConexionDB.getConexion();
        } catch (SQLException e) {
            System.out.println("No se pudo conectar con la base de datos: " + e.getMessage());
            System.out.println("Revisa config.properties y que el servidor MySQL esté en marcha.");
            return;
        }

        int opcion;
        do {
            System.out.println("\n========== CADENA HOTELERA ==========");
            System.out.println(hotel == null
                    ? "Hotel activo: ninguno (eligelo con la opcion 5)"
                    : "Hotel activo: " + hotel.getNombre() + " (" + hotel.getCiudad() + ")");
            System.out.println("1. Gestion de clientes");
            System.out.println("2. Gestion de habitaciones del hotel activo");
            System.out.println("3. Gestion de reservas del hotel activo");
            System.out.println("4. Gestion de hoteles");
            System.out.println("5. Seleccionar / cambiar hotel activo");
            System.out.println("0. Salir");
            opcion = consola.pedirEntero("Elige una opcion: ");

            switch (opcion) {
                case 1 -> clienteView.mostrarMenu();
                case 2 -> {
                    if (hayHotelActivo()) {
                        habitacionView.mostrarMenu();
                    }
                }
                case 3 -> {
                    if (hayHotelActivo()) {
                        reservasView.mostrarMenu();
                    }
                }
                case 4 -> {
                    hotelView.mostrarMenu();
                    refrescarHotelActivo();
                }
                case 5 -> seleccionarHotel();
                case 0 -> { }
                default -> System.out.println("Opcion no valida.");
            }
        } while (opcion != 0);

        ConexionDB.cerrarConexion();
        System.out.println("Conexion cerrada. Hasta pronto!");
    }

    /** Habitaciones y reservas siempre pertenecen a un hotel: sin hotel activo no se puede entrar. */
    private boolean hayHotelActivo() {
        if (hotel == null) {
            System.out.println("Primero selecciona un hotel con la opcion 5 (o registralo en la opcion 4).");
            return false;
        }
        return true;
    }

    /** Muestra los hoteles de la base de datos y deja elegir el hotel activo. */
    private void seleccionarHotel() {
        System.out.println("\n--- SELECCION DE HOTEL ---");
        if (!hotelView.listarHoteles()) {
            System.out.println("Registra primero un hotel en la opcion 4 (Gestion de hoteles).");
            return;
        }
        int id = consola.pedirEntero("ID del hotel con el que quieres trabajar: ");
        try {
            cargarHotel(hotelCtrl.buscarHotelPorId(id));
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Tras volver del menú de hoteles, recarga el hotel activo por si se ha
     * modificado o eliminado.
     */
    private void refrescarHotelActivo() {
        if (hotel == null) {
            return;
        }
        try {
            hotel = hotelCtrl.buscarHotelPorId(hotel.getIdHotel());
            cargarHotel(hotel);
        } catch (DatosInvalidosException e) {
            System.out.println("El hotel activo se ha eliminado. Selecciona otro con la opcion 5.");
            hotel = null;
        }
    }

    /** Las vistas de habitaciones y reservas trabajan siempre sobre el hotel activo. */
    private void cargarHotel(Hotel nuevoHotel) {
        this.hotel = nuevoHotel;
        HabitacionController habitacionCtrl = new HabitacionController(habitacionDAO, hotel.getIdHotel());
        ReservasController reservasCtrl = new ReservasController(reservaDAO, clienteDAO, habitacionDAO, hotel.getIdHotel());
        this.habitacionView = new HabitacionView(habitacionCtrl, consola);
        this.reservasView = new ReservasView(reservasCtrl, consola, hotel);
        System.out.println("Trabajando con: " + hotel);
    }
}