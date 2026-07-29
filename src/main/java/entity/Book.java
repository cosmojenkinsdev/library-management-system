package entity;

import enums.BookType;
import exceptions.InvalidBookException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Класс книги как библиографической сущности.
 */

@Entity
@Table(name = "books")
public class Book {

    @Id
    private String isbn;
    @Column(name = "title", length = 256, nullable = false)
    private String title;
    @Column(name = "author", length = 256, nullable = false)
    private String author;
    @Column(name = "publish_year", length = 256, nullable = false)
    private int publishYear;
    @Column(name = "book_type", length = 256, nullable = false)
    @Enumerated(EnumType.STRING)
    private BookType type;

    protected Book() {

    }

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
            throw new InvalidBookException("isbn обязан быть");
        }
        if (title == null || title.isBlank()) {
            throw new InvalidBookException("title обязано быть");
        }
        if (author == null || author.isBlank()) {
            throw new InvalidBookException("Автор обязан быть");
        }
        if (publishYear < LocalDate.of(1450, 1, 1).getYear() ||
                publishYear > LocalDate.now().getYear()) {
            throw new InvalidBookException("Год публикации обязан быть");
        }
        if (type == null) {
            throw new InvalidBookException("Тип обязан быть");
        }
    }
}
