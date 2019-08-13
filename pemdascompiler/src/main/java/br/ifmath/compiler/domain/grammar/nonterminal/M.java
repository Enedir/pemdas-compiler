/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.nonterminal;

import br.ifmath.compiler.domain.compiler.Token;
import br.ifmath.compiler.domain.grammar.GrammarSymbol;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction11;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction12;
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
public class M extends NonTerminal {

    private String address;
    private int position;
    private int level;
    
    public M() {
        super("M");
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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
            P p = new P();
            ML ml = new ML();
            
            return new GrammarSymbol[] {
                new SemanticAction12(p, this, ml),
                ml,
                p,
                new SemanticAction11(p, this, ml)
            };
        }
        
        throw new UnrecognizedStructureException(this, token);
    }
    
}
