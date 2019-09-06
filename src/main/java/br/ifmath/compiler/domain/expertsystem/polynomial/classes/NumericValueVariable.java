package br.ifmath.compiler.domain.expertsystem.polynomial.classes;

public class NumericValueVariable {

    private String label;
    private Integer value;

    public NumericValueVariable() {

    }

    public NumericValueVariable(String label, Integer value) {
        this.label = label;
        this.value = value;
    }

    public NumericValueVariable(String label) {
        this.label = label;
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

    public NumericValueVariable findByLabel(String value) {
        if (value == label) {
            return this;
        }
        return null;
    }

    public void addValue(Integer valor){
        this.value+=valor;
    }


}
