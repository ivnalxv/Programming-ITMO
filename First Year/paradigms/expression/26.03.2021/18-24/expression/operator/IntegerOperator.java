package expression.operator;

import expression.exceptions.DivideByZeroException;
import expression.exceptions.OperatorOverflowException;

public class IntegerOperator extends UncheckedIntegerOperator {
    @Override
    protected void checkAbs(int a) {
        if (a == Integer.MIN_VALUE) {
            throw new OperatorOverflowException("Abs", Integer.toString(a));
        }
    }

    @Override
    protected void checkSquare(int a) {
        checkMultiply(a, a);
    }

    @Override
    protected void checkMod(int a, int b) {
        if (b == 0) {
            throw new DivideByZeroException("Divide by zero: " + a + " % " + b);
        }
    }

    @Override
    protected void checkAdd(int a, int b) {
        if (b > 0 && Integer.MAX_VALUE - b < a) {
            throw new OperatorOverflowException("Add", Integer.toString(a));
        } else if (b < 0 && Integer.MIN_VALUE - b > a) {
            throw new OperatorOverflowException("Add", Integer.toString(a));
        }
    }

    @Override
    protected void checkSubtract(int a, int b) {
        if (b > 0 && Integer.MIN_VALUE + b > a) {
            throw new OperatorOverflowException("Subtract", Integer.toString(a, b));
        } else if (b < 0 && Integer.MAX_VALUE + b < a) {
            throw new OperatorOverflowException("Subtract", Integer.toString(a, b));
        }
    }

    @Override
    protected void checkDivide(int a, int b) {
        if (a == Integer.MIN_VALUE && b == -1) {
            throw new OperatorOverflowException("Divide", Integer.toString(a));
        }
        if (b == 0) {
            throw new DivideByZeroException("Divide by zero: " + a + " / " + b);
        }
    }

    @Override
    protected void checkMultiply(int a, int b) {
        if (a != 0 && b != 0) {
            if (a == Integer.MIN_VALUE && b == -1 || a == -1 && b == Integer.MIN_VALUE) {
                throw new OperatorOverflowException("Multiply", Integer.toString(a));
            } else if ((a == Integer.MIN_VALUE && b != 1) || (a != 1 && b == Integer.MIN_VALUE)) {
                throw new OperatorOverflowException("Multiply", Integer.toString(a));
            } else if (Integer.MAX_VALUE / abs(a) < abs(b)) {
                throw new OperatorOverflowException("Multiply", Integer.toString(a));
            }
        }
    }
}