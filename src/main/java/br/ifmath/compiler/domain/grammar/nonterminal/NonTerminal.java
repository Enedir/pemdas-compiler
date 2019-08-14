package br.ifmath.compiler.domain.grammar.nonterminal;

import br.ifmath.compiler.domain.grammar.GrammarSymbol;

import java.util.Objects;

/**
 *
 * @author alex_
 */
public abstract class NonTerminal extends GrammarSymbol implements INonTerminal {

    private final String name;

    public NonTerminal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.name);
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
        final NonTerminal other = (NonTerminal) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
}
