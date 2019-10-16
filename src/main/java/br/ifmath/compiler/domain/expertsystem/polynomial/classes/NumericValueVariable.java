package br.ifmath.compiler.domain.expertsystem.polynomial.classes;

import br.ifmath.compiler.infrastructure.input.ValueVariable;

public class NumericValueVariable extends ValueVariable {

    public NumericValueVariable() {
    }

    public NumericValueVariable(String label, Integer value) {
        this.label = label;
        this.value = value;
    }

    public NumericValueVariable findByLabel(String value) {
        if (value == label) {
            return this;
        }
        return null;
    }

}
