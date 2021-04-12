package expression.operator;

import expression.exceptions.DivideByZeroException;

import java.math.BigInteger;

public class BigIntegerOperator implements TypeOperator<BigInteger> {
    @Override
    public BigInteger add(BigInteger a, BigInteger b) {
        return a.add(b);
    }

    @Override
    public BigInteger subtract(BigInteger a, BigInteger b) {
        return a.subtract(b);
    }

    @Override
    public BigInteger multiply(BigInteger a, BigInteger b) {
        return a.multiply(b);
    }

    @Override
    public BigInteger divide(BigInteger a, BigInteger b) {
        checkDivide(a, b);
        return a.divide(b);
    }

    @Override
    public BigInteger mod(BigInteger a, BigInteger b) {
        checkMod(a, b);
        return a.mod(b);
    }

    @Override
    public BigInteger negate(BigInteger x) {
        return x.negate();
    }

    @Override
    public BigInteger abs(BigInteger x) {
        return x.abs();
    }

    @Override
    public BigInteger square(BigInteger x) {
        return x.multiply(x);
    }

    @Override
    public BigInteger parse(String value) {
        return new BigInteger(value);
    }

    @Override
    public BigInteger parse(int value) {
        return BigInteger.valueOf(value);
    }

    private void checkMod(BigInteger a, BigInteger b) {
        if (b.add(b.abs()).equals(BigInteger.ZERO)) {
            throw new DivideByZeroException(a.toString() + " % " + b.toString());
        }
    }

    private void checkDivide(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            throw new DivideByZeroException(a.toString() + " / " + b.toString());
        }
    }
}