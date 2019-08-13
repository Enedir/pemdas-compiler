package br.ifmath.compiler.domain.grammar.terminal.function;

import br.ifmath.compiler.domain.grammar.terminal.VariableTerminal;

public abstract class Function extends VariableTerminal {

    public Function(String lexeme) {
        super(lexeme);
    }
}
