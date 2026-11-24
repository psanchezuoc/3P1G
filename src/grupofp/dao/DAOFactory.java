package grupofp.dao;

public abstract class DAOFactory {
    public static final int MYSQL = 1;
    public static final int JPA = 2;

    public abstract ClienteDAO getClienteDAO();
    public abstract HabitacionDAO getHabitacionDAO();
    public abstract ReservaDAO getReservaDAO();
    public abstract HotelDAO getHotelDAO();

    public static DAOFactory getDAOFactory(int motorDB) {
        if (motorDB == MYSQL) {
            return new MySQLDAOFactory();
        } else if (motorDB == JPA) {
            return new JPADAOFactory();
        }
        throw new IllegalArgumentException("Motor de base de datos no soportado: " + motorDB);
    }
}