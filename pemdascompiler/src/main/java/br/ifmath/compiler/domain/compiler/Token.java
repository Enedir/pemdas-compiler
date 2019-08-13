package br.ifmath.compiler.domain.compiler;

import br.ifmath.compiler.domain.grammar.terminal.Terminal;
import br.ifmath.compiler.domain.grammar.terminal.id.Id;
import br.ifmath.compiler.domain.grammar.terminal.id.IdWithCoefficient;
import br.ifmath.compiler.domain.grammar.terminal.number.DecimalNumber;
import br.ifmath.compiler.domain.grammar.terminal.number.NaturalNumber;
import br.ifmath.compiler.infrastructure.compiler.iface.ISymbolTable;


import java.util.Objects;

/**
 * It represents a token. A token is a recognized lexeme's pattern of a expression
 *
 * @author alexjravila
 */
public class Token {

    private Terminal terminal;
    private Object value;

    public Token() {
    }

    public Token(Terminal name) {
        this.terminal = name;
    }

    public Token(Terminal terminal, Object value) {
        this.terminal = terminal;
        this.value = value;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isInstanceOfAny(Class<? extends Terminal>... classes) {
        for (Class<? extends Terminal> clazz : classes) {
            if (clazz.isInstance(this.terminal))
                return true;
        }

        return false;
    }

    public String getLexeme(ISymbolTable symbolTable) {
        if (this.isInstanceOfAny(Id.class, IdWithCoefficient.class))
            return symbolTable.get((int) this.getValue()).getValue();

        if (this.isInstanceOfAny(NaturalNumber.class, DecimalNumber.class))
            return (String) this.getValue();

        return this.getTerminal().getLexeme();
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final Token other = (Token) obj;
        if (!Objects.equals(this.terminal, other.terminal)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (value == null)
            return "<" + terminal.toString() + ">";

        return "<" + terminal.toString() + ", " + value + ">";
    }
}
