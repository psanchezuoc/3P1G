package grupofp.vista;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Consola {
    private final Scanner scanner;

    public Consola() {
        this.scanner = new Scanner(System.in);
    }

    public String pedirTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    public int pedirEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error: Introduce un número entero válido.");
            }
        }
    }

    public double pedirDecimal(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Error: Introduce un número decimal válido.");
            }
        }
    }

    public boolean pedirSiNo(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String respuesta = scanner.nextLine().trim().toLowerCase();
            if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
                return true;
            }
            if (respuesta.equals("n") || respuesta.equals("no")) {
                return false;
            }
            System.out.println("Error: Responde con 's' o 'n'.");
        }
    }

    public LocalDate pedirFecha(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("Error: Formato de fecha no válido. Usa AAAA-MM-DD (ejemplo: 2026-11-20).");
            }
        }
    }
}