/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.nonterminal;


import br.ifmath.compiler.domain.compiler.Token;
import br.ifmath.compiler.domain.grammar.GrammarSymbol;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction1;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction3;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction4;
import br.ifmath.compiler.domain.grammar.terminal.function.*;
import br.ifmath.compiler.domain.grammar.terminal.id.Id;
import br.ifmath.compiler.domain.grammar.terminal.id.IdWithCoefficient;
import br.ifmath.compiler.domain.grammar.terminal.number.DecimalNumber;
import br.ifmath.compiler.domain.grammar.terminal.number.NaturalNumber;
import br.ifmath.compiler.domain.grammar.terminal.operator.Minus;
import br.ifmath.compiler.domain.grammar.terminal.operator.Plus;
import br.ifmath.compiler.domain.grammar.terminal.precedence.BeginBracket;
import br.ifmath.compiler.domain.grammar.terminal.precedence.BeginKey;
import br.ifmath.compiler.domain.grammar.terminal.precedence.BeginParentheses;

/**
 *
 * @author alex_
 */
public class E extends NonTerminal {
    
    private String parameter1;
    private String comparison;
    private String parameter2;
    
    public E() {
        super("E");
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public String getComparison() {
        return comparison;
    }

    public void setComparison(String comparison) {
        this.comparison = comparison;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    @Override
    public GrammarSymbol[] derivate(Token token) throws UnrecognizedStructureException {
        if (token.isInstanceOfAny(BeginParentheses.class,
                BeginBracket.class,
                BeginKey.class,
                Cosine.class,
                Sine.class,
                Tangent.class,
                SquareRoot.class,
                Root.class,
                Log.class,
                Log10.class,
                Id.class,
                IdWithCoefficient.class,
                NaturalNumber.class,
                DecimalNumber.class,
                Plus.class,
                Minus.class
            )
        ) {
            T t1 = new T();
            T t2 = new T();
            C c = new C();
            
            return new GrammarSymbol[] {
                new SemanticAction4(this, t1, t2, c),
                t2,
                new SemanticAction3(t2),
                c,
                t1,
                new SemanticAction1(t1)
            };
        }
        
        throw new UnrecognizedStructureException(this, token);
    }
    
}
