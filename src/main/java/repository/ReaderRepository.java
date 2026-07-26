package repository;

import entity.Book;
import entity.Reader;
import org.hibernate.SessionFactory;

public class ReaderRepository extends HibernateRepository<Reader, String> {
    protected ReaderRepository(SessionFactory sessionFactory) {
        super(Reader.class, sessionFactory);
    }
}
