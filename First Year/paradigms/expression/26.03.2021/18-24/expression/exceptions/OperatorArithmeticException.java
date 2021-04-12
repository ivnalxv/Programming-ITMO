package expression.exceptions;

public class OperatorArithmeticException extends ExpressionException {
    public OperatorArithmeticException(String operation, String argument) {
        super(operation + " arithmetic error: " + argument);
    }
}