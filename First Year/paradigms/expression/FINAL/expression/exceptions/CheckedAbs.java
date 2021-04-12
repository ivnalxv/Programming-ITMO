package expression.exceptions;

import expression.operator.TypeOperator;

public class CheckedAbs<T> extends AbstractUnaryOperator<T> {
    public CheckedAbs(GeneralExpression<T> a, final TypeOperator<T> operator) {
        super(a, operator);
    }

    @Override
    protected String getOperator() {
        return " abs ";
    }

    protected T operate(T x) {
        return operator.abs(x);
    }
}
