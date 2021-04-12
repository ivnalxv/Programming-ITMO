package expression.exceptions;

import java.util.Objects;

public class Const<T> implements GeneralExpression<T> {
    private T val;

    public Const(T val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) { return false; }
        Const<?> aConst = (Const<?>) object;
        return val == aConst.val;
    }

    @Override
    public int hashCode() {
        return Objects.hash(val);
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return val;
    }
}
