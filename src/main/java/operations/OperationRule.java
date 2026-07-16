package operations;

import java.util.Optional;

@FunctionalInterface
public interface OperationRule<T extends LibraryOperation> {
    Optional<String> validate(T operation);
}