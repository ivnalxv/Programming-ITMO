package expression.operator;

import expression.exceptions.DivideByZeroException;
import expression.exceptions.OperatorOverflowException;

public class IntegerOperator extends UncheckedIntegerOperator {

    public Integer abs(Integer a) {
        if (a == Integer.MIN_VALUE) {
            throw new OperatorOverflowException("Abs", Integer.toString(a));
        }
        return super.abs(a);
    }

    public Integer square(Integer a) {
        return multiply(a, a);
    }

    public Integer mod(Integer a, Integer b) {
        return super.mod(a, b);
    }

    public Integer add(Integer a, Integer b) {
        if (b > 0 && Integer.MAX_VALUE - b < a) {
            throw new OperatorOverflowException("Add", Integer.toString(a));
        } else if (b < 0 && Integer.MIN_VALUE - b > a) {
            throw new OperatorOverflowException("Add", Integer.toString(a));
        }
        return super.add(a, b);
    }

    public Integer subtract(Integer a, Integer b) {
        if (b > 0 && Integer.MIN_VALUE + b > a) {
            throw new OperatorOverflowException("Subtract", Integer.toString(a, b));
        } else if (b < 0 && Integer.MAX_VALUE + b < a) {
            throw new OperatorOverflowException("Subtract", Integer.toString(a, b));
        }
        return super.subtract(a, b);
    }

    public Integer divide(Integer a, Integer b) {
        if (a == Integer.MIN_VALUE && b == -1) {
            throw new OperatorOverflowException("Divide", Integer.toString(a));
        }
        return super.divide(a, b);
    }

    public Integer multiply(Integer a, Integer b) {
        if (a != 0 && b != 0) {
            if (a == Integer.MIN_VALUE && b == -1 || a == -1 && b == Integer.MIN_VALUE) {
                throw new OperatorOverflowException("Multiply", Integer.toString(a));
            } else if ((a == Integer.MIN_VALUE && b != 1) || (a != 1 && b == Integer.MIN_VALUE)) {
                throw new OperatorOverflowException("Multiply", Integer.toString(a));
            } else if (Integer.MAX_VALUE / abs(a) < abs(b)) {
                throw new OperatorOverflowException("Multiply", Integer.toString(a));
            }
        }
        return super.multiply(a, b);
    }
}