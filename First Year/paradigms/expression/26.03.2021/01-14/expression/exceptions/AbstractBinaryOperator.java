
package expression.exceptions;

import expression.operator.TypeOperator;

import java.util.Objects;

abstract class AbstractBinaryOperator<T> implements GeneralExpression<T> {
    private GeneralExpression<T> a, b;
    protected final TypeOperator<T> operator;

    public AbstractBinaryOperator(GeneralExpression<T> a, GeneralExpression<T> b, final TypeOperator<T> operator) {
        this.a = a;
        this.b = b;
        this.operator = operator;
    }

    protected abstract String getOperator();
    protected abstract int getPriority();
    protected abstract T operate(T a, T b);
    protected abstract boolean isOrdered();

    @Override
    public T evaluate(T x, T y, T z) {
        return operate(a.evaluate(x, y, z), b.evaluate(x, y, z));
    }

    @Override
    public String toString() {
        return "(" + a + getOperator() + b + ')';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) { return false; }
        AbstractBinaryOperator obj = (AbstractBinaryOperator) object;
        return this.a.equals(obj.a) && this.b.equals(obj.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, getClass());
    }
}