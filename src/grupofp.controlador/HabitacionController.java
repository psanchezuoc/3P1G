package grupofp.controlador;

import grupofp.modelo.Datos;
import grupofp.modelo.DatosInvalidosException;
import grupofp.modelo.Habitacion;
import grupofp.modelo.HabitacionNoDisponibleException;

public class HabitacionController {
    private final Datos datos;

    public HabitacionController(Datos datos) {
        this.datos = datos;
    }

    public void registrarHabitacion(Habitacion habitacion) throws DatosInvalidosException {
        datos.registrarHabitacion(habitacion);
    }

    public Habitacion buscarHabitacion(String numero) throws HabitacionNoDisponibleException {
        return datos.buscarHabitacion(numero);
    }
}