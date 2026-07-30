package services;

import config.HibernateTransactionExecutor;
import entity.Book;
import entity.BookCopy;
import entity.Loan;
import entity.Reader;
import enums.OperationStatus;
import exceptions.InvalidOperationException;
import operations.BorrowOperation;
import operations.LibraryOperation;
import operations.LostOperation;
import operations.ReturnOperation;
import repository.BookCopyRepository;
import repository.BookRepository;
import repository.LoanRepository;
import repository.ReaderRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class LibraryService {
    private static final String BAD_MSG = "Операция завершилась c ошибкой";
    private static final String GOOD_MSG = "Операция завершилась успешно";

    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final BookCopyRepository bookCopyRepository;
    private final LoanRepository loanRepository;
    private final HibernateTransactionExecutor transactionExecutor;
    private final OperationJournal journal;
    private final LibraryRules rules;

    public LibraryService(
            BookRepository bookRepository,
            ReaderRepository readerRepository,
            BookCopyRepository bookCopyRepository,
            LoanRepository loanRepository,
            HibernateTransactionExecutor transactionExecutor,
            OperationJournal journal,
            LibraryRules rules
    ) {
        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.loanRepository = loanRepository;
        this.transactionExecutor = transactionExecutor;
        this.journal = journal;
        this.rules = rules;
        validate();
    }

    private void validate() {
        if (rules == null) {
            throw new InvalidOperationException("Правила не могут быть null");
        }
        if (journal == null) {
            throw new InvalidOperationException("Журнал не может быть null");
        }
    }

    public void addBook(Book book) {
        bookRepository.save(book);
    }

    public void addReader(Reader reader) {
        readerRepository.save(reader);
    }

    public void addCopy(BookCopy copy) {
        bookCopyRepository.save(copy);
    }

    public boolean validateLoanCount(Reader reader) {
        return loanRepository.countActiveLoansByReader(reader) >= 3;
    }

    private OperationResult processOperation(LibraryOperation libraryOperation, Optional<String> error) {
        if (error.isPresent()) {
            return reject(libraryOperation);
        }
        libraryOperation.execute();
        return success(libraryOperation);
    }

    private <T extends LibraryOperation> OperationResult reject(T operation) {
        return new OperationResult(
                OperationStatus.REJECTED,
                BAD_MSG,
                operation.getOperationId(),
                LocalDateTime.now());

    }

    private <T extends LibraryOperation> OperationResult success(T operation) {
        return new OperationResult(
                OperationStatus.SUCCESS,
                GOOD_MSG,
                operation.getOperationId(),
                LocalDateTime.now());
    }

    public OperationResult borrowBook(Reader reader, BookCopy copy, int days) {
        BorrowOperation requestedOperation = new BorrowOperation(reader, copy, days);
        OperationResult result = transactionExecutor.executeInTransaction(session -> {
            Optional<Reader> managedReader = readerRepository.findById(session, reader.getReaderId());
            Optional<BookCopy> managedCopy = bookCopyRepository.findById(session, copy.getCopyId());

            if (managedReader.isEmpty() || managedCopy.isEmpty()) {
                return reject(requestedOperation);
            }

            BorrowOperation operation = new BorrowOperation(
                    managedReader.get(),
                    managedCopy.get(),
                    days
            );
            Optional<String> error = rules.validateBorrow(operation);

            if (error.isPresent()
                    || loanRepository.countActiveLoansByReader(session, managedReader.get()) >= 3) {
                return reject(operation);
            }

            operation.execute();
            Loan loan = new Loan(
                    managedReader.get(),
                    managedCopy.get(),
                    LocalDate.now(),
                    LocalDate.now().plusDays(days)
            );
            loanRepository.save(session, loan);
            return success(operation);
        });

        journal.add(result);
        return result;
    }

    public OperationResult returnBook(Reader reader, BookCopy copy) {
        OperationResult result = transactionExecutor.executeInTransaction(session -> {
            Optional<Reader> managedReader = readerRepository.findById(session, reader.getReaderId());
            Optional<BookCopy> managedCopy = bookCopyRepository.findById(session, copy.getCopyId());

            if (managedReader.isEmpty() || managedCopy.isEmpty()) {
                return missingEntityResult();
            }

            Optional<Loan> loan = loanRepository.findActiveLoan(
                    session,
                    managedReader.get(),
                    managedCopy.get()
            );
            if (loan.isEmpty()) {
                return missingEntityResult();
            }

            ReturnOperation operation = new ReturnOperation(managedReader.get(), loan.get());
            return processOperation(operation, rules.validateReturn(operation));
        });

        journal.add(result);
        return result;
    }

    public OperationResult markLost(Reader reader, BookCopy copy, String reason) {
        OperationResult result = transactionExecutor.executeInTransaction(session -> {
            Optional<Reader> managedReader = readerRepository.findById(session, reader.getReaderId());
            Optional<BookCopy> managedCopy = bookCopyRepository.findById(session, copy.getCopyId());

            if (managedReader.isEmpty() || managedCopy.isEmpty()) {
                return missingEntityResult();
            }

            Optional<Loan> loan = loanRepository.findActiveLoan(
                    session,
                    managedReader.get(),
                    managedCopy.get()
            );
            if (loan.isEmpty()) {
                return missingEntityResult();
            }

            LostOperation operation = new LostOperation(managedReader.get(), loan.get(), reason);
            return processOperation(operation, rules.validateLost(operation));
        });

        journal.add(result);
        return result;
    }

    private OperationResult missingEntityResult() {
        return new OperationResult(
                OperationStatus.REJECTED,
                BAD_MSG,
                "no id",
                LocalDateTime.now()
        );
    }

    public List<BookCopy> findAvailableCopiesByIsbn(String isbn) {
        return bookCopyRepository.findAvailableCopiesByIsbn(isbn);
    }

    public List<Reader> getAllReaders() {
        return readerRepository.findAll();
    }

    public List<BookCopy> getAllCopies() {
        return bookCopyRepository.findAll();
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public List<Loan> findActiveLoansByReader(Reader reader) {
        return loanRepository.findActiveLoansByReader(reader);
    }

    public List<Loan> findOverdueLoans(LocalDate date) {
        return loanRepository.findOverdueLoans(date);
    }

    public OperationJournal getJournal() {
        return journal;
    }
}
