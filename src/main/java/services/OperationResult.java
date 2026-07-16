package services;

import enums.OperationStatus;
import exceptions.InvalidOperationException;

import java.time.LocalDateTime;

public record OperationResult(OperationStatus status,
                              String message,
                              String operationId,
                              LocalDateTime finishedAt) {
    public OperationResult {
        if (status == null) {
            throw new InvalidOperationException("статус обязателен");
        }
        if (message == null || message.isBlank()) {
            throw new InvalidOperationException("message обязателен и не должен быть пустым");
        }
        if (operationId == null || operationId.isBlank()) {
            throw new InvalidOperationException("id операции обязательно и не должно быть пустым");
        }
        if (finishedAt == null) {
            throw new InvalidOperationException("Время завершения операции обязательно");
        }
    }

    public static OperationResult success(String operationId, String message) {
        return new OperationResult(OperationStatus.SUCCESS, message, operationId, LocalDateTime.now());
    }

    public static OperationResult rejected(String operationId, String message) {
        return new OperationResult(OperationStatus.REJECTED, message, operationId, LocalDateTime.now());
    }

}
