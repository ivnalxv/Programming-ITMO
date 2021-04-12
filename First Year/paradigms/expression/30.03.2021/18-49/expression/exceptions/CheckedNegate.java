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
        return operator.negate(x);
    }
}
