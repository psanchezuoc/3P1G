package grupofp.dao;

public class JPADAOFactory extends DAOFactory {

    @Override
    public ClienteDAO getClienteDAO() {
        return new ClienteJPADAOImpl(); // Persona 2 (ClienteJPADAOImpl)
    }

    @Override
    public HabitacionDAO getHabitacionDAO() {
        return null; // Persona 3 (HabitacionJPADAOImpl)
    }

    @Override
    public ReservaDAO getReservaDAO() {
        return null; // Persona 4 (ReservaJPADAOImpl)
    }

    @Override
    public HotelDAO getHotelDAO() { return new HotelJPADAOImpl(); }
}