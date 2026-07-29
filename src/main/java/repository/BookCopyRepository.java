package repository;

import entity.BookCopy;
import org.hibernate.SessionFactory;

import java.util.List;

public class BookCopyRepository extends HibernateRepository<BookCopy, String> {
    protected BookCopyRepository(SessionFactory sessionFactory) {
        super(BookCopy.class, sessionFactory);
    }
    public List<BookCopy> findAvailableCopiesByIsbn(String isbn) {
        executeInTransaction(session -> {

            return
        });
        return result;
    }
}
