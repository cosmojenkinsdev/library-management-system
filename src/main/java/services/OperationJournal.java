package services;

import enums.OperationStatus;
import exceptions.InvalidOperationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Хранит историю результатов операций.
 */
public class OperationJournal {
    private final List<OperationResult> results;

    public OperationJournal() {
        results = new ArrayList<>();
    }

    public void add(OperationResult result) {
        if (result == null) {
            throw new InvalidOperationException("Статус операции обязателен");
        }
        results.add(result);
    }

    public List<OperationResult> getAll() {
        return List.copyOf(results);
    }

    public List<OperationResult> findByStatus(OperationStatus status) {
        if (status == null) {
            throw new InvalidOperationException("Статус операции обязателен");
        }
        if (results.isEmpty()){
            return List.copyOf(results);
        }
        List<OperationResult> foundResults = new ArrayList<>();
        for (OperationResult result : results) {
            if (result.status() == status) {
                foundResults.add(result);
            }
        }
        return List.copyOf(foundResults);
    }

    public List<OperationResult> findByPeriod(LocalDateTime from, LocalDateTime to) {
        if (results.isEmpty()){
            throw new InvalidOperationException("Список результатов пуст");
        }
        List<OperationResult> foundResultsByPeriod = new ArrayList<>();
        if (from == null) {
            throw new InvalidOperationException("Дата начала периода обязательна");
        }
        if (to == null) {
            throw new InvalidOperationException("Дата конца периода обязательна");
        }
        if (from.isAfter(to)) {
            throw new InvalidOperationException("Дата начала не может быть позже даты конца");
        }
        for (OperationResult result : results) {
            if (!result.finishedAt().isBefore(from) && !result.finishedAt().isAfter(to)) {
                foundResultsByPeriod.add(result);
            }
        }
        return List.copyOf(foundResultsByPeriod);
    }

    public void printAll() {
        if (results.isEmpty()){
            System.out.println("Результатов еще нет");
        }
        results.forEach(System.out::println);
    }

    @Override
    public String toString() {
        return "OperationJournal{" +
                "results=" + results +
                '}';
    }
}
