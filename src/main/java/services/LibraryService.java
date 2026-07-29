package services;

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
import repository.LoanRepository;
import repository.ReaderRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class LibraryService {
    private final ReaderRepository readerRepository;
    private final BookCopyRepository bookCopyRepository;
    private final LoanRepository loanRepository;
    private final OperationJournal journal;
    private final LibraryRules rules;
    private final String BAD_MSG = "Операция завершилась c ошибкой";

    public LibraryService(
            ReaderRepository readerRepository,
            BookCopyRepository bookCopyRepository,
            LoanRepository loanRepository,
            OperationJournal journal,
            LibraryRules rules
    ) {
        this.readerRepository = readerRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.loanRepository = loanRepository;
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

    public void addReader(Reader reader) {
        readerRepository.save(reader);
    }

    public void addCopy(BookCopy copy) {
        bookCopyRepository.save(copy);
    }

    public Boolean validateLoanCount(Reader reader) {
        return loanRepository.countActiveLoansByReader(reader) >= 3;
    }

    private Optional<Loan> activeLoan(Reader reader, BookCopy copy) {
        return loanRepository.findActiveLoan(reader, copy);
    }

    private OperationResult processOperation(LibraryOperation libraryOperation, Optional<String> error) {
        if (error.isPresent()) {
            OperationResult result = reject(libraryOperation);
            journal.add(result);
            return result;
        }
        libraryOperation.execute();
        OperationResult result = success(libraryOperation);
        journal.add(result);
        return result;
    }

    private <T extends LibraryOperation> OperationResult reject(T operation) {
        return new OperationResult(
                OperationStatus.REJECTED,
                BAD_MSG,
                operation.getOperationId(),
                LocalDateTime.now());

    }

    private <T extends LibraryOperation> OperationResult success(T operation) {
        String GOOD_MSG = "Операция завершилась успешно";
        return new OperationResult(
                OperationStatus.SUCCESS,
                GOOD_MSG,
                operation.getOperationId(),
                LocalDateTime.now());
    }

    public OperationResult borrowBook(Reader reader, BookCopy copy, int days) {

        BorrowOperation borrowOperation = new BorrowOperation(reader, copy, days);
        Optional<String> error = rules.validateBorrow(borrowOperation);

        if (readerRepository.findById(reader.getReaderId()).isEmpty()) {
            OperationResult result = new OperationResult(
                    OperationStatus.REJECTED,
                    BAD_MSG,
                    borrowOperation.getOperationId(),
                    LocalDateTime.now());
            journal.add(result);
            return result;
        }

        if (bookCopyRepository.findById(copy.getCopyId()).isEmpty()) {
            OperationResult result = reject(borrowOperation);
            journal.add(result);
            return result;
        }

        if (error.isPresent()) {
            OperationResult result = reject(borrowOperation);
            journal.add(result);
            return result;
        }

        if (validateLoanCount(reader)) {
            OperationResult result = reject(borrowOperation);
            journal.add(result);
            return result;
        }

        borrowOperation.execute();
        Loan loan = new Loan(reader, copy, LocalDate.now(), LocalDate.now().plusDays(days));
        loanRepository.save(loan);
        OperationResult result = success(borrowOperation);
        journal.add(result);
        return result;
    }

    public OperationResult returnBook(Reader reader, BookCopy copy) {
        Optional<Loan> loan = activeLoan(reader, copy);
        if (loanRepository.findActiveLoan(reader, copy).isEmpty()) {
            OperationResult operationResult = new OperationResult(
                    OperationStatus.REJECTED,
                    BAD_MSG,
                    "no id",
                    LocalDateTime.now());
            journal.add(operationResult);
            return operationResult;
        }
        // Немного запутался в архитектуре. В каком месте и как должен происходить коммит?
        ReturnOperation returnOperation = new ReturnOperation(reader, loan.get());
        Optional<String> error = rules.validateReturn(returnOperation);
        return processOperation(returnOperation, error);
    }

    public OperationResult markLost(Reader reader, BookCopy copy, String reason) {
        Optional<Loan> loan = activeLoan(reader, copy);
        if (loanRepository.findActiveLoan(reader, copy).isEmpty()) {
            OperationResult operationResult = new OperationResult(
                    OperationStatus.REJECTED,
                    BAD_MSG,
                    "no id",
                    LocalDateTime.now());
            journal.add(operationResult);
            return operationResult;
        }
        LostOperation lostOperation = new LostOperation(reader, loan.get(), reason);
        Optional<String> error = rules.validateLost(lostOperation);
        return processOperation(lostOperation, error);
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
