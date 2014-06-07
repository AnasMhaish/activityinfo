package org.activityinfo.core.shared.expr.functions;

import org.activityinfo.core.shared.expr.ExprFunction;

public class ArithmeticFunctions {

    public static final ExprFunction BINARY_PLUS = new BinaryInfixFunction("+") {

        @Override
        public double applyReal(double x, double y) {
            return x + y;
        }
    };

    public static final ExprFunction DIVIDE = new BinaryInfixFunction("/") {

        @Override
        public double applyReal(double x, double y) {
            return x / y;
        }
    };

    public static final ExprFunction SQUARE_ROOT = new UnaryInfixFunction("sqrt") {

        @Override
        public double applyReal(double x) {
            return Math.sqrt(x);
        }
    };

    public static ExprFunction getUnaryInfix(String name) {
        if (name.equalsIgnoreCase("sqrt")) {
            return SQUARE_ROOT;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static ExprFunction getBinaryInfix(String name) {
        if (name.equals("+")) {
            return BINARY_PLUS;

        } else if (name.equals("/")) {
            return DIVIDE;

        } else {
            throw new IllegalArgumentException();
        }
    }
}
