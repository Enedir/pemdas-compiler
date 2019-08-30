package br.ifmath.compiler.domain.expertsystem.polynomial.classes;

public class NumericValueVariable {

    private String label;
    private Integer value;

    public NumericValueVariable(String label, Integer value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }


}
