import Enums.CopyStatus;
import Enums.LoanStatus;
import Exceptions.InvalidOperationException;

public class ReturnOperation extends LibraryOperation {
    private final Loan loan;

    protected ReturnOperation(Reader reader, Loan loan) {
        super(reader);
        validate(loan);
        this.loan = loan;
    }

    private void validate(Loan loan){
        if (loan == null){
            throw new InvalidOperationException("loan = null");
        }
    }

    @Override
    public OperationResult execute() {
        if (loan.checkStatus() == LoanStatus.ACTIVE) {
            loan.getCopy().markAvailable();
            loan.markReturned();
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
