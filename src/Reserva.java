import java.util.List;
import java.time.LocalDate;

public class Reserva implements ICalculable {
    private int idReserva;
    private int noches;
    private double precioNoche;
    private int numeroHabitaciones;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private int cantidadPersonas;

    private Cliente cliente;
    private Hotel hotel;
    private List<Habitacion> habitaciones;

    public Reserva(int idReserva, int noches, double precioNoche, int numeroHabitaciones, Cliente cliente, Hotel hotel, List<Habitacion> habitaciones) {
        this.idReserva = idReserva;
        this.noches = noches;
        this.precioNoche = precioNoche;
        this.numeroHabitaciones = numeroHabitaciones;
        this.cliente = cliente;
        this.hotel = hotel;
        this.habitaciones = habitaciones;
    }

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }

    public int getNoches() { return noches; }
    public void setNoches(int noches) { this.noches = noches; }

    public double getPrecioNoche() { return precioNoche; }
    public void setPrecioNoche(double precioNoche) { this.precioNoche = precioNoche; }

    public int getNumeroHabitaciones() { return numeroHabitaciones; }
    public void setNumeroHabitaciones(int numeroHabitaciones) { this.numeroHabitaciones = numeroHabitaciones; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public List<Habitacion> getHabitaciones() { return habitaciones; }
    public void setHabitaciones(List<Habitacion> habitaciones) { this.habitaciones = habitaciones; }

    @Override
    public double calcularImporte() {
        return noches * precioNoche * numeroHabitaciones;
    }
}