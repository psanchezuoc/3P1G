import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // test instancias de las clases creadas
        Cliente cliente = new Cliente(1, "Marco", "Fernandez", "MF@test.com");


        Hotel hotel = new Hotel(1, "Hotel BCN", "Barcelona");


        Habitacion habitacion = new Suite("123", 2, 250.0, true);


        List<Habitacion> listaHabitaciones = new ArrayList<>();
        listaHabitaciones.add(habitacion);


        Reserva reserva = new Reserva(101, 4, habitacion.getPrecioNoche(), listaHabitaciones.size(), cliente, hotel, listaHabitaciones);


        System.out.println("--------------------------------------------------------TEST INSTANCIAS--------------------------------------------------------");
        System.out.println(cliente.toString());
        System.out.println(hotel.toString());
        System.out.println("Habitación: " + habitacion.getNumero() + " | Categoría: " + habitacion.getCategoria() + " | Precio/Noche: " + habitacion.getPrecioNoche() + "€");
        System.out.println("Importe total de la Reserva: " + reserva.calcularImporte() + "€");
    }
}