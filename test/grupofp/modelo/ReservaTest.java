package grupofp.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias del método de lógica de negocio Reserva.calcularImporte().
 * Fórmula del enunciado: noches x precio por noche x número de habitaciones.
 */
class ReservaTest {

    private static final double DELTA = 0.001;

    private Cliente cliente;
    private Hotel hotel;
    private List<Habitacion> habitaciones;

    @BeforeEach
    void preparar() {
        cliente = new Cliente(1, "Ana", "Lopez", "ana@correo.com");
        hotel = new Hotel(1, "Hotel Mar", "Barcelona");
        habitaciones = new ArrayList<>();
        habitaciones.add(new HabitacionEstandar("101", 2, 100.0, "Doble"));
    }

    private Reserva crearReserva(int noches, double precioNoche, int numeroHabitaciones) {
        return new Reserva(1, noches, precioNoche, numeroHabitaciones, cliente, hotel, habitaciones);
    }

    @Test
    @DisplayName("Una noche, una habitación: el importe es el precio de la noche")
    void importeUnaNocheUnaHabitacion() {
        assertEquals(100.0, crearReserva(1, 100.0, 1).calcularImporte(), DELTA);
    }

    @Test
    @DisplayName("Varias noches con precio decimal")
    void importeVariasNoches() {
        assertEquals(241.5, crearReserva(3, 80.5, 1).calcularImporte(), DELTA);
    }

    @Test
    @DisplayName("Varias noches y varias habitaciones multiplican el importe")
    void importeVariasHabitaciones() {
        assertEquals(720.0, crearReserva(2, 120.0, 3).calcularImporte(), DELTA);
    }

    @Test
    @DisplayName("Precio con céntimos y varias habitaciones")
    void importeConCentimos() {
        assertEquals(599.94, crearReserva(3, 99.99, 2).calcularImporte(), DELTA);
    }

    @Test
    @DisplayName("Con 0 noches el importe es 0")
    void importeCeroNoches() {
        assertEquals(0.0, crearReserva(0, 150.0, 2).calcularImporte(), DELTA);
    }

    @Test
    @DisplayName("El importe se recalcula al modificar las noches")
    void importeSeRecalculaAlModificarNoches() {
        Reserva reserva = crearReserva(2, 100.0, 1);
        assertEquals(200.0, reserva.calcularImporte(), DELTA);

        reserva.setNoches(5);

        assertEquals(500.0, reserva.calcularImporte(), DELTA);
    }

    @Test
    @DisplayName("Reserva se puede usar como ICalculable")
    void importeATravesDeLaInterfaz() {
        ICalculable calculable = crearReserva(4, 50.0, 1);
        assertEquals(200.0, calculable.calcularImporte(), DELTA);
    }
}
