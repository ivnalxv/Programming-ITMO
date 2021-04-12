package expression.exceptions;

public interface GeneralExpression<T> {
    T evaluate(T x, T y, T z);
}
