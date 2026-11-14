package grupofp.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConexionDB {

    private static String URL;
    private static String USUARIO;
    private static String PASSWORD;
    private static Connection conexion = null;

    static {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(fis);
            URL = prop.getProperty("db.url");
            USUARIO = prop.getProperty("db.user");
            PASSWORD = prop.getProperty("db.password");
        } catch (IOException e) {
            System.err.println("Error crítico: No se encontró el archivo config.properties en la raíz del proyecto.");
        }
    }

    private ConexionDB() { }

    public static Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Error crítico: Driver de MySQL no encontrado.", e);
            }
        }
        return conexion;
    }

    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                conexion = null;
            } catch (SQLException e) {
                System.err.println("Error al intentar cerrar la conexión con la base de datos: " + e.getMessage());
            }
        }
    }
}

