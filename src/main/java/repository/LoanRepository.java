package repository;

import entity.BookCopy;
import entity.Loan;
import entity.Reader;
import enums.LoanStatus;
import org.hibernate.SessionFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LoanRepository extends HibernateRepository<Loan, String> {
    public LoanRepository(SessionFactory sessionFactory) {
        super(Loan.class, sessionFactory);
    }

    public List<Loan> findActiveLoansByReader(Reader reader) {
        return executeInTransaction(session -> session.createQuery("""
                              FROM Loan AS l
                              WHERE status = :status
                              AND l.reader = :reader
                        """, Loan.class
                ).setParameter("status", LoanStatus.ACTIVE.name())
                .setParameter("reader", reader)
                .getResultList());
    }

    public List<Loan> findOverdueLoans(LocalDate date) {
        return executeInTransaction(session -> session.createQuery("""
                        FROM Loan AS l
                        WHERE status = :status
                        AND l.dueDate < :today
                        """, Loan.class)
                .setParameter("status", LoanStatus.ACTIVE.name())
                .setParameter("today", date)
                .getResultList());
    }

    public long countActiveLoansByReader(Reader reader) {
        return executeInTransaction(session -> session.createQuery("""
                        SELECT COUNT(*)
                        FROM Loan AS l
                        WHERE status = :status
                        AND l.reader = :reader
                        """, Loan.class)
                .setParameter("status", LoanStatus.ACTIVE.name())
                .setParameter("reader", reader)
                .getResultCount());
    }

    public Optional<Loan> findActiveLoan(Reader reader, BookCopy copy) {
        return Optional.ofNullable(executeInTransaction(session -> session.createQuery("""
                        FROM Loan AS l
                        WHERE l.status = :status
                        AND l.reader = :reader
                        AND l.copy = :copy
                        """, Loan.class)
                .setParameter("status", LoanStatus.ACTIVE.name())
                .setParameter("reader", reader)
                .setParameter("copy", copy)
                .getSingleResult()));
    }
}
