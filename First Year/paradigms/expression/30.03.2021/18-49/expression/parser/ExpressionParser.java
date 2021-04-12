package expression.parser;

import expression.exceptions.*;
import expression.operator.TypeOperator;

import java.util.Map;

public class ExpressionParser<T> extends BaseParser {
    private String lastOp;
    private static final int maxLevel = 2;
    private static final int minLevel = 0;
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
        lastOp = null;
        final GeneralExpression<T> finalExpression = parse(maxLevel);
        if (isEOF()) {
            return finalExpression;
        }
        throw error("Unexpected close bracket");
    }

    private GeneralExpression<T> parse(int level) {
        if (level == minLevel) {
            return getLowest();
        }
        GeneralExpression<T> exp = parse(level - 1);
        skipWhitespaces();
        while (lastOp != null || !isEOF() && ch != ')') {
            if (lastOp == null) {
                getOperator();
            }
            if (BINARY_PRIORITIES.get(lastOp) != level) {
                break;
            }
            String prevOp = lastOp;
            lastOp = null;
            exp = makeBinaryExpression(prevOp, exp, parse(level - 1));
            skipWhitespaces();
        }
        return exp;
    }

    private GeneralExpression<T> getLowest() {
        skipWhitespaces();
        if (ch == '(') {
            nextChar();
            GeneralExpression<T> exp = parse(maxLevel);
            expect(')');
            return exp;
        } else if (ch == '-') {
            nextChar();
            if (between('0', '9')) {
                return getConst(true);
            }
            return new CheckedNegate<>(getLowest(), mode);
        } else if (between('0', '9')) {
            return getConst(false);
        }

        StringBuilder sb = new StringBuilder();
        while (between('a', 'z') || between('0', '9')) {
            sb.append(ch);
            nextChar();
        }
        String thing = sb.toString();
        if (UNARY_PRIORITIES.containsKey(thing)) {
            return makeUnaryExpression(thing, getLowest());
        } else if (thing.equals("x") || thing.equals("y") || thing.equals("z")) {
            return new Variable<>(sb.toString(), mode);
        }
        throw error("Invalid variable: " + thing);
    }


    private GeneralExpression<T> getConst(boolean isNegative) {
        StringBuilder sb = new StringBuilder(isNegative ? "-" : "");
        while (between('0', '9')) {
            sb.append(ch);
            nextChar();
        }
        String cnst = sb.toString();
        checkConst(cnst);
        return new Const<>(mode.parse(cnst));
    }

    private void checkConst(String num) {
        try {
            mode.parse(num);
        } catch (NumberFormatException e) {
            throw error("Invalid const expression");
        }
    }

    private void getOperator() {
        StringBuilder sb = new StringBuilder();
        while (!isEOF() && !BINARY_PRIORITIES.containsKey(sb.toString()) && sb.length() <= 3) {
            sb.append(ch);
            nextChar();
        }
        if (!BINARY_PRIORITIES.containsKey(sb.toString())) {
            throw error("Expected operator");
        }
        lastOp = sb.toString();
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
