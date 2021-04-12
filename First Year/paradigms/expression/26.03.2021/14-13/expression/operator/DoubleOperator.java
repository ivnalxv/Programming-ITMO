package expression.operator;

public class DoubleOperator implements TypeOperator<Double> {
    @Override
    public Double add(Double a, Double b) {
        return a + b;
    }

    @Override
    public Double subtract(Double a, Double b) {
        return a - b;
    }

    @Override
    public Double multiply(Double a, Double b) {
        return a * b;
    }

    @Override
    public Double divide(Double a, Double b) {
        return a / b;
    }

    @Override
    public Double mod(Double a, Double b) {
        return a % b;
    }

    @Override
    public Double negate(Double x) {
        return -x;
    }

    @Override
    public Double abs(Double x) {
        return x > 0 ? x : -x;
    }

    @Override
    public Double square(Double x) {
        return x * x;
    }

    @Override
    public Double parse(String value) {
        return Double.parseDouble(value);
    }

    @Override
    public Double parse(int value) {
        return (double) value;
    }
}