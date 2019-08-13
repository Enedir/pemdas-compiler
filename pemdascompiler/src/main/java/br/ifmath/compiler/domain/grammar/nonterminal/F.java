/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.nonterminal;


import br.ifmath.compiler.domain.compiler.Token;
import br.ifmath.compiler.domain.grammar.GrammarSymbol;
import br.ifmath.compiler.domain.grammar.semanticaction.*;
import br.ifmath.compiler.domain.grammar.terminal.function.*;
import br.ifmath.compiler.domain.grammar.terminal.id.Id;
import br.ifmath.compiler.domain.grammar.terminal.id.IdWithCoefficient;
import br.ifmath.compiler.domain.grammar.terminal.number.DecimalNumber;
import br.ifmath.compiler.domain.grammar.terminal.number.NaturalNumber;
import br.ifmath.compiler.domain.grammar.terminal.precedence.*;

/**
 *
 * @author alex_
 */
public class F extends NonTerminal {

    private String address;
    private int position;
    private int level;
    
    public F() {
        super("F");
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
        if (token.isInstanceOfAny(BeginParentheses.class)) {
            T t = new T();
            
            return new GrammarSymbol[] {
                new SemanticAction29(t, this),
                new EndParentheses(),
                t,
                new SemanticAction28(t, this),
                new BeginParentheses()
            };
        }
        
        if (token.isInstanceOfAny(BeginBracket.class)) {
            T t = new T();
            
            return new GrammarSymbol[] {
                new SemanticAction29(t, this),
                new EndBracket(),
                t,
                new SemanticAction28(t, this),
                new BeginBracket()
            };
        }
        
        if (token.isInstanceOfAny(BeginKey.class)) {
            T t = new T();
            
            return new GrammarSymbol[] {
                new SemanticAction29(t, this),
                new EndKey(),
                t,
                new SemanticAction28(t, this),
                new BeginKey()
            };
        }
        
        if (token.isInstanceOfAny(Id.class)) {
            Id id = new Id();
            return new GrammarSymbol[] {
                new SemanticAction30(this, id),
                id
            };
        }
        
        if (token.isInstanceOfAny(IdWithCoefficient.class)) {
            IdWithCoefficient idWithCoefficient = new IdWithCoefficient();
            return new GrammarSymbol[] {
                new SemanticAction30(this, idWithCoefficient),
                idWithCoefficient
            };
        }
        
        if (token.isInstanceOfAny(NaturalNumber.class)) {
            NaturalNumber integerNumber = new NaturalNumber();
            
            return new GrammarSymbol[] {
                new SemanticAction30(this, integerNumber),
                integerNumber
            };
        }
        
        if (token.isInstanceOfAny(DecimalNumber.class)) {
            DecimalNumber decimalNumber = new DecimalNumber();
            return new GrammarSymbol[] {
                new SemanticAction30(this, decimalNumber),
                decimalNumber
            };
        }
        
        if (token.isInstanceOfAny(
                Cosine.class,
                Sine.class,
                Tangent.class,
                SquareRoot.class,
                Root.class,
                Log.class,
                Log10.class
            )
        ) {
            Func func = new Func();
            
            return new GrammarSymbol[] {
                new SemanticAction27(func, this),
                func,
                new SemanticAction26(func, this)
            };
        }
        
        throw new UnrecognizedStructureException(this, token);
    }
    
}
