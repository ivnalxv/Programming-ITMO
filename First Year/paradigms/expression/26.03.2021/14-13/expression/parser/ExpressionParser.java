package expression.parser;

import expression.exceptions.*;
import expression.operator.TypeOperator;

import java.util.Map;

public class ExpressionParser<T> extends BaseParser {
    private String lastOperator;
    private static final int topLevel = 2;
    private static final int primeLevel = 0;
    private final TypeOperator<T> mode;

    private static final Map<String, Integer> BINARY_PRIORITIES = Map.of(
            "+", 2,
            "-", 2,
            "*", 1,
            "/", 1,
            "mod", 1
    );

    private static final Map<String, Integer> UNARY_PRIORITIES = Map.of(
            "abs", 1,
            "square", 1
    );

    public ExpressionParser(TypeOperator<T> mode) {
        this.mode = mode;
    }


    public GeneralExpression<T> parse(String expression) {
        setSource(new StringSource(expression));
        nextChar();
        lastOperator = null;
        final GeneralExpression<T> finalExpression = parseLevel(topLevel);
        if (isEOF()) {
            return finalExpression;
        }
        throw error("Unexpected close bracket");
    }

    private GeneralExpression<T> parseLevel(int level) {
        if (level == primeLevel) {
            return getPrimeExpression();
        }
        GeneralExpression<T> expression = parseLevel(level - 1);
        skipWhitespaces();
        while (lastOperator != null || ch != '\0' && ch != ')') {
            if (lastOperator == null) {
                getOperator();
            }
            if (BINARY_PRIORITIES.get(lastOperator) != level) {
                break;
            }
            String prevOperator = lastOperator;
            lastOperator = null;
            expression = makeBinaryExpression(prevOperator, expression, parseLevel(level - 1));
            skipWhitespaces();
        }
        return expression;
    }

    private void getOperator() {
        StringBuilder stringBuilder = new StringBuilder();
        while (ch != '\0' && !BINARY_PRIORITIES.containsKey(stringBuilder.toString()) && stringBuilder.length() <= 3) {
            stringBuilder.append(ch);
            nextChar();
        }
        if (!BINARY_PRIORITIES.containsKey(stringBuilder.toString())) {
            throw error("Expected operator");
        }
        lastOperator = stringBuilder.toString();
    }

    private GeneralExpression<T> getPrimeExpression() {
        skipWhitespaces();
        if (ch == '(') {
            nextChar();
            GeneralExpression<T> expression = parseLevel(topLevel);
            expect(')');
            return expression;
        } else if (ch == '-') {
            nextChar();
            if (between('0', '9')) {
                return getConstExpression(true);
            } else {
                return new CheckedNegate<>(getPrimeExpression(), mode);
            }
        } else if (between('0', '9')) {
            return getConstExpression(false);
        }

        StringBuilder stringBuilder = new StringBuilder();
        while (between('a', 'z') || between('0', '9')) {
            stringBuilder.append(ch);
            nextChar();
        }
        if (UNARY_PRIORITIES.containsKey(stringBuilder.toString())) {
            return makeUnaryExpression(stringBuilder.toString(), getPrimeExpression());
        } else if (checkVariable(stringBuilder.toString())) {
            return new Variable<>(stringBuilder.toString(), mode);
        }
        throw error("Invalid variable");
    }

    private boolean checkVariable(String var) {
        return var.length() == 1 && var.charAt(0) >= 'x' && var.charAt(0) <= 'z';
    }


    private GeneralExpression<T> getConstExpression(boolean isNegative) {
        StringBuilder stringBuilder = new StringBuilder(isNegative ? "-" : "");
        while (between('0', '9')) {
            stringBuilder.append(ch);
            nextChar();
        }
        try {
            return new Const<>(mode.parse(stringBuilder.toString()));
        } catch (NumberFormatException e) {
            throw error("Invalid const expression");
        }
    }

    private GeneralExpression<T> makeBinaryExpression(String operator, GeneralExpression<T> a, GeneralExpression<T> b) {
        return switch (operator) {
            case "+" -> new CheckedAdd<>(a, b, mode);
            case "-" -> new CheckedSubtract<>(a, b, mode);
            case "*" -> new CheckedMultiply<>(a, b, mode);
            case "/" -> new CheckedDivide<>(a, b, mode);
            case "mod" -> new CheckedMod<>(a, b, mode);
            default -> throw error("Unsupported operator: " + operator);
        };
    }

    private GeneralExpression<T> makeUnaryExpression(String operator, GeneralExpression<T> a) {
        return switch (operator) {
            case "abs" -> new CheckedAbs<>(a, mode);
            case "square" -> new CheckedSquare<>(a, mode);
            default -> throw error("Unsupported operator: " + operator);
        };
    }

    private void skipWhitespaces() {
        while (Character.isWhitespace(ch)) {
            nextChar();
        }
    }
}
