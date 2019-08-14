package br.ifmath.compiler.domain.grammar.terminal.comparison;

import br.ifmath.compiler.domain.grammar.terminal.VariableTerminal;

public abstract class Comparison extends VariableTerminal {

    public Comparison(String lexeme) {
        super(lexeme);
    }

}
