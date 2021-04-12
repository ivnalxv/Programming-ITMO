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
        return operator.multiply(a, b);
    }

}
