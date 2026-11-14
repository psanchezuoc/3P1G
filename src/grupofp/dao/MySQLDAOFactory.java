package grupofp.dao;

public class MySQLDAOFactory extends DAOFactory {

    @Override
    public ClienteDAO getClienteDAO() { return new ClienteDAOImpl(); }

    @Override
    public HabitacionDAO getHabitacionDAO() {
        return new HabitacionDAOImpl();
    }

    @Override
    public ReservaDAO getReservaDAO() { return new ReservaDAOImpl(); }

    @Override
    public HotelDAO getHotelDAO() {
        // La Persona 5 cambiará esto por: return new HotelDAOImpl();
        return null;
    }
}