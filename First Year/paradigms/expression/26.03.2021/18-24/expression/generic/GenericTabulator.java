package expression.generic;


import expression.exceptions.ExpressionException;
import expression.exceptions.GeneralExpression;
import expression.operator.*;
import expression.parser.ExpressionParser;


import java.util.Map;

public class GenericTabulator implements Tabulator {
    private final Map<String, TypeOperator<?>> MODES = Map.of(
            "i", new IntegerOperator(),
            "d", new DoubleOperator(),
            "bi", new BigIntegerOperator(),
            "u", new UncheckedIntegerOperator(),
            "b", new ByteOperator(),
            "p", new ModOperator()
    );

    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) {
        if (MODES.containsKey(mode)) {
            return getTable(MODES.get(mode), expression, x1, x2, y1, y2, z1, z2);
        }
        throw new ExpressionException("Unknown mode: " + mode);
    }

    private <T> Object[][][] getTable(TypeOperator<T> operator, String expression, int x1, int x2, int y1, int y2, int z1, int z2) {
        GeneralExpression<T> exp = (new ExpressionParser<>(operator)).parse(expression);
        Object[][][] table = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];

        for (int x = 0; x <= x2 - x1; x++) {
            for (int y = 0; y <= y2 - y1; y++) {
                for (int z = 0; z <= z2 - z1; z++) {
                    try {
                        table[x][y][z] = exp.evaluate(
                                operator.parse(x + x1),
                                operator.parse(y + y1),
                                operator.parse(z + z1)
                        );
                    } catch (ExpressionException ignored) {}

                }
            }
        }

        return table;
    }
}