/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.nonterminal;


import br.ifmath.compiler.domain.compiler.Token;
import br.ifmath.compiler.domain.grammar.GrammarSymbol;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction3;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction4;
import br.ifmath.compiler.domain.grammar.terminal.Success;
import br.ifmath.compiler.domain.grammar.terminal.comparison.Different;
import br.ifmath.compiler.domain.grammar.terminal.comparison.Equal;
import br.ifmath.compiler.domain.grammar.terminal.comparison.Greater;
import br.ifmath.compiler.domain.grammar.terminal.comparison.GreaterOrEqual;
import br.ifmath.compiler.domain.grammar.terminal.comparison.Lower;
import br.ifmath.compiler.domain.grammar.terminal.comparison.LowerOrEqual;

/**
 *
 * @author alex_
 */
public class EL extends NonTerminal {

    private String parameter;
    private String comparison;
    
    public EL() {
        super("EL");
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getComparison() {
        return comparison;
    }

    public void setComparison(String comparison) {
        this.comparison = comparison;
    }
    
    @Override
    public GrammarSymbol[] derivate(Token token) throws UnrecognizedStructureException {
        if (token.isInstanceOfAny(
            Equal.class,
            Different.class,
            Greater.class,
            GreaterOrEqual.class,
            Lower.class,
            LowerOrEqual.class
        )) {
            C c = new C();
            T t = new T();
            
            return new GrammarSymbol[] {
                new SemanticAction4(this, t, c),
                t,
                c,
                new SemanticAction3(t),
            };
        }
        
        if (token.isInstanceOfAny(Success.class)) {
            return new GrammarSymbol[] { };
        }
        
        throw new UnrecognizedStructureException(this, token);
    }
    
}
