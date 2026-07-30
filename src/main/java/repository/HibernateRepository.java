package repository;

import config.HibernateTransactionExecutor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public abstract class HibernateRepository<T, ID> implements Repository<T, ID> {
    private final Class<T> entityClass;
    private final HibernateTransactionExecutor transactionExecutor;

    protected HibernateRepository(Class<T> entityClass, SessionFactory sessionFactory) {
        this.entityClass = Objects.requireNonNull(entityClass);
        this.transactionExecutor = new HibernateTransactionExecutor(sessionFactory);
    }

    protected <R> R executeInTransaction(Function<Session, R> func) {
        return transactionExecutor.executeInTransaction(func);
    }

    @Override
    public void save(T entity) {
        executeInTransaction(session -> {
            save(session, entity);
            return null;
        });
    }

    public void save(Session session, T entity) {
        session.merge(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return executeInTransaction(session -> findById(session, id));
    }

    public Optional<T> findById(Session session, ID id) {
        return Optional.ofNullable(session.find(entityClass, id));
    }

    @Override
    public List<T> findAll() {
        return executeInTransaction(this::findAll);
    }

    public List<T> findAll(Session session) {
        String hql = "from " + entityClass.getName();
        return session.createQuery(hql, entityClass).getResultList();
    }

    @Override
    public void delete(T entity) {
        executeInTransaction(session -> {
            delete(session, entity);
            return null;
        });
    }

    public void delete(Session session, T entity) {
        T managedEntity = session.contains(entity) ? entity : session.merge(entity);
        session.remove(managedEntity);
    }
}
