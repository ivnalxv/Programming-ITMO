package expression.exceptions;

public class OperatorOverflowException extends ExpressionException {
    public OperatorOverflowException(String operation, String argument) {
        super(operation + " overflow: " + argument);
    }
}
