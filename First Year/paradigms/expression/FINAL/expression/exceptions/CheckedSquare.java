package expression.exceptions;

import expression.operator.TypeOperator;

public class CheckedSquare<T> extends AbstractUnaryOperator<T> {
    public CheckedSquare(GeneralExpression<T> a, final TypeOperator<T> operator) {
        super(a, operator);
    }

    @Override
    protected String getOperator() {
        return " square ";
    }

    protected T operate(T x) {
        return operator.square(x);
    }
}