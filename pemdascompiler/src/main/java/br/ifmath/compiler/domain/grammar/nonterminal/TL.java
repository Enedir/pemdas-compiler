/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.nonterminal;


import br.ifmath.compiler.domain.compiler.Token;
import br.ifmath.compiler.domain.grammar.GrammarSymbol;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction10;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction7;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction8;
import br.ifmath.compiler.domain.grammar.semanticaction.SemanticAction9;
import br.ifmath.compiler.domain.grammar.terminal.Semicolon;
import br.ifmath.compiler.domain.grammar.terminal.Success;
import br.ifmath.compiler.domain.grammar.terminal.comparison.*;
import br.ifmath.compiler.domain.grammar.terminal.operator.Minus;
import br.ifmath.compiler.domain.grammar.terminal.operator.Plus;
import br.ifmath.compiler.domain.grammar.terminal.precedence.EndBracket;
import br.ifmath.compiler.domain.grammar.terminal.precedence.EndKey;
import br.ifmath.compiler.domain.grammar.terminal.precedence.EndParentheses;

/**
 *
 * @author alex_
 */
public class TL extends NonTerminal {

    private String address;
    private int position;
    private int level;
    private boolean value;
    private String operator;
    
    public TL() {
        super("TL");
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
        if (token.isInstanceOfAny(Plus.class)) {
            M m = new M();
            TL tl1 = new TL();
            
            return new GrammarSymbol[] {
                new SemanticAction8(m, this, tl1),
                tl1,
                m,
                new SemanticAction7(m, this, tl1),
                new Plus()
            };
        }
        
        if (token.isInstanceOfAny(Minus.class)) {
            M m = new M();
            TL tl1 = new TL();
            
            return new GrammarSymbol[] {
                new SemanticAction9(m, this, tl1),
                tl1,
                m,
                new SemanticAction7(m, this, tl1),
                new Minus()
            };
        }
        
        if (token.isInstanceOfAny(
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
            return new GrammarSymbol[] { new SemanticAction10(this) };
        }
        
        throw new UnrecognizedStructureException(this, token);
    }
    
}
