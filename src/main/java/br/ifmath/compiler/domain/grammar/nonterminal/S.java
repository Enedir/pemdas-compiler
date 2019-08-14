/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.nonterminal;

import br.ifmath.compiler.domain.compiler.Token;
import br.ifmath.compiler.domain.grammar.GrammarSymbol;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction24;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction25;
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
public class S extends NonTerminal {

    private boolean value;
    
    public S() {
        super("S");
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public GrammarSymbol[] derivate(Token token) throws UnrecognizedStructureException {
        if (token.isInstanceOfAny(Plus.class)) {
            return new GrammarSymbol[] {
                new SemanticAction24(this),
                new Plus()
            };
        }
        
        if (token.isInstanceOfAny(Minus.class)) {
            return new GrammarSymbol[] {
                new SemanticAction25(this),
                new Minus()
            };
        }
        
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
                DecimalNumber.class
            )
        ) {
            return new GrammarSymbol[] { new SemanticAction24(this) };
        }
        
        throw new UnrecognizedStructureException(this, token);
    }
    
}
