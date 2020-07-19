package br.ifmath.compiler.domain.expertsystem.polynomial.classes;

import br.ifmath.compiler.infrastructure.input.ValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

public class NumericValueVariable extends ValueVariable {

    public NumericValueVariable() {
    }

    public NumericValueVariable(String label, Integer value) {
        this.label = label;
        this.value = value;
    }

    public void setAttributesFromString(String argument) {
        if (StringUtil.match(argument, RegexPattern.VARIABLE_WITH_EXPOENT.toString())) {
            this.value = this.getValueFromString(argument.substring(0, argument.indexOf("^") - 1));
            this.label = argument.substring(argument.indexOf("^") - 1);
        } else {
            String value = StringUtil.removeNonNumericChars(argument);
            String label = StringUtil.removeNumericChars(argument);
            if (argument.startsWith("-")) {
                value = "-" + value;
                label = label.replace("-", "");
            }
            this.value = this.getValueFromString(value);
            this.label = label;
        }
    }

    private int getValueFromString(String param) {
        if (param.equals(""))
            return 1;
        return Integer.parseInt(param);
    }

    public int getLabelPower() {
        if (this.label == null || this.label.equals("")) {
            return 0;
        }

        String value = StringUtil.removeNonNumericChars(this.label);
        if (value.equals("")) {
            return 1;
        }
        return Integer.parseInt(value);
    }

    public void labelExponentSum(int exponent) {
        int newExponent = this.getLabelPower() + exponent;
        this.label = label.substring(0, label.indexOf('^') + 1) + newExponent;
    }

    @Override
    public String toString() {
        if (this.label == null)
            return String.valueOf(this.value);
        else if (this.value == null)
            return this.label;
        else
            return value + label;
    }
}
