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
    public Double min(Double a, Double b) {
        return Double.min(a, b);
    }

    @Override
    public Double max(Double a, Double b) {
        return Double.max(a, b);
    }

    @Override
    public Double negate(Double x) {
        return -x;
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