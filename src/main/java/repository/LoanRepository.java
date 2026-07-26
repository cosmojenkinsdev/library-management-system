package repository;

import entity.BookCopy;
import entity.Loan;
import entity.Reader;
import org.hibernate.SessionFactory;

import java.time.LocalDate;

public class LoanRepository extends HibernateRepository<Loan, String> {
    protected LoanRepository(SessionFactory sessionFactory) {
        super(Loan.class, sessionFactory);
    }

    public Loan findActiveLoansByReader(Reader reader) {
        return null;
    }

    public Loan findOverdueLoans(LocalDate date) {
        return null;
    }

    public int countActiveLoansByReader(Reader reader) {
        return 0;
    }

    public Loan findActiveLoan(Reader reader, BookCopy copy) {
        return null;
    }
}
