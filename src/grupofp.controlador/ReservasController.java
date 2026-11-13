package grupofp.controlador;

import grupofp.modelo.Cliente;
import grupofp.modelo.ClienteNoEncontradoException;
import grupofp.modelo.Datos;
import grupofp.modelo.DatosInvalidosException;
import grupofp.modelo.Habitacion;
import grupofp.modelo.HabitacionNoDisponibleException;
import grupofp.modelo.Reserva;

import java.util.List;

public class ReservasController {
    private final Datos datos;

    public ReservasController(Datos datos) {
        this.datos = datos;
    }

    public Cliente buscarCliente(String email) throws ClienteNoEncontradoException {
        return datos.buscarCliente(email);
    }

    public Habitacion buscarHabitacion(String numero) throws HabitacionNoDisponibleException {
        return datos.buscarHabitacion(numero);
    }

    public void registrarReserva(Reserva reserva) throws DatosInvalidosException {
        datos.registrarReserva(reserva);
    }

    public List<Reserva> obtenerTodasLasReservas() {
        return datos.obtenerTodasLasReservas();
    }
}