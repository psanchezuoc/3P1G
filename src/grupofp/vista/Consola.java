package grupofp.vista;

import java.util.Scanner;

public class Consola {
    private final Scanner teclado;

    public Consola() {
        this.teclado = new Scanner(System.in);
    }

    public String pedirTexto(String mensaje) {
        System.out.print(mensaje);
        return teclado.nextLine().trim();
    }

    public int pedirEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(pedirTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Introduce un numero entero valido.");
            }
        }
    }

    public double pedirDecimal(String mensaje) {
        while (true) {
            try {
                return Double.parseDouble(pedirTexto(mensaje).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Introduce un numero valido (ej. 85.50).");
            }
        }
    }

    public boolean pedirSiNo(String mensaje) {
        while (true) {
            String r = pedirTexto(mensaje).toLowerCase();
            if (r.equals("s")) return true;
            if (r.equals("n")) return false;
            System.out.println("Responde 's' o 'n'.");
        }
    }
}