package entity;

import enums.CopyStatus;
import exceptions.InvalidBookCopyException;

import java.util.Objects;

/**
 * `main.java.main.java.operations.operations.entity.BookCopy` — это конкретный экземпляр книги.
 */
public class BookCopy {
    private final String copyId;
    private final Book book;
    private CopyStatus status;

    public BookCopy(String copyId, Book book, CopyStatus status) throws InvalidBookCopyException {
        validate(copyId, book, status);
        this.copyId = copyId;
        this.book = book;
        this.status = status;
    }

    public String getCopyId() {
        return copyId;
    }

    public Book getBook() {
        return book;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BookCopy bookCopy)) return false;
        return Objects.equals(getCopyId(), bookCopy.getCopyId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCopyId());
    }

    @Override
    public String toString() {
        return "Экземпляр нашей книги: " +
                "id конкретного экземпляра = " + copyId +
                ", книга = " + book +
                ", статус книги = " + status;
    }

    public void markBorrowed() throws InvalidBookCopyException {
        if (this.status != CopyStatus.AVAILABLE) {
            throw new InvalidBookCopyException("Статус книги менять нельзя если статус уже BORROWED");
        }
        this.status = CopyStatus.BORROWED;
    }

    public void markAvailable() throws InvalidBookCopyException {
        if (this.status != CopyStatus.BORROWED) {
            throw new InvalidBookCopyException("Статус книги менять нельзя если статус уже AVAILABLE/LOST");
        }
        this.status = CopyStatus.AVAILABLE;
    }

    public void markLost() throws InvalidBookCopyException {
        if (this.status != CopyStatus.BORROWED) {
            throw new InvalidBookCopyException("Статус книги менять нельзя если статус уже BORROWED");
        }
        this.status = CopyStatus.LOST;
    }

    public CopyStatus getStatus() {
        return this.status;
    }

    private void validate(String copyId, Book book, CopyStatus status) {
        if (copyId == null || copyId.isBlank()) {
            throw new InvalidBookCopyException("ID у книги обязателен");
        }
        if (book == null) {
            throw new InvalidBookCopyException("У тебя же есть книга, да? Ведь есть???....");
        }
        if (status == null) {
            throw new InvalidBookCopyException("Статус книги обязателен");
        }
    }
}
