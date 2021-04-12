package expression.exceptions;

import expression.operator.TypeOperator;

public class CheckedDivide<T> extends AbstractBinaryOperator<T> {
    public CheckedDivide(GeneralExpression<T> a, GeneralExpression<T> b, final TypeOperator<T> operator) {
        super(a, b, operator);
    }

    @Override
    protected String getOperator() {
        return " / ";
    }

    @Override
    protected int getPriority() {
        return 2;
    }

    @Override
    protected T operate(T a, T b) {
        //checkException(a, b);
        return operator.divide(a, b);
    }

    @Override
    protected boolean isOrdered() {
        return true;
    }

    /*
    private void checkException(int a, int b) {
        if (a == Integer.MIN_VALUE && b == -1) {
            throw new OperatorOverflowException("Divide", Integer.toString(a));
        }
        if (b == 0) {
            throw new DivideByZeroException("Divide by zero: " + a + getOperator() + b);
        }
    }

     */

}
