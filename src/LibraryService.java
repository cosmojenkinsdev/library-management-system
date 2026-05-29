import Exceptions.InvalidReaderException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public class LibraryService {
    Collection<Reader> readers;
    Collection<BookCopy> copies;
    List<Loan> loans;
    OperationJournal journal;
    LibraryRules rules;

    public void addReader(Reader reader) throws InvalidReaderException {
        if (reader == null ){

        }
    }
    public void addCopy(BookCopy copy){}
    public OperationResult borrowBook(Reader reader, BookCopy copy, int days){}
    public OperationResult returnBook(Reader reader, BookCopy copy){}
    public OperationResult markLost(Reader reader, BookCopy copy, String reason){}
    public List<BookCopy> findAvailableCopiesByIsbn(String isbn){}
    public List<Reader> getAllReaders(){}
    public List<BookCopy> getAllCopies(){}
    public List<Loan> getAllLoans(){}
    public List<Loan> findActiveLoansByReader(Reader reader){}
    public List<Loan> findOverdueLoans(LocalDate date){}
    public OperationJournal getJournal(){}
}
