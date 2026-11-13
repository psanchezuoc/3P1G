package grupofp.dao;

public abstract class DAOFactory {

    public static final int MYSQL = 1;

    public abstract ClienteDAO getClienteDAO();
    public abstract HabitacionDAO getHabitacionDAO();
    public abstract ReservaDAO getReservaDAO();
    public abstract HotelDAO getHotelDAO();

    public static DAOFactory getDAOFactory(int motorDB) {
        switch (motorDB) {
            case MYSQL:
                return new MySQLDAOFactory();
            default:
                throw new IllegalArgumentException("Motor de base de datos no soportado.");
        }
    }
}