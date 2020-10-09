package br.ifmath.compiler.infrastructure.input;

public abstract class Polynomial {

    protected String literal;
    protected Integer coefficient;

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    public Integer getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Integer coefficient) {
        this.coefficient = coefficient;
    }


}
