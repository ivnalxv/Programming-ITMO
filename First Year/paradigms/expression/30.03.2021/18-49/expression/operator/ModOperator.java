package expression.operator;

import expression.exceptions.DivideByZeroException;

public class ModOperator implements TypeOperator<Integer> {
    private final int[] rev = new int[1009];

    public ModOperator() {
        rev[1] = 1;
        for (int i = 2; i < 1009; ++i) {
            rev[i] = (1009 - (1009 / i) * rev[1009 % i] % 1009) % 1009;
        }
    }

    private Integer mod(int a) {
        return (a % 1009 + 1009) % 1009;
    }

    @Override
    public Integer add(Integer a, Integer b) {
        return mod(a + b);
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        return mod(a - b);
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        return mod(a * b);
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        if (b == 0) {
            throw new DivideByZeroException("Divide by zero: " + a + " / " + b);
        }
        return mod(a * rev[mod(b)]);
    }

    @Override
    public Integer mod(Integer a, Integer b) {
        checkMod(mod(a), mod(b));
        return mod(a % b);
    }

    @Override
    public Integer negate(Integer x) {
        return mod(-x);
    }

    @Override
    public Integer abs(Integer x) {
        return mod(x > 0 ? x : -x);
    }

    @Override
    public Integer square(Integer x) {
        return mod(x * x);
    }

    @Override
    public Integer parse(String value) {
        return mod(Integer.parseInt(value));
    }

    @Override
    public Integer parse(int value) {
        return mod(value);
    }

    private void checkMod(int a, int b) {
        if (b == 0) {
            throw new DivideByZeroException("Divide by zero: " + a + " % " + b);
        }
    }
}
