/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.infrastructure.compiler;

import br.ifmath.compiler.domain.compiler.Symbol;
import br.ifmath.compiler.domain.compiler.Token;
import br.ifmath.compiler.infrastructure.compiler.iface.ILexicalAnalyzer;
import br.ifmath.compiler.infrastructure.compiler.iface.ISymbolTable;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author alex_
 */
public class LexicalAnalyzer implements ILexicalAnalyzer {
    
    private Token nextToken;
    private final ISymbolTable symbolTable;
    
    public LexicalAnalyzer(ISymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }
    
    /**
     * It returns the last recognized token by lexical analyzer
     * @return last recognized token
     */
    @Override
    public Token getNextToken() {
        return nextToken;
    }
    
    /**
     * It returns the next recognized token by lexical analyzer
     * @param expression - expression that will be analyzed
     * @return next recognized token
     * @throws br.ifmath.compiler.infrastructure.compiler.UnrecognizedLexemeException - when a lemexe isn't recognized by lexical analyzer
     */
    @Override
    public String identifyNextToken(String expression) throws UnrecognizedLexemeException {
    	return findNextToken(expression);
    }
    
    /**
     * It returns a list of recognized tokens in a expression
     * 
     * @param expression - expression to be analysed by compiler
     * @return a list of recognized tokens
     * @throws br.ifmath.compiler.infrastructure.compiler.UnrecognizedLexemeException - when a lemexe isn't recognized by lexical analyzer
     */
    @Override
    public List<Token> lexicalAnalysis(String expression) throws UnrecognizedLexemeException {
        List<Token> tokens = new ArrayList<>();
        
        while (StringUtil.isNotEmpty(expression)) {
            expression = findNextToken(expression);
            tokens.add(getNextToken());
        }
        
        return tokens;
    }    
    
    /**
     * It finds next valid token in an expression and return the expression without that token.
     * This method execute a looping that identify new lexemes to be analysed. After this, the 
     * resulted lexeme will be compared to regular expression of language
     * 
     * @param expression - expression to be analysed
     * @return expression without recognized token
     */
    private String findNextToken(String expression) throws UnrecognizedLexemeException {
        nextToken = null;

        expression = expression.trim();

        String lexeme = "";

        LanguageToken token = null, currentToken;

        int beginIndex = 0, endIndex = 1;
        while (endIndex <= expression.length()) {
            lexeme = expression.substring(beginIndex, endIndex);

            currentToken = LanguageToken.get(lexeme);

            if (currentToken != null) {
                token = currentToken;
            } else {
                if (token != null) {
                    lexeme = lexeme.substring(0, lexeme.length() - 1);
                    
                    if (!token.matchPattern(lexeme)) {
                        token = null;
                    }
                }
                
                break;
            }

            endIndex++;
        }

        if (token != null) {
            if (Objects.equals(token, LanguageToken.ID) || Objects.equals(token, LanguageToken.ID_WITH_COEFFICIENT)) {
                createIdToken(lexeme, token);
            } else if (Objects.equals(token, LanguageToken.NATURAL_NUMBER) || Objects.equals(token, LanguageToken.DECIMAL_NUMBER)) {
                createNumberToken(lexeme, token);
            } else {
                nextToken = new Token(token.getMatchingTerminal());
            }
            
            expression = expression.replaceFirst(StringUtil.getLiteralLexemeToRegex(lexeme), "");
        } else {
            throw new UnrecognizedLexemeException(lexeme);
        }
        
        return expression;
    }
    
    /**
     * It creates a id token
     * 
     * @param lexeme - recognized lexeme
     */
    private void createIdToken(String lexeme, LanguageToken token) {
        Symbol symbol = new Symbol(symbolTable.getNextId() ,lexeme);
        symbolTable.addSymbol(symbol);
        nextToken = new Token(token.getMatchingTerminal(), symbol.getId());
    }
    
    /**
     * It creates a number token
     * 
     * @param lexeme - recognized lexeme
     */
    private void createNumberToken(String lexeme, LanguageToken token) {
        nextToken = new Token(token.getMatchingTerminal(), lexeme);
    }
}
