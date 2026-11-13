package grupofp.dao;

public class MySQLDAOFactory extends DAOFactory {

    @Override
    public ClienteDAO getClienteDAO() {
        // La Persona 2 cambiará esto por: return new ClienteDAOImpl();
        return null;
    }

    @Override
    public HabitacionDAO getHabitacionDAO() {
        // La Persona 3 cambiará esto por: return new HabitacionDAOImpl();
        return null;
    }

    @Override
    public ReservaDAO getReservaDAO() {
        // La Persona 4 cambiará esto por: return new ReservaDAOImpl();
        return null;
    }

    @Override
    public HotelDAO getHotelDAO() {
        // La Persona 5 cambiará esto por: return new HotelDAOImpl();
        return null;
    }
}