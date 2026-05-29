import Enums.LoanStatus;
import Exceptions.InvalidOperationException;

/**
 * Операция отметки книги как потерянной.
 */
public class LostOperation extends LibraryOperation{
    private final Loan loan;
    private final String reason;

    public LostOperation(Reader reader, Loan loan, String reason) {
        super(reader);
        validate(loan, reason);
        this.loan = loan;
        this.reason = reason;
    }

    public void validate(Loan loan, String reason){
        if (loan == null){
            throw new InvalidOperationException("loan не может быть null");
        }
        if (reason == null || reason.isBlank()){
            throw new InvalidOperationException("Укажите причину пропажи экземпляра");
        }
    }
    @Override
    public OperationResult execute() {
        if (loan.checkStatus() == LoanStatus.ACTIVE){
            loan.getCopy().markLost();
            loan.markLost();
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
