package entity;

import enums.ReaderStatus;
import exceptions.InvalidReaderException;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Класс читателя библиотеки.
 */
@Entity
@Table(name = "readers")
public class Reader {
    @Id
    private String readerId;
    private String fullName;
    private LocalDate birthDate;
    private ReaderStatus status;

    protected Reader(){}

    public Reader(
            String readerId,
            String fullName,
            LocalDate birthDate,
            ReaderStatus status) {
        validate(readerId, fullName, birthDate, status);
        this.readerId = readerId;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.status = status;
    }

    public String getReaderId() {
        return readerId;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public ReaderStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Reader reader)) return false;
        return Objects.equals(getReaderId(), reader.getReaderId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getReaderId());
    }

    @Override
    public String toString() {
        return "Инфо. по читателю библиотеки: " +
                "id = " + readerId +
                ", ФИО = " + fullName +
                ", день рождения = " + birthDate +
                ", статус = " + status;
    }

    private void validate(String readerId,
                          String fullName,
                          LocalDate birthDate,
                          ReaderStatus status) throws InvalidReaderException {
        if (readerId == null || readerId.isBlank()) {
            throw new InvalidReaderException("readerId обязан быть");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new InvalidReaderException("ФИО обязано быть");
        }
        if (birthDate == null || birthDate.isAfter(LocalDate.now().minusYears(14))) {
            throw new InvalidReaderException("День рождения обязано быть или читателю должно быть 14 лет");
        }
        if (status == null) {
            throw new InvalidReaderException("Статус обязан быть");
        }
    }
}
