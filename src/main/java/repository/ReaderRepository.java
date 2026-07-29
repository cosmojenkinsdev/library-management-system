package repository;

import entity.Reader;
import org.hibernate.SessionFactory;

public class ReaderRepository extends HibernateRepository<Reader, String> {
    public ReaderRepository(SessionFactory sessionFactory) {
        super(Reader.class, sessionFactory);
    }
}
