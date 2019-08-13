package br.ifmath.compiler.domain.grammar.terminal;

public interface ITerminal {

    public String getLexeme();

    public boolean match(String lexeme);
}
