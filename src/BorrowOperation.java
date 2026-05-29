import Enums.CopyStatus;
import Exceptions.InvalidOperationException;

/**
 * проверить возможность выдачи
 * поменять статус BookCopy
 * вернуть OperationResult
 */
public class BorrowOperation extends LibraryOperation {
    private final BookCopy copy;
    private final int days;

    public BorrowOperation(Reader reader, BookCopy copy, int days) throws InvalidOperationException {
        super(reader);
        validate(copy, days);
        this.copy = copy;
        this.days = days;
    }

    public void validate(BookCopy copy, int days){
        if (copy == null) {
            throw new InvalidOperationException("Экземпляр книги обязателен");
        }
        if (days < 1 || days > 30){
            throw new InvalidOperationException("Некорректно указаны дни");
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
        if (copy.checkStatusBookCopy() == CopyStatus.BORROWED) {
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
