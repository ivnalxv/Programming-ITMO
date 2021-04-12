package expression.parser;


public class

BaseParser {
    protected final char EOF = '\0';
    protected Source source;
    protected char ch;

    protected void setSource(Source source) {
        this.source = source;
    }

    protected void nextChar() {
        ch = source.hasNext() ? source.next() : EOF;
    }

    protected boolean test(char expected) {
        if (ch == expected) {
            nextChar();
            return true;
        }
        return false;
    }

    protected boolean test(final String value) {
        for (char c : value.toCharArray()) {
            if(!test(c)) {
                return false;
            }
        }
        return true;
    }

    protected void expect(final char c) {
        if (ch != c) {
            throw error("expected '" + c + "', found '" + ch + "'");
        }
        nextChar();
    }

    protected boolean isEOF() {
        return ch == EOF;
    }

    protected boolean between(final char from, final char to) {
        return from <= ch && ch <= to;
    }

    protected ParserException error(final String message) {
        return source.error(message);
    }
}
