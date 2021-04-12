package expression.operator;

import expression.exceptions.DivideByZeroException;

public class UncheckedIntegerOperator implements TypeOperator<Integer> {
    @Override
    public Integer add(Integer a, Integer b) {
        return a + b;
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        return a - b;
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
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
        return x > 0 ? x : -x;
    }

    @Override
    public Integer square(Integer x) {
        return x*x;
    }

    @Override
    public Integer parse(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public Integer parse(int value) {
        return (Integer) value;
    }

    private void checkMod(int a, int b) {
        if (b == 0) {
            throw new DivideByZeroException("Divide by zero: " + a + " % " + b);
        }
    }

    private void checkDivide(int a, int b) {
        if (b == 0) {
            throw new DivideByZeroException("Divide by zero: " + a + " / " + b);
        }
    }

}
