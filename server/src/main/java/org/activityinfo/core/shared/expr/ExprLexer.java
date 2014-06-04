package org.activityinfo.core.shared.expr;

import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import org.activityinfo.core.shared.util.StringUtil;

import java.util.List;

/**
 * Splits an expression string into a sequence of tokens
 */
public class ExprLexer extends UnmodifiableIterator<Token> {

    private String string;
    private int currentCharIndex;
    private int currentTokenStart = 0;


    private static final String VALID_OPERATORS = "+-/*";
    private Token previous;

    public ExprLexer(String string) {
        this.string = string;
    }

    /**
     * @return the current character within the string being processed
     */
    private char peekChar() {
        return string.charAt(currentCharIndex);
    }

    private char nextChar() {
        return string.charAt(currentCharIndex++);
    }

    /**
     * Adds the current char to the current token
     */
    private void consumeChar() {
        currentCharIndex++;
    }

    private Token finishToken(TokenType type) {
        Token token = new Token(type, currentTokenStart,
                string.substring(currentTokenStart, currentCharIndex));
        currentTokenStart = currentCharIndex;
        return token;
    }

    public List<Token> readAll() {
        List<Token> tokens = Lists.newArrayList();
        previous = null;
        while (!isEndOfInput()) {
            tokens.add(next());
        }
        return tokens;
    }

    public boolean isEndOfInput() {
        return currentCharIndex >= string.length();
    }

    @Override
    public boolean hasNext() {
        return !isEndOfInput();
    }

    @Override
    public Token next() {
        char c = nextChar();
        Token next = null;
        if (c == '(') {
            next = finishToken(TokenType.PAREN_START);

        } else if (c == ')') {
            next = finishToken(TokenType.PAREN_END);

        } else if (c == '{') {
            next = finishToken(TokenType.BRACE_START);

        } else if (c == '}') {
            next = finishToken(TokenType.BRACE_END);

        } else if (StringUtil.isWhitespace(c)) {
            next = readWhitespace();

        } else if (isNumberPart(c)) {
            next = readNumber();

        } else if (isOperator(c)) {
            next = finishToken(TokenType.OPERATOR);

        } else if (isSymbolStart(c)) {
            // right now we support only functions, maybe later we will need to support also variables.
            // However we should think twice since technically variable should be reference to indicator and expressed
            // via reference {i00001}
            TokenType type = previous != null && previous.getType() == TokenType.BRACE_START ? TokenType.SYMBOL : TokenType.FUNCTION;
            next = readSymbol(type);

        } else {
            throw new RuntimeException("Symbol '" + c + "' is not supported");
        }
        previous = next;
        return next;
    }

    private boolean isOperator(char c) {
        return VALID_OPERATORS.indexOf(c) != -1;
    }

    private boolean isSymbolStart(char c) {
        return c == '_' || Character.isLetter(c);
    }

    private boolean isSymbolChar(char c) {
        return c == '_' || StringUtil.isAlphabetic(c) || Character.isDigit(c);
    }

    private boolean isNumberPart(char c) {
        return Character.isDigit(c);
    }

    private Token readWhitespace() {
        while (!isEndOfInput() && StringUtil.isWhitespace(peekChar())) {
            consumeChar();
        }
        return finishToken(TokenType.WHITESPACE);
    }

    private Token readNumber() {
        while (!isEndOfInput() && isNumberPart(peekChar())) {
            consumeChar();
        }
        return finishToken(TokenType.NUMBER);
    }

    private Token readSymbol(TokenType type) {
        while (!isEndOfInput() && isSymbolChar(peekChar())) {
            consumeChar();
        }
        return finishToken(type);
    }

}
