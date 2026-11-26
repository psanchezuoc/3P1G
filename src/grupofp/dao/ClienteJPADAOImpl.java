package grupofp.dao;

import grupofp.modelo.Cliente;
import jakarta.persistence.*;

import java.sql.SQLException;
import java.util.List;

public class ClienteJPADAOImpl implements ClienteDAO {

    @Override
    public void crearCliente(Cliente cliente) throws SQLException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(cliente);
            tx.commit();
        } catch (PersistenceException e) {
            if (tx.isActive()) tx.rollback();
            throw new SQLException("Error al crear el cliente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public Cliente obtenerCliente(int id) throws SQLException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Cliente.class, id);
        } catch (PersistenceException e) {
            throw new SQLException("Error al buscar el cliente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public Cliente obtenerClientePorEmail(String email) throws SQLException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Cliente> resultado = em.createQuery(
                            "SELECT c FROM Cliente c WHERE c.email = :email", Cliente.class)
                    .setParameter("email", email)
                    .getResultList();
            return resultado.isEmpty() ? null : resultado.get(0);
        } catch (PersistenceException e) {
            throw new SQLException("Error al buscar el cliente por email: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Cliente> obtenerTodosLosClientes() throws SQLException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Cliente c ORDER BY c.idCliente", Cliente.class)
                    .getResultList();
        } catch (PersistenceException e) {
            throw new SQLException("Error al listar los clientes: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void actualizarCliente(Cliente cliente) throws SQLException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(cliente);
            tx.commit();
        } catch (PersistenceException e) {
            if (tx.isActive()) tx.rollback();
            throw new SQLException("Error al actualizar el cliente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void eliminarCliente(int id) throws SQLException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Cliente cliente = em.find(Cliente.class, id);
            if (cliente != null) {
                em.remove(cliente);
            }
            tx.commit();
        } catch (PersistenceException e) {
            if (tx.isActive()) tx.rollback();
            throw new SQLException("Error al eliminar el cliente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Cliente> buscarPorApellidoStoredProcedure(String apellido) throws SQLException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            StoredProcedureQuery sp = em.createStoredProcedureQuery("BuscarClientePorApellido", Cliente.class);
            sp.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
            sp.setParameter(1, apellido);
            return sp.getResultList();
        } catch (PersistenceException e) {
            throw new SQLException("Error al ejecutar la búsqueda por apellido: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }


}