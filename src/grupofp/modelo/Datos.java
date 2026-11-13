package grupofp.modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Datos {
    private Map<String, Cliente> clientes;
    private List<Reserva> reservas;
    private List<Habitacion> habitaciones;
    private Hotel hotel;

    public Datos() {
        this.clientes = new HashMap<>();
        this.reservas = new ArrayList<>();
        this.habitaciones = new ArrayList<>();
    }

    public Cliente buscarCliente(String email) throws ClienteNoEncontradoException {
        if (!clientes.containsKey(email)) {
            throw new ClienteNoEncontradoException("No existe ningún cliente con el email: " + email);
        }
        return clientes.get(email);
    }

    public void registrarCliente(Cliente nuevoCliente) throws DatosInvalidosException {
        if (nuevoCliente == null || nuevoCliente.getEmail() == null || nuevoCliente.getEmail().isEmpty()) {
            throw new DatosInvalidosException("Los datos del cliente o su email no pueden estar vacíos.");
        }

        if (clientes.containsKey(nuevoCliente.getEmail())) {
            throw new DatosInvalidosException(
                    "Ya existe un cliente registrado con el email: " + nuevoCliente.getEmail());
        }

        clientes.put(nuevoCliente.getEmail(), nuevoCliente);
    }

    public Habitacion buscarHabitacion(String numero) throws HabitacionNoDisponibleException {
        for (Habitacion hab : habitaciones) {
            if (hab.getNumero().equals(numero)) {
                return hab;
            }
        }

        throw new HabitacionNoDisponibleException(
                "La habitacion " + numero + " no esta disponible ahora mismo o no existe.");
    }

    public void registrarHabitacion(Habitacion nuevaHabitacion) throws DatosInvalidosException {
        if (nuevaHabitacion == null || nuevaHabitacion.getNumero() == null) {
            throw new DatosInvalidosException("La información de la habitación es invalida.");
        }
        habitaciones.add(nuevaHabitacion);
    }

    public void registrarReserva(Reserva nuevaReserva) throws DatosInvalidosException {
        if (nuevaReserva == null) {
            throw new DatosInvalidosException("La información de la reserva no puede estar vacia");
        }
        reservas.add(nuevaReserva);
    }

    public List<Reserva> obtenerTodasLasReservas() {
        return reservas;
    }
}