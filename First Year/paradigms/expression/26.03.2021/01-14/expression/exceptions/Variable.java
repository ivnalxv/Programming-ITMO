package expression.exceptions;

import expression.operator.TypeOperator;

import java.util.Objects;

public class Variable<T> implements GeneralExpression<T> {
    private String var;
    protected final TypeOperator<T> operator;

    public Variable(String var, final TypeOperator<T> operator) {
        this.var = var;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return var;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) { return false; }
        Variable variable = (Variable) object;
        return Objects.equals(var, variable.var);
    }

    @Override
    public int hashCode() {
        return Objects.hash(var);
    }

    @Override
    public T evaluate(T x, T y, T z) {
        if (var.equals("x")) {
            return x;
        }
        if (var.equals("y")) {
            return y;
        }
        if (var.equals("z")) {
            return z;
        }
        throw new ExpressionException("Unexpected variable " + var);
    }
}
