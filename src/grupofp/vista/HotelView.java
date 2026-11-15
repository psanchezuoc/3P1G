package grupofp.vista;

import grupofp.controlador.HotelController;
import grupofp.modelo.DatosInvalidosException;
import grupofp.modelo.Hotel;

import java.util.List;

public class HotelView {
    private final HotelController controlador;
    private final Consola consola;

    public HotelView(HotelController controlador, Consola consola) {
        this.controlador = controlador;
        this.consola = consola;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n--- HOTELES ---");
            System.out.println("1. Registrar hotel");
            System.out.println("2. Listar todos los hoteles");
            System.out.println("3. Buscar hoteles por ciudad");
            System.out.println("4. Actualizar hotel");
            System.out.println("5. Eliminar hotel");
            System.out.println("0. Volver");
            opcion = consola.pedirEntero("Elige una opcion: ");

            switch (opcion) {
                case 1 -> registrarHotel();
                case 2 -> listarHoteles();
                case 3 -> buscarPorCiudad();
                case 4 -> actualizarHotel();
                case 5 -> eliminarHotel();
                case 0 -> { }
                default -> System.out.println("Opcion no valida.");
            }
        } while (opcion != 0);
    }

    /** Registra un hotel y lo devuelve (o null si no se pudo registrar). */
    public Hotel registrarHotel() {
        String nombre = consola.pedirTexto("Nombre del hotel: ");
        String ciudad = consola.pedirTexto("Ciudad: ");
        try {
            Hotel hotel = new Hotel(0, nombre, ciudad);
            controlador.registrarHotel(hotel);
            System.out.println("Hotel registrado correctamente con ID: " + hotel.getIdHotel());
            return hotel;
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    /** Muestra los hoteles y devuelve true si hay al menos uno. */
    public boolean listarHoteles() {
        try {
            return imprimirLista(controlador.obtenerTodosLosHoteles(), "No hay hoteles registrados.");
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    private void buscarPorCiudad() {
        String ciudad = consola.pedirTexto("Ciudad a buscar: ");
        try {
            imprimirLista(controlador.buscarHotelesPorCiudad(ciudad),
                    "No se encontraron hoteles en: " + ciudad);
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void actualizarHotel() {
        int id = consola.pedirEntero("ID del hotel a actualizar: ");
        try {
            Hotel existente = controlador.buscarHotelPorId(id);
            System.out.println("Hotel actual: " + existente);

            String nombre = consola.pedirTexto("Nuevo nombre (Enter para mantener): ");
            String ciudad = consola.pedirTexto("Nueva ciudad (Enter para mantener): ");

            Hotel actualizado = new Hotel(id,
                    nombre.isBlank() ? existente.getNombre() : nombre,
                    ciudad.isBlank() ? existente.getCiudad() : ciudad);
            controlador.actualizarHotel(actualizado);
            System.out.println("Hotel actualizado correctamente.");
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarHotel() {
        int id = consola.pedirEntero("ID del hotel a eliminar: ");
        try {
            Hotel hotel = controlador.buscarHotelPorId(id);
            System.out.println("Se eliminará " + hotel + " y todas sus habitaciones.");
            if (!consola.pedirSiNo("¿Confirmas la eliminación? (s/n): ")) {
                System.out.println("Operación cancelada.");
                return;
            }
            controlador.eliminarHotel(id);
            System.out.println("Hotel eliminado correctamente.");
        } catch (DatosInvalidosException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private boolean imprimirLista(List<Hotel> hoteles, String mensajeVacio) {
        if (hoteles.isEmpty()) {
            System.out.println(mensajeVacio);
            return false;
        }
        for (Hotel h : hoteles) {
            System.out.println("ID: " + h.getIdHotel() + " | " + h.getNombre() + " | " + h.getCiudad());
        }
        return true;
    }
}
