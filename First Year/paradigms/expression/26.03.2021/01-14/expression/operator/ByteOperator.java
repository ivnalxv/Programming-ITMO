package expression.operator;

import expression.exceptions.DivideByZeroException;

public class ByteOperator implements TypeOperator<Byte> {
    @Override
    public Byte add(Byte a, Byte b) {
        return (byte)(a + b);
    }

    @Override
    public Byte subtract(Byte a, Byte b) {
        return (byte)(a - b);
    }

    @Override
    public Byte multiply(Byte a, Byte b) {
        return (byte)(a * b);
    }

    @Override
    public Byte divide(Byte a, Byte b) {
        if (b.equals((byte)0)) {
            throw new DivideByZeroException(a.toString());
        }
        return (byte)(a / b);
    }

    @Override
    public Byte min(Byte a, Byte b) {
        return a < b ? a : b;
    }

    @Override
    public Byte max(Byte a, Byte b) {
        return a > b ? a : b;
    }

    @Override
    public Byte negate(Byte x) {
        return (byte)-x;
    }

    @Override
    public Byte parse(String value) {
        return Byte.parseByte(value);
    }

    @Override
    public Byte parse(int value) {
        return (byte)value;
    }
}