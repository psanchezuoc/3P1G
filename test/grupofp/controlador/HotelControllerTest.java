package grupofp.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import grupofp.dao.HotelDAO;
import grupofp.modelo.DatosInvalidosException;
import grupofp.modelo.Hotel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del HotelController con un DAO falso en memoria.
 * Demuestra la ventaja del patrón DAO: el controlador se prueba sin MySQL.
 */
class HotelControllerTest {

    private HotelController controlador;

    @BeforeEach
    void preparar() {
        controlador = new HotelController(new HotelDAOFalso());
    }

    @Test
    @DisplayName("Un hotel válido se registra y recibe un ID")
    void registrarHotelValido() throws Exception {
        Hotel hotel = new Hotel(0, "Hotel Mar", "Barcelona");
        controlador.registrarHotel(hotel);
        assertEquals(1, hotel.getIdHotel());
        assertEquals("Hotel Mar", controlador.buscarHotelPorId(1).getNombre());
    }

    @Test
    @DisplayName("No se registra un hotel sin nombre o sin ciudad")
    void registrarHotelInvalido() {
        assertThrows(DatosInvalidosException.class,
                () -> controlador.registrarHotel(new Hotel(0, " ", "Barcelona")));
        assertThrows(DatosInvalidosException.class,
                () -> controlador.registrarHotel(new Hotel(0, "Hotel Mar", "")));
    }

    @Test
    @DisplayName("Buscar un hotel inexistente lanza excepción")
    void buscarHotelInexistente() {
        assertThrows(DatosInvalidosException.class, () -> controlador.buscarHotelPorId(99));
    }

    /** DAO en memoria solo para las pruebas. */
    private static class HotelDAOFalso implements HotelDAO {
        private final List<Hotel> hoteles = new ArrayList<>();

        @Override
        public void crearHotel(Hotel hotel) {
            hotel.setIdHotel(hoteles.size() + 1);
            hoteles.add(hotel);
        }

        @Override
        public Hotel obtenerHotel(int idHotel) {
            return hoteles.stream().filter(h -> h.getIdHotel() == idHotel).findFirst().orElse(null);
        }

        @Override
        public List<Hotel> obtenerTodosLosHoteles() {
            return new ArrayList<>(hoteles);
        }

        @Override
        public void actualizarHotel(Hotel hotel) { }

        @Override
        public void eliminarHotel(int idHotel) throws SQLException { }

        @Override
        public List<Hotel> buscarPorCiudadStoredProcedure(String ciudad) {
            return new ArrayList<>();
        }
    }
}
