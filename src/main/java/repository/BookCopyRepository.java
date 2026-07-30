package repository;

import entity.BookCopy;
import enums.CopyStatus;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class BookCopyRepository extends HibernateRepository<BookCopy, String> {
    public BookCopyRepository(SessionFactory sessionFactory) {
        super(BookCopy.class, sessionFactory);
    }

    public List<BookCopy> findAvailableCopiesByIsbn(String isbn) {
        return executeInTransaction(session -> findAvailableCopiesByIsbn(session, isbn));
    }

    public List<BookCopy> findAvailableCopiesByIsbn(Session session, String isbn) {
        return session.createQuery("""
                        FROM BookCopy bc
                        WHERE bc.status = :status
                        AND bc.book.isbn = :isbn
                        """, BookCopy.class)
                .setParameter("status", CopyStatus.AVAILABLE)
                .setParameter("isbn", isbn)
                .getResultList();
    }
}
