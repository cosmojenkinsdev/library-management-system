package entity;

import enums.BookType;
import exceptions.InvalidBookException;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Класс книги как библиографической сущности.
 */
public final class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private final int publishYear;
    private final BookType type;


    public Book(String isbn,
                String title,
                String author,
                int publishYear,
                BookType type) {
        validate(isbn, title, author, publishYear, type);
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
        this.type = type;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public BookType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Book book)) return false;
        return Objects.equals(getIsbn(), book.getIsbn());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIsbn());
    }

    @Override
    public String toString() {
        return "Информация по книге: " +
                "isbn = " + isbn +
                ", название = " + title +
                ", автор = " + author +
                ", год выпуска = " + publishYear +
                ", тип книги = " + type;
    }

    private void validate(String isbn,
                          String title,
                          String author,
                          int publishYear,
                          BookType type) throws InvalidBookException {
        if (isbn == null || isbn.isBlank()) {
            throw new InvalidBookException("readerId обязан быть");
        }
        if (title == null || title.isBlank()) {
            throw new InvalidBookException("ФИО обязано быть");
        }
        if (author == null || author.isBlank()) {
            throw new InvalidBookException("Автор обязан быть");
        }
        if (publishYear < LocalDate.of(1450, 1, 1).getYear() ||
                publishYear > LocalDate.now().getYear()) {
            throw new InvalidBookException("День рождения обязано быть");
        }
        if (type == null) {
            throw new InvalidBookException("Статус обязан быть");
        }
    }
}
