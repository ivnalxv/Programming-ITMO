package expression.exceptions;

import expression.operator.TypeOperator;

import java.util.Objects;

abstract class AbstractUnaryOperator<T> implements GeneralExpression<T> {
    protected GeneralExpression<T> a;
    protected final TypeOperator<T> operator;

    public AbstractUnaryOperator(GeneralExpression<T> a, final TypeOperator<T> operator) {
        this.a = a;
        this.operator = operator;
    }

    protected abstract String getOperator();
    protected abstract T operate(T a);


    @Override
    public T evaluate(T x, T y, T z) {
        return operate(a.evaluate(x, y, z));
    }

    @Override
    public String toString() {
        return getOperator() + "(" + a + ")";
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) { return false; }
        AbstractUnaryOperator<?> obj = (AbstractUnaryOperator<?>) object;
        return this.a.equals(obj.a);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, getClass());
    }
}
