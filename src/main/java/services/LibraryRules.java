package services;

import enums.CopyStatus;
import enums.LoanStatus;
import enums.ReaderStatus;
import operations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Класс хранит и применяет правила валидации операций.
 */
public class LibraryRules {

    private final List<OperationRule<BorrowOperation>> borrowRules;
    private final List<OperationRule<ReturnOperation>> returnRules;
    private final List<OperationRule<LostOperation>> lostRules;

    /**
     * 1. Зачем нам вообще метод validate из интерфейса? Просто для того, чтобы показать, что у нас есть
     * какой-то метод, который возвращает какой-то текст и этот метод должен быть в каждой проверке правил?
     *
     */
    public LibraryRules() {
        borrowRules = new ArrayList<>();
        returnRules = new ArrayList<>();
        lostRules = new ArrayList<>();
        initBorrowRules();
        initReturnRules();
        initLostRules();
    }

    private <T extends LibraryOperation> Optional<String> validate(T operation, List<OperationRule<T>> rules) {
        for (OperationRule<T> rule : rules) {
            Optional<String> error = rule.validate(operation);
            if (error.isPresent()) {
                System.out.println(error);
                return error;
            }
        }
        return Optional.empty();
    }

    public Optional<String> validateBorrow(BorrowOperation operation) {
        return validate(operation, borrowRules);
    }

    public Optional<String> validateReturn(ReturnOperation operation) {
        return validate(operation, returnRules);
    }

    public Optional<String> validateLost(LostOperation operation) {
        return validate(operation, lostRules);
    }

    public void initBorrowRules() {
        /**
         * main.java.main.java.operations.operations.OperationRule<main.java.main.java.operations.operations.BorrowOperation> ruleNull = new main.java.main.java.operations.operations.OperationRule<main.java.main.java.operations.operations.BorrowOperation>() {
         *     @Override
         *     public Optional<String> validate(main.java.main.java.operations.operations.BorrowOperation operation) {
         *         if (operation == null) {
         *             return Optional.of("Операция обязательна");
         *         }
         *         return Optional.empty();
         *     }
         * };
         */

        borrowRules.add(operation -> {
            if (operation == null) {
                return Optional.of("Операция не может быть null");
            }
            return Optional.empty();
        });

        borrowRules.add(operation -> {
            if (operation.getCopy().getStatus() != CopyStatus.AVAILABLE) {
                return Optional.of("Экземпляр книги недоступен для выдачи");
            }
            return Optional.empty();
        });

        borrowRules.add(operation -> {
            if (operation.getDays() < 1 || operation.getDays() > 30) {
                return Optional.of("Дни не могут быть <1 и >30");
            }
            return Optional.empty();
        });

        borrowRules.add(operation -> {
            if (operation.getReader() == null) {
                return Optional.of("Читатель не может быть null");
            }
            return Optional.empty();
        });

        borrowRules.add(operation -> {
            if (operation.getReader().getStatus() == ReaderStatus.BLOCKED) {
                return Optional.of("Статус читателя не может быть BLOCKED");
            }
            return Optional.empty();
        });
    }

    public void initReturnRules() {
        returnRules.add(operation -> {
            if (operation.getLoan() == null) {
                return Optional.of("main.java.main.java.operations.operations.entity.Loan не может быть null");
            }
            return Optional.empty();
        });
        returnRules.add(operation -> {
            if (operation.getLoan().getStatus() != LoanStatus.ACTIVE) {
                return Optional.of("LoanStatus должен быть ACTIVE");
            }
            return Optional.empty();
        });
        returnRules.add(operation -> {
            if (operation.getLoan().getCopy().getStatus() != CopyStatus.BORROWED) {
                return Optional.of("Вернуть можно только экземпляр со статусом BORROWED");
            }
            return Optional.empty();
        });
    }

    public void initLostRules() {
        lostRules.add(operation -> {
            if (operation.getLoan() == null) {
                return Optional.of("main.java.main.java.operations.operations.entity.Loan не может быть null");
            }
            return Optional.empty();
        });
        lostRules.add(operation -> {
            if (operation.getReason() == null) {
                return Optional.of("Reason не может быть null");
            }
            return Optional.empty();
        });
        lostRules.add(operation -> {
            if (operation.getReason().isBlank()) {
                return Optional.of("Reason не может быть пустым");
            }
            return Optional.empty();
        });
        lostRules.add(operation -> {
            if (operation.getLoan().getStatus() != LoanStatus.ACTIVE) {
                return Optional.of("LoanStatus должен быть ACTIVE");
            }
            return Optional.empty();
        });
        lostRules.add(operation -> {
            if (operation.getLoan().getCopy().getStatus() != CopyStatus.BORROWED) {
                return Optional.of("Вернуть можно только экземпляр со статусом BORROWED");
            }
            return Optional.empty();
        });
    }
}
