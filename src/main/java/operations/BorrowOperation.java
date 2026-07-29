package operations;

import entity.BookCopy;
import entity.Reader;
import enums.CopyStatus;
import exceptions.InvalidOperationException;
import services.OperationResult;

/**
 * проверить возможность выдачи
 * поменять статус main.java.main.java.operations.operations.entity.BookCopy
 * вернуть main.java.main.java.operations.operations.services.OperationResult
 */
public class BorrowOperation extends LibraryOperation {

    private final BookCopy copy;
    private final int days;

    public BorrowOperation(Reader reader, BookCopy copy, int days) throws InvalidOperationException {
        super(reader);
        validate(copy);
        this.copy = copy;
        this.days = days;
    }

    public void validate(BookCopy copy) {
        if (copy == null) {
            throw new InvalidOperationException("Экземпляр книги обязателен");
        }
    }

    public BookCopy getCopy() {
        return copy;
    }

    public int getDays() {
        return days;
    }

    @Override
    public OperationResult execute() {
        copy.markBorrowed();
        if (copy.getStatus() == CopyStatus.BORROWED) {
            return OperationResult.success(
                    getOperationId(),
                    "operation success"
            );
        }
        return OperationResult.rejected(
                getOperationId(),
                "operation rejected"
        );
    }
}
