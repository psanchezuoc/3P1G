package grupofp.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    // Debe coincidir exactamente con <persistence-unit name="hotelPU"> de persistence.xml
    private static final String PERSISTENCE_UNIT_NAME = "hotelPU";
    private static EntityManagerFactory factory;

    private JPAUtil() {
    }

    public static EntityManager getEntityManager() {
        if (factory == null || !factory.isOpen()) {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return factory.createEntityManager();
    }

    public static void cerrar() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}