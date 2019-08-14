/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.nonterminal;


import br.ifmath.compiler.domain.compiler.Token;
import br.ifmath.compiler.domain.grammar.GrammarSymbol;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction19;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction20;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction21;
import br.ifmath.compiler.domain.grammar.terminal.Semicolon;
import br.ifmath.compiler.domain.grammar.terminal.Success;
import br.ifmath.compiler.domain.grammar.terminal.comparison.*;
import br.ifmath.compiler.domain.grammar.terminal.operator.*;
import br.ifmath.compiler.domain.grammar.terminal.precedence.EndBracket;
import br.ifmath.compiler.domain.grammar.terminal.precedence.EndKey;
import br.ifmath.compiler.domain.grammar.terminal.precedence.EndParentheses;

/**
 *
 * @author alex_
 */
public class PL extends NonTerminal {

    private String address;
    private int position;
    private int level;
    private boolean value;
    private String operator;
    
    public PL() {
        super("PL");
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
        if (token.isInstanceOfAny(Pow.class)) {
            O o = new O();
            PL pl1 = new PL();
            
            return new GrammarSymbol[] {
                new SemanticAction20(o, this, pl1),
                pl1,
                o,
                new SemanticAction19(o, this, pl1),
                new Pow()
            };
        }
        
        if (token.isInstanceOfAny(
                Plus.class,
                Minus.class,
                Times.class,
                Fraction.class,
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
            return new GrammarSymbol[] { new SemanticAction21(this) };
        }
        
        throw new UnrecognizedStructureException(this, token);
    }
    
}
