package repository;

import entity.BookCopy;
import enums.CopyStatus;
import org.hibernate.SessionFactory;

import java.util.List;

public class BookCopyRepository extends HibernateRepository<BookCopy, String> {
    public BookCopyRepository(SessionFactory sessionFactory) {
        super(BookCopy.class, sessionFactory);
    }
    public List<BookCopy> findAvailableCopiesByIsbn(String isbn) {
        return executeInTransaction(session -> session.createQuery("""
                        FROM BookCopy AS bc
                        WHERE status = :status
                        AND bc.book = :isbn
                        """)
                .setParameter("status", CopyStatus.AVAILABLE.name())
                .setParameter("isbn", isbn)
                .getResultList());
    }
}
