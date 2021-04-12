package expression.operator;

import expression.exceptions.DivideByZeroException;
import expression.exceptions.OperatorOverflowException;

public class IntegerOperator implements TypeOperator<Integer> {
    @Override
    public Integer add(Integer a, Integer b) {
        checkAdd(a, b);
        return a + b;
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        checkSubtract(a, b);
        return a - b;
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        checkMultiply(a, b);
        return a * b;
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        checkDivide(a, b);
        return a / b;
    }

    @Override
    public Integer min(Integer a, Integer b) {
        return Integer.min(a, b);
    }

    @Override
    public Integer max(Integer a, Integer b) {
        return Integer.max(a, b);
    }

    @Override
    public Integer negate(Integer x) {
        return -x;
    }

    @Override
    public Integer parse(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public Integer parse(int value) {
        return value;
    }


    private void checkAdd(int a, int b) {
        if (b > 0 && Integer.MAX_VALUE - b < a) {
            throw new OperatorOverflowException("Add", Integer.toString(a));
        } else if (b < 0 && Integer.MIN_VALUE - b > a) {
            throw new OperatorOverflowException("Add", Integer.toString(a));
        }
    }

    private void checkSubtract(int a, int b) {
        if (b > 0 && Integer.MIN_VALUE + b > a) {
            throw new OperatorOverflowException("Subtract", Integer.toString(a, b));
        } else if (b < 0 && Integer.MAX_VALUE + b < a) {
            throw new OperatorOverflowException("Subtract", Integer.toString(a, b));
        }
    }

    private void checkDivide(int a, int b) {
        if (a == Integer.MIN_VALUE && b == -1) {
            throw new OperatorOverflowException("Divide", Integer.toString(a));
        }
        if (b == 0) {
            throw new DivideByZeroException("Divide by zero: " + a + " / " + b);
        }
    }

    public void checkMultiply(int a, int b) {
        if (a != 0 && b != 0) {
            if (a == Integer.MIN_VALUE && b == -1 || a == -1 && b == Integer.MIN_VALUE) {
                throw new OperatorOverflowException("Multiply", Integer.toString(a));
            } else if (a * b == Integer.MIN_VALUE && Integer.MIN_VALUE / b == a) {
                return;
            } else if ((a == Integer.MIN_VALUE && b != 1) || (a != 1 && b == Integer.MIN_VALUE)) {
                throw new OperatorOverflowException("Multiply", Integer.toString(a));
            } else if (Integer.MAX_VALUE / abs(a) < abs(b)) {
                throw new OperatorOverflowException("Multiply", Integer.toString(a));
            }
        }
    }

    private static int abs(int x) {
        return x >= 0 ? x : -x;
    }
}