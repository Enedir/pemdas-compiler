package br.ifmath.compiler.domain.grammar.terminal;

public interface IVariableTerminal extends ITerminal {

    String getValue();

    void setValue(String value);
}
