/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.nonterminal;

import br.ifmath.compiler.domain.compiler.Token;
import br.ifmath.compiler.domain.grammar.GrammarSymbol;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction14;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction15;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction16;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction17;
import br.ifmath.compiler.domain.grammar.terminal.Semicolon;
import br.ifmath.compiler.domain.grammar.terminal.Success;
import br.ifmath.compiler.domain.grammar.terminal.comparison.*;
import br.ifmath.compiler.domain.grammar.terminal.operator.Fraction;
import br.ifmath.compiler.domain.grammar.terminal.operator.Minus;
import br.ifmath.compiler.domain.grammar.terminal.operator.Plus;
import br.ifmath.compiler.domain.grammar.terminal.operator.Times;
import br.ifmath.compiler.domain.grammar.terminal.precedence.EndBracket;
import br.ifmath.compiler.domain.grammar.terminal.precedence.EndKey;
import br.ifmath.compiler.domain.grammar.terminal.precedence.EndParentheses;

/**
 *
 * @author alex_
 */
public class ML extends NonTerminal {

    private String address;
    private int position;
    private int level;
    private boolean value;
    private String operator;
    
    public ML() {
        super("ML");
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

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public GrammarSymbol[] derivate(Token token) throws UnrecognizedStructureException {
        if (token.isInstanceOfAny(Times.class)) {
            P p = new P();
            ML pl1 = new ML();
            
            return new GrammarSymbol[] {
                new SemanticAction15(p, this, pl1),
                pl1,
                p,
                new SemanticAction14(p, this, pl1),
                new Times()
            };
        }
        
        if (token.isInstanceOfAny(Fraction.class)) {
            P p = new P();
            ML ml1 = new ML();
            
            return new GrammarSymbol[] {
                new SemanticAction16(p, this, ml1),
                ml1,
                p,
                new SemanticAction14(p, this, ml1),
                new Fraction()
            };
        }
        
        if (token.isInstanceOfAny(
                Plus.class,
                Minus.class,
                EndParentheses.class,
                EndBracket.class,
                EndKey.class,
                Equal.class,
                Different.class,
                Greater.class,
                GreaterOrEqual.class,
                Lower.class,
                LowerOrEqual.class,
                Semicolon.class,
                Success.class
            )
        ) {
            return new GrammarSymbol[] { new SemanticAction17(this) };
        }
        
        throw new UnrecognizedStructureException(this, token);
    }
    
}
