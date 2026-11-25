package grupofp.dao;

import grupofp.modelo.Hotel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

import java.sql.SQLException;
import java.util.List;

public class HotelJPADAOImpl implements HotelDAO {

    @Override
    public void crearHotel(Hotel hotel) throws SQLException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(hotel); // Ejecuta el INSERT y asigna automáticamente el idHotel generado
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new SQLException("Error al crear el hotel con JPA: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public Hotel obtenerHotel(int idHotel) throws SQLException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Hotel.class, idHotel);
        } catch (Exception e) {
            throw new SQLException("Error al obtener el hotel con JPA: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Hotel> obtenerTodosLosHoteles() throws SQLException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT h FROM Hotel h ORDER BY h.idHotel", Hotel.class)
                    .getResultList();
        } catch (Exception e) {
            throw new SQLException("Error al listar los hoteles con JPA: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void actualizarHotel(Hotel hotel) throws SQLException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Hotel existente = em.find(Hotel.class, hotel.getIdHotel());
            if (existente == null) {
                throw new SQLException("No existe ningún hotel con ID " + hotel.getIdHotel() + ".");
            }
            em.merge(hotel);
            em.getTransaction().commit();
        } catch (SQLException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new SQLException("Error al actualizar el hotel con JPA: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void eliminarHotel(int idHotel) throws SQLException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // 1. Comprobar si el hotel tiene reservas registradas
            Number totalReservas = (Number) em.createNativeQuery(
                            "SELECT COUNT(*) FROM reservas WHERE id_hotel = :id")
                    .setParameter("id", idHotel)
                    .getSingleResult();

            if (totalReservas.longValue() > 0) {
                throw new SQLException("El hotel tiene " + totalReservas.longValue()
                        + " reserva(s) registradas. Elimínalas antes de borrar el hotel.");
            }

            // 2. Eliminar sus habitaciones asociadas
            em.createNativeQuery("DELETE FROM habitaciones WHERE id_hotel = :id")
                    .setParameter("id", idHotel)
                    .executeUpdate();

            // 3. Buscar y eliminar el hotel
            Hotel hotel = em.find(Hotel.class, idHotel);
            if (hotel == null) {
                throw new SQLException("No existe ningún hotel con ID " + idHotel + ".");
            }
            em.remove(hotel);

            em.getTransaction().commit();
        } catch (SQLException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new SQLException("Error al eliminar el hotel con JPA: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Hotel> buscarPorCiudadStoredProcedure(String ciudad) throws SQLException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery("BuscarHotelesPorCiudad", Hotel.class);
            query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
            query.setParameter(1, ciudad);
            return query.getResultList();
        } catch (Exception e) {
            throw new SQLException("Error al buscar hoteles por ciudad con JPA: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}