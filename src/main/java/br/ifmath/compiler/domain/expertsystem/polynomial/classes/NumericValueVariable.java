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

    public NumericValueVariable findByLabel(String value) {
        if (value.equals(label)) {
            return this;
        }
        return null;
    }

    public void setAttributesFromString(String argument) {
        if (StringUtil.match(argument, RegexPattern.VARIABLE_AND_COEFICIENT.toString())) {
            this.value = Integer.parseInt(argument.substring(0, argument.indexOf("^") - 1));
            this.label = argument.substring(argument.indexOf("^") - 1);
        } else {
            this.value = Integer.parseInt(StringUtil.removeNonNumericChars(argument));
            this.label = StringUtil.removeNumericChars(argument);
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

}
