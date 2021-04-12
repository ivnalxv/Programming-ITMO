package expression.exceptions;

import expression.operator.TypeOperator;

public class CheckedMultiply<T> extends AbstractBinaryOperator<T> {
    public CheckedMultiply(GeneralExpression<T> a, GeneralExpression<T> b, final TypeOperator<T> operator) {
        super(a, b, operator);
    }

    @Override
    protected String getOperator() {
        return " * ";
    }

    @Override
    protected T operate(T a, T b) {
        //checkException(a, b);
        return operator.multiply(a, b);
    }

    @Override
    protected boolean isOrdered() {
        return false;
    }

    @Override
    public int getPriority() {
        return 2;
    }

    /*
    public void checkException(int a, int b) {
        if (a != 0 && b != 0) {
            if (a == Integer.MIN_VALUE && b == -1 || a == -1 && b == Integer.MIN_VALUE) {
                throw new OperatorOverflowException("Multiply", Integer.toString(a));
            } else if (a * b == Integer.MIN_VALUE && Integer.MIN_VALUE / b == a) {
                return;
            } else if ((a == Integer.MIN_VALUE && b != 1) || (a != 1 && b == Integer.MIN_VALUE)) {
                throw new OperatorOverflowException("Multiply", Integer.toString(a));
            } else if (Integer.MAX_VALUE / abs(a) < abs(b)) {
                throw new OperatorOverflowException("Multiply", Integer.toString(a));
            }
        }
    }

    private static int abs(int x) {
        return x >= 0 ? x : -x;
    }

     */
}
