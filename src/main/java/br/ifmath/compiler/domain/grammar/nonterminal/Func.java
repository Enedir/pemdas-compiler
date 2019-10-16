/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.nonterminal;


import br.ifmath.compiler.domain.compiler.Token;
import br.ifmath.compiler.domain.grammar.GrammarSymbol;
import br.ifmath.compiler.domain.grammar.semanticaction.*;
import br.ifmath.compiler.domain.grammar.terminal.Semicolon;
import br.ifmath.compiler.domain.grammar.terminal.function.*;
import br.ifmath.compiler.domain.grammar.terminal.number.NaturalNumber;
import br.ifmath.compiler.domain.grammar.terminal.precedence.BeginParentheses;
import br.ifmath.compiler.domain.grammar.terminal.precedence.EndParentheses;

/**
 *
 * @author alex_
 */
public class Func extends NonTerminal {

    private String address;
    private int position;
    private int level;
    private int parameter;
    private String operator;
    
    public Func() {
        super("FUNC");
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

    public int getParameter() {
        return parameter;
    }

    public void setParameter(int parameter) {
        this.parameter = parameter;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public GrammarSymbol[] derivate(Token token) throws UnrecognizedStructureException {
        if (token.isInstanceOfAny(Cosine.class)) {
            T t = new T();
            Cosine cosine = new Cosine();
            
            return new GrammarSymbol[] {
                new SemanticAction33(this, t),
                new EndParentheses(),
                t,
                new BeginParentheses(),
                new SemanticAction32(this, cosine),
                cosine
            };
        }
        
        if (token.isInstanceOfAny(Sine.class)) {
            T t = new T();
            Sine sine = new Sine();
            
            return new GrammarSymbol[] {
                new SemanticAction33(this, t),
                new EndParentheses(),
                t,
                new BeginParentheses(),
                new SemanticAction32(this, sine),
                sine
            };
        }
        if (token.isInstanceOfAny(Tangent.class)) {
            T t = new T();
            Tangent tangent = new Tangent();
            
            return new GrammarSymbol[] {
                new SemanticAction33(this, t),
                new EndParentheses(),
                t,
                new BeginParentheses(),
                new SemanticAction32(this, tangent),
                tangent
            };
        }
        if (token.isInstanceOfAny(SquareRoot.class)) {
            T t = new T();
            SquareRoot squareRoot = new SquareRoot();
            
            return new GrammarSymbol[] {
                new SemanticAction35(this, t),
                new EndParentheses(),
                new SemanticAction34(this),
                t,
                new BeginParentheses(),
                new SemanticAction32(this, squareRoot),
                squareRoot
            };
        }
        
        if (token.isInstanceOfAny(Root.class)) {
            T t = new T();
            Root root = new Root();
            NaturalNumber integerNumber = new NaturalNumber();
            
            return new GrammarSymbol[] {
                new SemanticAction35(this, t),
                new EndParentheses(),
                new SemanticAction36(this, integerNumber),
                integerNumber,
                new Semicolon(),
                t,
                new BeginParentheses(),
                new SemanticAction32(this, root),
                root
            };
        }
        
        if (token.isInstanceOfAny(Log10.class)) {
            T t = new T();
            Log10 log10 = new Log10();
                    
            return new GrammarSymbol[] {
                new SemanticAction35(this, t),
                new EndParentheses(),
                new SemanticAction37(this),
                t,
                new BeginParentheses(),
                new SemanticAction32(this, log10),
                log10
            };
        }
        
        if (token.isInstanceOfAny(Log.class)) {
            T t = new T();
            Log log = new Log();
            NaturalNumber integerNumber = new NaturalNumber();
            
            return new GrammarSymbol[] {
                new SemanticAction35(this, t),
                new EndParentheses(),
                new SemanticAction36(this, integerNumber),
                integerNumber,
                new Semicolon(),
                t,
                new BeginParentheses(),
                new SemanticAction32(this, log),
                log
            };
        }
        throw new UnrecognizedStructureException(this, token);
    }
    
}
