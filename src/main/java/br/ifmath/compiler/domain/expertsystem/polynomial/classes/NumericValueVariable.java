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

    public NumericValueVariable(String argument) {
        this.setAttributesFromString(argument);
    }

    public void setAttributesFromString(String argument) {
        if (StringUtil.match(argument.replace("-", ""), RegexPattern.VARIABLE_WITH_EXPONENT.toString())) {
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
        if (param.equals("-"))
            return -1;
        return Integer.parseInt(param);
    }

    public void setLabelPower(int newPower) {
        String alphabeticLabel = (this.getLabelPower() == 1) ? label : this.label.substring(0, this.label.indexOf('^'));
        if (newPower == 0) {
            this.label = "";
        } else if (newPower == 1) {
            this.label = alphabeticLabel;
        } else {
            this.label = alphabeticLabel + "^" + newPower;
        }
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

    public String getLabelVariable() {
        if (label.equals(""))
            return "";
        return (label.length() == 1) ? label : label.substring(0, label.indexOf('^'));
    }

    @Override
    public String toString() {
        if ((this.label == null || this.label.equals("")) && this.value == null)
            return "";
        else if (this.label == null || this.label.equals(""))
            return String.valueOf(this.value);
        else if (this.value == null)
            return this.label;
        else {
            if (value == 1)
                return label;
            if (value == -1)
                return "-" + label;
            return value + label;
        }
    }
}
