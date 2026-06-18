package services;

import entity.BookCopy;
import entity.Reader;
import enums.CopyStatus;
import enums.LoanStatus;
import enums.OperationStatus;
import exceptions.InvalidBookCopyException;
import exceptions.InvalidOperationException;
import exceptions.InvalidReaderException;
import operations.BorrowOperation;
import operations.LibraryOperation;
import operations.LostOperation;
import operations.ReturnOperation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class LibraryService {
    private final HashSet<Reader> readers;
    private final HashSet<BookCopy> copies;
    private final List<Loan> loans;
    private final OperationJournal journal;
    private final LibraryRules rules;
    private final String BAD_MSG = "Операция завершилась c ошибкой";

    public LibraryService(
            OperationJournal journal,
            LibraryRules rules
    ) {
        readers = new HashSet<>();
        copies = new HashSet<>();
        loans = new ArrayList<>();
        this.rules = rules;
        this.journal = journal;
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
        if (reader == null) {
            throw new InvalidReaderException("Читатель не может быть null");
        }
        boolean controlRID = readers.stream()
                .anyMatch(anyReader -> anyReader.getReaderId().equals(reader.getReaderId()));
        if (controlRID) {
            throw new InvalidReaderException("Такой читатель уже есть по такому readerID");
        }
        readers.add(reader);
    }

    /**
     * Аналогично
     */
    public void addCopy(BookCopy copy) {
        if (copy == null) {
            throw new InvalidBookCopyException("Экземпляр книги не может быть null");
        }
        boolean controlBID = copies.stream()
                .anyMatch(anyCopy -> anyCopy.getCopyId().equals(copy.getCopyId()));
        if (controlBID) {
            throw new InvalidBookCopyException("Такая книга уже есть по такому copyID");
        }
        copies.add(copy);
    }

    /**
     * borrowBook я так понимаю это выданная книга и нужно проверить, выдалась или нет.
     */
    public OperationResult borrowBook(Reader reader, BookCopy copy, int days) {

        BorrowOperation borrowOperation = new BorrowOperation(reader, copy, days);
        Optional<String> error = rules.validateBorrow(borrowOperation);

        if (!readers.contains(reader)) {
            OperationResult result = new OperationResult(
                    OperationStatus.REJECTED,
                    BAD_MSG,
                    borrowOperation.getOperationId(),
                    LocalDateTime.now());
            journal.add(result);
            return result;
        }

        if (!copies.contains(copy)) {
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
        loans.add(loan);
        OperationResult result = success(borrowOperation);
        journal.add(result);
        return result;
    }

    public Boolean validateLoanCount(Reader reader) {
        return loans.stream()
                .filter(loan -> loan.getReader().equals(reader)
                        && loan.getStatus() == LoanStatus.ACTIVE)
                .count() >= 3;
    }

    private Optional<Loan> activeLoan(Reader reader, BookCopy copy) {
        return loans.stream()
                .filter(loan -> loan.getReader().equals(reader)
                        && loan.getCopy().equals(copy)
                        && loan.getStatus() == LoanStatus.ACTIVE)
                .findFirst();
    }

    public OperationResult returnBook(Reader reader, BookCopy copy) {
        Optional<Loan> loan = activeLoan(reader, copy);
        if (loan.isEmpty()) {
            OperationResult operationResult = new OperationResult(
                    OperationStatus.REJECTED,
                    BAD_MSG,
                    "no id",
                    LocalDateTime.now());
            journal.add(operationResult);
            return operationResult;
        }

        ReturnOperation returnOperation = new ReturnOperation(reader, loan.get());
        Optional<String> error = rules.validateReturn(returnOperation);
        if (error.isPresent()) {
            OperationResult result = reject(returnOperation);
            journal.add(result);
            return result;
        }

        returnOperation.execute();
        OperationResult result = success(returnOperation);
        journal.add(result);
        return result;
    }

    /**
     * аналогично
     */
    public OperationResult markLost(Reader reader, BookCopy copy, String reason) {
        Optional<Loan> loan = activeLoan(reader, copy);
        if (loan.isEmpty()) {
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
        if (error.isPresent()) {
            OperationResult result = reject(lostOperation);
            journal.add(result);
            return result;
        }
        lostOperation.execute();
        OperationResult result = success(lostOperation);
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

    /**
     * этот метод, я так понимаю, должен искать доступные копии по isbn и выдавать список,
     * где есть все копии данной книги
     */
    public List<BookCopy> findAvailableCopiesByIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new InvalidBookCopyException("isbn не может быть null");
        }
        return copies.stream()
                .filter(bookCopy -> bookCopy.getBook().getIsbn().equals(isbn)
                        && bookCopy.getStatus() == CopyStatus.AVAILABLE)
                .toList();
    }

    /**
     * метод должен показывать, всех активных читателей
     */
    public List<Reader> getAllReaders() {
        return List.copyOf(readers);
    }

    /**
     * такой же метод как и findAvailableCopiesByIsbn, только все копии
     */
    public List<BookCopy> getAllCopies() {
        return List.copyOf(copies);
    }

    /**
     * показывает все факты выдачи, которые были
     */
    public List<Loan> getAllLoans() {
        return List.copyOf(loans);
    }

    /**
     * показывает все активные выдачи
     */
    public List<Loan> findActiveLoansByReader(Reader reader) {
        if (reader == null) {
            throw new InvalidReaderException("Читатель не может быть null");
        }
        return loans.stream()
                .filter(loan -> loan.getReader().equals(reader)
                        && loan.getStatus() == LoanStatus.ACTIVE)
                .toList();
    }

    /**
     * показывает все завершенные выдачи
     */
    public List<Loan> findOverdueLoans(LocalDate date) {
        if (date == null) {
            throw new InvalidOperationException("Дата обязана быть");
        }
        return loans.stream()
                .filter(loan -> loan.getStatus() == LoanStatus.ACTIVE
                        && loan.getDueDate().isBefore(date))
                .toList();
    }

    /**
     * журнал всех операций
     */
    public OperationJournal getJournal() {
        return journal;
    }
}
