import Enums.OperationStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Хранит историю результатов операций.
 */
public class OperationJournal {
    List<OperationResult> results;

    public void add(OperationResult result) {

    }
    public List<OperationResult> getAll(){
        return
    }
    public List<OperationResult> findByStatus(OperationStatus status) {

    }
    public List<OperationResult> findByPeriod(LocalDateTime from, LocalDateTime to){

    }
    public void printAll(){}
}
