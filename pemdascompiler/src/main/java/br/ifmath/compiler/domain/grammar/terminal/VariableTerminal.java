package br.ifmath.compiler.domain.grammar.terminal;

public class VariableTerminal extends Terminal implements IVariableTerminal {

    private String value;

    public VariableTerminal(String lexeme) {
        super(lexeme);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
