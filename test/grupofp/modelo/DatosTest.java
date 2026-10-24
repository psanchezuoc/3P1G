package grupofp.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias del método de lógica de negocio Datos.registrarCliente():
 * valida los datos y no permite dos clientes con el mismo email.
 * Se usa buscarCliente() solo para comprobar el resultado.
 */
class DatosTest {

    private Datos datos;

    @BeforeEach
    void preparar() {
        datos = new Datos();
    }

    @Test
    @DisplayName("Un cliente válido se registra y se puede recuperar por email")
    void registrarClienteValido() throws Exception {
        Cliente cliente = new Cliente(1, "Ana", "Lopez", "ana@correo.com");

        datos.registrarCliente(cliente);

        assertSame(cliente, datos.buscarCliente("ana@correo.com"));
    }

    @Test
    @DisplayName("Dos clientes con emails distintos se registran sin problema")
    void registrarDosClientesDistintos() throws Exception {
        Cliente ana = new Cliente(1, "Ana", "Lopez", "ana@correo.com");
        Cliente luis = new Cliente(2, "Luis", "Perez", "luis@correo.com");

        datos.registrarCliente(ana);
        datos.registrarCliente(luis);

        assertSame(ana, datos.buscarCliente("ana@correo.com"));
        assertSame(luis, datos.buscarCliente("luis@correo.com"));
    }

    @Test
    @DisplayName("Un cliente null lanza DatosInvalidosException")
    void registrarClienteNull() {
        assertThrows(DatosInvalidosException.class, () -> datos.registrarCliente(null));
    }

    @Test
    @DisplayName("Un cliente con email null lanza DatosInvalidosException")
    void registrarClienteEmailNull() {
        Cliente sinEmail = new Cliente(1, "Ana", "Lopez", null);
        assertThrows(DatosInvalidosException.class, () -> datos.registrarCliente(sinEmail));
    }

    @Test
    @DisplayName("Un cliente con email vacío lanza DatosInvalidosException")
    void registrarClienteEmailVacio() {
        Cliente emailVacio = new Cliente(1, "Ana", "Lopez", "");
        assertThrows(DatosInvalidosException.class, () -> datos.registrarCliente(emailVacio));
    }

    @Test
    @DisplayName("Un email repetido lanza DatosInvalidosException indicando el email")
    void registrarEmailDuplicado() throws Exception {
        datos.registrarCliente(new Cliente(1, "Ana", "Lopez", "ana@correo.com"));
        Cliente repetido = new Cliente(2, "Otra", "Persona", "ana@correo.com");

        DatosInvalidosException error = assertThrows(DatosInvalidosException.class,
                () -> datos.registrarCliente(repetido));

        assertTrue(error.getMessage().contains("ana@correo.com"));
    }

    @Test
    @DisplayName("Al rechazar un duplicado se conserva el cliente original")
    void duplicadoNoSobrescribeAlOriginal() throws Exception {
        Cliente original = new Cliente(1, "Ana", "Lopez", "ana@correo.com");
        datos.registrarCliente(original);

        assertThrows(DatosInvalidosException.class,
                () -> datos.registrarCliente(new Cliente(2, "Otra", "Persona", "ana@correo.com")));

        Cliente guardado = datos.buscarCliente("ana@correo.com");
        assertSame(original, guardado);
        assertEquals("Ana", guardado.getNombre());
    }

    @Test
    @DisplayName("Un email que no se ha registrado lanza ClienteNoEncontradoException")
    void clienteNoRegistradoNoSeEncuentra() {
        assertThrows(ClienteNoEncontradoException.class, () -> datos.buscarCliente("nadie@correo.com"));
    }
}
