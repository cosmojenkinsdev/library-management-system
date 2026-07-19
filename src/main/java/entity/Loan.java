package entity;

import enums.LoanStatus;
import exceptions.InvalidOperationException;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;


@Entity
@Table(name = "loans")
public class Loan {
    @Id
    private String loanId;
    @ManyToOne
    @JoinColumn(name = "reader_id")
    private Reader reader;
    @ManyToOne
    @JoinColumn(name = "copy_id")
    private BookCopy copy;
    private LocalDate borrowedAt;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    protected Loan(){}
    public Loan(Reader reader, BookCopy copy, LocalDate borrowedAt, LocalDate dueDate) {
        validate(reader, copy, borrowedAt, dueDate);
        this.loanId = String.valueOf(UUID.randomUUID());
        this.reader = reader;
        this.copy = copy;
        this.borrowedAt = borrowedAt;
        this.dueDate = dueDate;
        this.status = LoanStatus.ACTIVE;
    }

    public String getLoanId() {
        return loanId;
    }

    public Reader getReader() {
        return reader;
    }

    public BookCopy getCopy() {
        return copy;
    }

    public LocalDate getBorrowedAt() {
        return borrowedAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Loan loan)) return false;
        return Objects.equals(getLoanId(), loan.getLoanId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getLoanId());
    }

    @Override
    public String toString() {
        return "Фактические выдачи: " +
                "loanId = " + loanId +
                ", reader = " + reader +
                ", copy = " + copy +
                ", borrowedAt = " + borrowedAt +
                ", dueDate = " + dueDate +
                ", status = " + status +
                '}';
    }

    private void validate(Reader reader,
                          BookCopy copy,
                          LocalDate borrowedAt,
                          LocalDate dueDate) {
        if (reader == null) {
            throw new InvalidOperationException("Читатель должен быть");
        }
        if (copy == null) {
            throw new InvalidOperationException("Экземпляр книги должен быть");
        }
        if (borrowedAt == null || dueDate == null) {
            throw new InvalidOperationException("Обе даты обязательны");
        }
        if (borrowedAt.isAfter(dueDate)) {
            throw new InvalidOperationException("Дата выдачи не может быть позже срока возврата");
        }
    }

    public void markReturned() throws InvalidOperationException {
        if (status != LoanStatus.ACTIVE) {
            throw new InvalidOperationException("Статус книги должен быть ACTIVE, чтобы стать RETURNED");
        }
        this.status = LoanStatus.RETURNED;
    }

    public void markLost() throws InvalidOperationException {
        if (status != LoanStatus.ACTIVE) {
            throw new InvalidOperationException("Статус книги должен быть ACTIVE, чтобы стать LOST");
        }
        status = LoanStatus.LOST;
    }
}
