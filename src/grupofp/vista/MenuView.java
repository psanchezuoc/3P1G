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
 * El hotel activo se carga desde la base de datos (ya no está escrito en el código).
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

        if (!seleccionarHotel()) {
            ConexionDB.cerrarConexion();
            System.out.println("No hay ningún hotel seleccionado. Saliendo.");
            return;
        }

        int opcion;
        do {
            System.out.println("\n========== " + hotel.getNombre().toUpperCase()
                    + " (" + hotel.getCiudad() + ") ==========");
            System.out.println("1. Gestion de clientes");
            System.out.println("2. Gestion de habitaciones");
            System.out.println("3. Gestion de reservas");
            System.out.println("4. Gestion de hoteles");
            System.out.println("5. Cambiar de hotel");
            System.out.println("0. Salir");
            opcion = consola.pedirEntero("Elige una opcion: ");

            switch (opcion) {
                case 1 -> clienteView.mostrarMenu();
                case 2 -> habitacionView.mostrarMenu();
                case 3 -> reservasView.mostrarMenu();
                case 4 -> {
                    hotelView.mostrarMenu();
                    if (!refrescarHotelActivo()) {
                        opcion = 0;
                    }
                }
                case 5 -> seleccionarHotel();
                case 0 -> { }
                default -> System.out.println("Opcion no valida.");
            }
        } while (opcion != 0);

        ConexionDB.cerrarConexion();
        System.out.println("Conexion cerrada. Hasta pronto!");
    }

    /**
     * Pide al usuario el hotel con el que trabajar. Si no hay ninguno,
     * ofrece crearlo. Devuelve false si no se ha podido seleccionar ninguno.
     */
    private boolean seleccionarHotel() {
        while (true) {
            System.out.println("\n--- SELECCION DE HOTEL ---");
            if (!hotelView.listarHoteles()) {
                if (!consola.pedirSiNo("¿Quieres registrar un hotel ahora? (s/n): ")) {
                    return hotel != null;
                }
                Hotel nuevo = hotelView.registrarHotel();
                if (nuevo != null) {
                    cargarHotel(nuevo);
                    return true;
                }
                continue;
            }

            int id = consola.pedirEntero("ID del hotel con el que quieres trabajar: ");
            try {
                cargarHotel(hotelCtrl.buscarHotelPorId(id));
                return true;
            } catch (DatosInvalidosException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Tras volver del menú de hoteles, recarga el hotel activo por si se ha
     * modificado o eliminado. Devuelve false si ya no queda ningún hotel.
     */
    private boolean refrescarHotelActivo() {
        try {
            cargarHotel(hotelCtrl.buscarHotelPorId(hotel.getIdHotel()));
            return true;
        } catch (DatosInvalidosException e) {
            System.out.println("El hotel activo ya no existe. Selecciona otro.");
            hotel = null;
            return seleccionarHotel();
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
