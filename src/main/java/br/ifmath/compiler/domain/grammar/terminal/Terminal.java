package br.ifmath.compiler.domain.grammar.terminal;

import br.ifmath.compiler.domain.grammar.GrammarSymbol;
import java.util.Objects;

public abstract class Terminal  extends GrammarSymbol implements ITerminal {

    private final String lexeme;

    public Terminal(String lexeme) {
        this.lexeme = lexeme;
    }

    @Override
    public String getLexeme() {
        return lexeme;
    }

    @Override
    public boolean match(String lexeme) {
        return this.lexeme.equals(lexeme);
    }

    @Override
    public String toString() {
        return lexeme;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.lexeme);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Terminal other = (Terminal) obj;
        if (!Objects.equals(this.lexeme, other.lexeme)) {
            return false;
        }
        return true;
    }
}
