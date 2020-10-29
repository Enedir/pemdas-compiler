package br.ifmath.compiler.domain.expertsystem.polynomial.classes;

import br.ifmath.compiler.infrastructure.input.Polynomial;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

public class Monomial extends Polynomial {

    public Monomial() {
    }

    public Monomial(String literal, Integer coefficient) {
        this.literal = literal;
        this.coefficient = coefficient;
    }

    public Monomial(String argument) {
        this.setAttributesFromString(argument);
    }

    public void setAttributesFromString(String argument) {
        if (StringUtil.match(argument.replace("-", ""), RegexPattern.VARIABLE_WITH_EXPONENT.toString())) {
            this.coefficient = this.getCoefficientFromString(argument.substring(0, argument.indexOf("^") - 1));
            this.literal = argument.substring(argument.indexOf("^") - 1);
        } else {
            String coefficient = StringUtil.removeNonNumericChars(argument);
            String literal = StringUtil.removeNumericChars(argument);
            if (argument.startsWith("-")) {
                coefficient = "-" + coefficient;
                literal = literal.replace("-", "");
            }
            this.coefficient = this.getCoefficientFromString(coefficient);
            this.literal = literal;
        }
    }

    private int getCoefficientFromString(String param) {
        if (param.equals(""))
            return 1;
        if (param.equals("-"))
            return -1;
        return Integer.parseInt(param);
    }

    public void setLiteralDegree(int newPower) {
        String alphabeticLabel = (this.getLiteralDegree() == 1) ? literal : this.literal.substring(0, this.literal.indexOf('^'));
        if (newPower == 0) {
            this.literal = "";
        } else if (newPower == 1) {
            this.literal = alphabeticLabel;
        } else {
            this.literal = alphabeticLabel + "^" + newPower;
        }
    }

    public int getLiteralDegree() {
        if (this.literal == null || this.literal.equals("")) {
            return 0;
        }

        String coefficient = StringUtil.removeNonNumericChars(this.literal);
        if (coefficient.equals("")) {
            return 1;
        }
        return Integer.parseInt(coefficient);
    }

    public String getLiteralVariable() {
        if (literal.equals(""))
            return "";
        return (literal.length() == 1) ? literal : literal.substring(0, literal.indexOf('^'));
    }

    @Override
    public String toString() {
        if ((this.literal == null || this.literal.equals("")) && this.coefficient == null)
            return "";
        else if (this.literal == null || this.literal.equals(""))
            return String.valueOf(this.coefficient);
        else if (this.coefficient == null)
            return this.literal;
        else {
            if (coefficient == 1)
                return literal;
            if (coefficient == -1)
                return "-" + literal;
            return coefficient + literal;
        }
    }
}
