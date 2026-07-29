package repository;

import entity.Book;
import org.hibernate.SessionFactory;

public class BookRepository extends HibernateRepository<Book, String> {
    protected BookRepository(SessionFactory sessionFactory) {
        super(Book.class, sessionFactory);
    }

}
