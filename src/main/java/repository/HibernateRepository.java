package repository;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.Optional;

public abstract class HibernateRepository<T, ID> implements Repository<T, ID> {
    private final Class<T> entityClass;
    private final SessionFactory sessionFactory;

    protected HibernateRepository(Class<T> entityClass, SessionFactory sessionFactory) {
        this.entityClass = entityClass;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(T entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()){
            transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            T entity = session.find(entityClass, id);
            transaction.commit();
            return Optional.ofNullable(entity);
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()){
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public List<T> findAll() {
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            String hql = "from " + entityClass;
            transaction.commit();
            return session
                    .createQuery(hql, entityClass)
                    .getResultList();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()){
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public void delete(T entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.remove(entity);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()){
                transaction.rollback();
            }
            throw e;
        }
    }
}