package grupofp.vista;

/**
 * Punto de entrada de la aplicación. Solo arranca la Vista.
 */
public class Main {
    public static void main(String[] args) {
        MenuView menu = new MenuView();
        menu.iniciar();
    }
}