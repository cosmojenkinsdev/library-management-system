package config;

import entity.Book;
import entity.BookCopy;
import entity.Loan;
import entity.Reader;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateConfig {

    // поле для SessionFactory
    private static final SessionFactory sessionFactory = createSessionFactory();

    // метод создания SessionFactory
    private static SessionFactory createSessionFactory() {
        Configuration configuration = new Configuration()
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/library_db")
                .setProperty("hibernate.connection.username", "library_user")
                .setProperty("hibernate.connection.password", "library_password")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.hbm2ddl.auto", "update")
                .setProperty("hibernate.show_sql", "true")
                .setProperty("hibernate.format_sql", "true")
                .addAnnotatedClass(Book.class)
                .addAnnotatedClass(Reader.class)
                .addAnnotatedClass(BookCopy.class)
                .addAnnotatedClass(Loan.class);
        return configuration.buildSessionFactory();
    }
    // метод получения SessionFactory

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    // метод закрытия SessionFactory

    public static void closeSessionFactory() {
        if (!sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
