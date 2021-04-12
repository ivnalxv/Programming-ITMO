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
    protected T operate(T a, T b) {
        return operator.divide(a, b);
    }
}
