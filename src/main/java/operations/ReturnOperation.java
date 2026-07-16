package operations;

import entity.Reader;
import enums.LoanStatus;
import exceptions.InvalidOperationException;
import services.Loan;
import services.OperationResult;

public class ReturnOperation extends LibraryOperation {
    private final Loan loan;

    public ReturnOperation(Reader reader, Loan loan) {
        super(reader);
        validate(loan);
        this.loan = loan;
    }

    private void validate(Loan loan) {
        if (loan == null) {
            throw new InvalidOperationException("loan = null");
        }
    }

    public Loan getLoan() {
        return loan;
    }

    @Override
    public OperationResult execute() {
        if (loan.getStatus() == LoanStatus.ACTIVE) {
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
