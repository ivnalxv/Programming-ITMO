package expression.operator;

import expression.exceptions.DivideByZeroException;

public class UncheckedIntegerOperator implements TypeOperator<Integer> {
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
    public Integer mod(Integer a, Integer b) {
        checkMod(a, b);
        return a % b;
    }

    @Override
    public Integer negate(Integer x) {
        return -x;
    }

    @Override
    public Integer abs(Integer x) {
        checkAbs(x);
        return x > 0 ? x : -x;
    }

    @Override
    public Integer square(Integer x) {
        checkSquare(x);
        return x*x;
    }

    @Override
    public Integer parse(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public Integer parse(int value) {
        return value;
    }

    protected void checkMod(int a, int b) {
        if (b == 0) {
            throw new DivideByZeroException("Divide by zero: " + a + " % " + b);
        }
    }

    protected void checkDivide(int a, int b) {
        if (b == 0) {
            throw new DivideByZeroException("Divide by zero: " + a + " / " + b);
        }
    }

    protected void checkAbs(int a) {

    }

    protected void checkSquare(int a) {

    }

    protected void checkAdd(int a, int b) {

    }

    protected void checkSubtract(int a, int b) {

    }

    protected void checkMultiply(int a, int b) {

    }

    protected static int abs(int x) {
        return x >= 0 ? x : -x;
    }

}
