package expression.exceptions;

import expression.operator.TypeOperator;

public class CheckedMod<T> extends AbstractBinaryOperator<T> {
    public CheckedMod(GeneralExpression<T> a, GeneralExpression<T> b, final TypeOperator<T> operator) {
        super(a, b, operator);
    }

    @Override
    protected String getOperator() {
        return " mod ";
    }

    @Override
    protected T operate(T a, T b) {
        return operator.mod(a, b);
    }
}
