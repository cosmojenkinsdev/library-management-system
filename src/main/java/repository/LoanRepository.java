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

    public Loan findActiveLoansByReader(Reader reader){}
    public Loan findOverdueLoans(LocalDate date){}
    public int countActiveLoansByReader(Reader reader){}
    public Loan findActiveLoan(Reader reader, BookCopy copy){}
}
