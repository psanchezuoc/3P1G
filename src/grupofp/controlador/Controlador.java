package grupofp.controlador;

import grupofp.modelo.Cliente;
import grupofp.modelo.ClienteNoEncontradoException;
import grupofp.modelo.Datos;
import grupofp.modelo.DatosInvalidosException;
import grupofp.modelo.Habitacion;
import grupofp.modelo.HabitacionNoDisponibleException;
import grupofp.modelo.Reserva;
import java.util.List;

public class Controlador {

    private Datos datos;

    public Controlador() {
        this.datos = new Datos();
    }

    public Cliente buscarCliente(String email) throws ClienteNoEncontradoException {
        return datos.buscarCliente(email);
    }

    public void registrarCliente(Cliente nuevoCliente) throws DatosInvalidosException {
        datos.registrarCliente(nuevoCliente);
    }

    public Habitacion buscarHabitacion(String numero) throws HabitacionNoDisponibleException {
        return datos.buscarHabitacion(numero);
    }

    public void registrarHabitacion(Habitacion nuevaHabitacion) throws DatosInvalidosException {
        datos.registrarHabitacion(nuevaHabitacion);
    }

    public void registrarReserva(Reserva nuevaReserva) throws DatosInvalidosException {
        datos.registrarReserva(nuevaReserva);
    }

    public List<Reserva> obtenerTodasLasReservas() {
        return datos.obtenerTodasLasReservas();
    }
}