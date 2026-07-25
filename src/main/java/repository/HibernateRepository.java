package repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class HibernateRepository<T, ID> implements Repository<T, ID> {
    private final Class<T> entityClass;
    private final SessionFactory sessionFactory;

    protected HibernateRepository(Class<T> entityClass, SessionFactory sessionFactory) {
        this.entityClass = entityClass;
        this.sessionFactory = sessionFactory;
    }

    protected <R> R executeInTransaction(Function<Session, R> func) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            R result = func.apply(session);
            transaction.commit();
            return result;
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public void save(T entity) {
        executeInTransaction(s -> {
            s.persist(entity);
            return null;
        });
    }

    @Override
    public Optional<T> findById(ID id) {
        executeInTransaction(s -> {
            s.find(entityClass, id);
            return Optional.empty();
        });
        return Optional.empty();
    }

    @Override
    public List<T> findAll() {
        String hql = "from " + entityClass.getSimpleName();
        return executeInTransaction(session -> session
                .createQuery(hql, entityClass)
                .getResultList());
    }

    @Override
    public void delete(T entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.remove(entity);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
