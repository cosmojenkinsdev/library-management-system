import java.util.List;
import java.util.Optional;

/**
 * Класс хранит и применяет правила валидации операций.
 */
public class LibraryRules {
    List<OperationRule<BorrowOperation>> borrowRules;
    List<OperationRule<ReturnOperation>> returnRules;
    List<OperationRule<LostOperation>> lostRules;

    public Optional<String> validateBorrow(BorrowOperation operation){
        return
    }
    public Optional<String> validateReturn(ReturnOperation operation){
        return
    }
    public Optional<String> validateLost(LostOperation operation){
        return
    }

    public
}
