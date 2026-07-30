package repository;

import entity.BookCopy;
import entity.Loan;
import entity.Reader;
import enums.LoanStatus;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LoanRepository extends HibernateRepository<Loan, String> {
    public LoanRepository(SessionFactory sessionFactory) {
        super(Loan.class, sessionFactory);
    }

    public List<Loan> findActiveLoansByReader(Reader reader) {
        return executeInTransaction(session -> findActiveLoansByReader(session, reader));
    }

    public List<Loan> findActiveLoansByReader(Session session, Reader reader) {
        return session.createQuery("""
                        FROM Loan l
                        WHERE l.status = :status
                        AND l.reader = :reader
                        """, Loan.class)
                .setParameter("status", LoanStatus.ACTIVE)
                .setParameter("reader", reader)
                .getResultList();
    }

    public List<Loan> findOverdueLoans(LocalDate date) {
        return executeInTransaction(session -> findOverdueLoans(session, date));
    }

    public List<Loan> findOverdueLoans(Session session, LocalDate date) {
        return session.createQuery("""
                        FROM Loan l
                        WHERE l.status = :status
                        AND l.dueDate < :today
                        """, Loan.class)
                .setParameter("status", LoanStatus.ACTIVE)
                .setParameter("today", date)
                .getResultList();
    }

    public long countActiveLoansByReader(Reader reader) {
        return executeInTransaction(session -> countActiveLoansByReader(session, reader));
    }

    public long countActiveLoansByReader(Session session, Reader reader) {
        return session.createQuery("""
                        SELECT COUNT(l)
                        FROM Loan l
                        WHERE l.status = :status
                        AND l.reader = :reader
                        """, Long.class)
                .setParameter("status", LoanStatus.ACTIVE)
                .setParameter("reader", reader)
                .getSingleResult();
    }

    public Optional<Loan> findActiveLoan(Reader reader, BookCopy copy) {
        return executeInTransaction(session -> findActiveLoan(session, reader, copy));
    }

    public Optional<Loan> findActiveLoan(Session session, Reader reader, BookCopy copy) {
        return session.createQuery("""
                        FROM Loan l
                        WHERE l.status = :status
                        AND l.reader = :reader
                        AND l.copy = :copy
                        """, Loan.class)
                .setParameter("status", LoanStatus.ACTIVE)
                .setParameter("reader", reader)
                .setParameter("copy", copy)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
}
