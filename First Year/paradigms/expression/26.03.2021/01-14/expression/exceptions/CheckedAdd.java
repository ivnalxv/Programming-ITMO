package expression.exceptions;

import expression.operator.TypeOperator;

public class CheckedAdd<T> extends AbstractBinaryOperator<T> {
    public CheckedAdd(GeneralExpression<T> a, GeneralExpression<T> b, final TypeOperator<T> operator) {
        super(a, b, operator);
    }

    @Override
    protected String getOperator() {
        return " + ";
    }

    @Override
    protected T operate(T a, T b) {
        //checkException(a, b);
        return operator.add(a, b);
    }

    @Override
    protected boolean isOrdered() {
        return false;
    }

    /*
    private void checkException(int a, int b) {
        if (b > 0 && Integer.MAX_VALUE - b < a) {
            throw new OperatorOverflowException("Add", Integer.toString(a));
        } else if (b < 0 && Integer.MIN_VALUE - b > a) {
            throw new OperatorOverflowException("Add", Integer.toString(a));
        }
    }
    */

    @Override
    public int getPriority() {
        return 1;
    }

}
