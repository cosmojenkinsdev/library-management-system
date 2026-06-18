package operations;

import entity.Reader;
import exceptions.InvalidOperationException;
import services.OperationResult;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * `operations.LibraryOperation` хранит общие данные операции
 */
public abstract class LibraryOperation {
    private final String operationId;
    private final LocalDateTime createdAt;
    private final Reader reader;

    protected LibraryOperation(Reader reader) throws InvalidOperationException {
        if (reader == null) {
            throw new InvalidOperationException("Читатель обязателен");
        }
        this.operationId = String.valueOf(UUID.randomUUID());
        this.createdAt = LocalDateTime.now();
        this.reader = reader;
    }

    public abstract OperationResult execute();

    public String getOperationId() {
        return operationId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Reader getReader() {
        return reader;
    }
}
