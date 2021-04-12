package expression.exceptions;

import expression.operator.TypeOperator;

public class CheckedNegate<T> extends AbstractUnaryOperator<T> {
    public CheckedNegate(GeneralExpression<T> a, final TypeOperator<T> operator) {
        super(a, operator);
    }

    @Override
    protected String getOperator() {
        return "-";
    }

    protected T operate(T x) {
        //checkException(x);
        return operator.negate(x);
    }


    /*
    private static void checkException(int x) {
        if (x == Integer.MIN_VALUE) {
            throw new OperatorOverflowException("Negate", Integer.toString(x));
        }
    }

     */
}
