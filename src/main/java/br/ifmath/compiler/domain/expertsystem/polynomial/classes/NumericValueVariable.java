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
            this.value = this.getValueFromString(StringUtil.removeNonNumericChars(argument));
            this.label = StringUtil.removeNumericChars(argument);
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
