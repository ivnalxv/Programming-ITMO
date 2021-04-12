package expression.exceptions;

import expression.operator.TypeOperator;

public class CheckedSubtract<T> extends AbstractBinaryOperator<T> {
    public CheckedSubtract(GeneralExpression<T> a, GeneralExpression<T> b, final TypeOperator<T> operator) {
        super(a, b, operator);
    }

    @Override
    protected String getOperator() {
        return " - ";
    }

    @Override
    protected T operate(T a, T b) {
        //checkException(a, b);
        return operator.subtract(a, b);
    }

    @Override
    protected boolean isOrdered() {
        return true;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    /*
    private void checkException(int a, int b) {
        if (b > 0 && Integer.MIN_VALUE + b > a) {
            throw new OperatorOverflowException("Subtract", Integer.toString(a, b));
        } else if (b < 0 && Integer.MAX_VALUE + b < a) {
            throw new OperatorOverflowException("Subtract", Integer.toString(a, b));
        }
    }

     */

}
