package br.ifmath.compiler.domain.grammar.nonterminal;

import br.ifmath.compiler.domain.compiler.Token;
import br.ifmath.compiler.domain.grammar.GrammarSymbol;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction4;
import br.ifmath.compiler.domain.grammar.terminal.comparison.*;

/**
 *
 * @author alex_
 */
public class C extends NonTerminal {

    private String address;

    public C() {
        super("C");
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public GrammarSymbol[] derivate(Token token) throws UnrecognizedStructureException {
        if (token.isInstanceOfAny(Equal.class)) {
            Equal equal = new Equal();

            return new GrammarSymbol[] {
                    new SemanticAction4(this, equal),
                    equal
            };
        }

        if (token.isInstanceOfAny(Different.class)) {
            Different different = new Different();

            return new GrammarSymbol[] {
                    new SemanticAction4(this, different),
                    different
            };
        }

        if (token.isInstanceOfAny(Lower.class)) {
            Lower lower = new Lower();

            return new GrammarSymbol[] {
                    new SemanticAction4(this, lower),
                    lower
            };
        }

        if (token.isInstanceOfAny(LowerOrEqual.class)) {
            LowerOrEqual lowerOrEqual = new LowerOrEqual();

            return new GrammarSymbol[] {
                    new SemanticAction4(this, lowerOrEqual),
                    lowerOrEqual
            };
        }

        if (token.isInstanceOfAny(Greater.class)) {
            Greater greater = new Greater();

            return new GrammarSymbol[] {
                    new SemanticAction4(this, greater),
                    greater
            };
        }

        if (token.isInstanceOfAny(GreaterOrEqual.class)) {
            GreaterOrEqual greaterOrEqual = new GreaterOrEqual();

            return new GrammarSymbol[] {
                    new SemanticAction4(this, greaterOrEqual),
                    greaterOrEqual
            };
        }

        throw new UnrecognizedStructureException(this, token);
    }

}