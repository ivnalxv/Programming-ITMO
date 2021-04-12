package expression.parser;

import expression.exceptions.*;
import expression.operator.TypeOperator;

import java.util.List;
import java.util.Map;

public class ExpressionParser<T> extends BaseParser {
    private String lastOperator;
    private static final int topLevel =  2; //3;
    private static final int primeLevel = 0;
    private final TypeOperator<T> modeOperator;

    private static final Map<String, Integer> priorities = Map.of(
            //"gcd", 3,
            //"lcm", 3,
            "+", 2,
            "-", 2,
            "*", 1,
            "/", 1
    );
    private static final Map<Character, String> firstCharToOperator = Map.of(
            //'g', "gcd",
            //'l', "lcm",
            '+', "+",
            '-', "-",
            '*', "*",
            '/', "/"
    );

    private static final List<String> UNARY_OPERATORS = List.of(
            //"abs", "square"
    );

    public ExpressionParser(TypeOperator<T> modeOperator) {
        this.modeOperator = modeOperator;
    }


    public GeneralExpression<T> parse(String expression) {
        setSource(new StringSource(expression));
        nextChar();
        lastOperator = null;
        final GeneralExpression<T> generalExpression = parseLevel(topLevel);
        if (ch != '\0') {
            throw error("Unexpected close bracket");
        }
        return generalExpression;
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
            if (priorities.get(lastOperator) != level) {
                break;
            }
            String prevOperator = lastOperator;
            lastOperator = null;
            expression = makeExpression(prevOperator, expression, parseLevel(level - 1));
            skipWhitespaces();
        }
        return expression;
    }

    private void getOperator() {
        StringBuilder stringBuilder = new StringBuilder();
        while (ch != '\0' && !priorities.containsKey(stringBuilder.toString()) && stringBuilder.length() <= 3) {
            stringBuilder.append(ch);
            nextChar();
        }
        if (!priorities.containsKey(stringBuilder.toString())) {
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
                return new CheckedNegate<>(getPrimeExpression(), modeOperator);
            }
        } else if (between('0', '9')) {
            return getConstExpression(false);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            while (between('a', 'z') || between('0', '9')) {
                stringBuilder.append(ch);
                nextChar();
            }
            if (UNARY_OPERATORS.contains(stringBuilder.toString())) {
                return makeExpression(stringBuilder.toString(), getPrimeExpression());
            } else if (checkVariable(stringBuilder.toString())) {
                return new Variable<>(stringBuilder.toString(), modeOperator);
            }
            throw error("Invalid variable");
        }
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
            return new Const<>(modeOperator.parse(stringBuilder.toString()));
        } catch (NumberFormatException e) {
            throw error("Invalid const expression");
        }
    }

    private GeneralExpression<T> makeExpression(String operator, GeneralExpression<T> a, GeneralExpression<T> b) {
        switch (operator) {
            case "+":
                return new CheckedAdd<>(a, b, modeOperator);
            case "-":
                return new CheckedSubtract<>(a, b, modeOperator);
            case "*":
                return new CheckedMultiply<>(a, b, modeOperator);
            case "/":
                return new CheckedDivide<>(a, b, modeOperator);
            default:
                throw error("Unsupported operator: " + operator);
        }
    }

    private GeneralExpression<T> makeExpression(String operator, GeneralExpression<T> a) {
        switch (operator) {
            case "neg":
                return new CheckedNegate<>(a, modeOperator);
            default:
                throw error("Unsupported operator: " + operator);
        }
    }

    private void skipWhitespaces() {
        while (Character.isWhitespace(ch)) {
            nextChar();
        }
    }
}
