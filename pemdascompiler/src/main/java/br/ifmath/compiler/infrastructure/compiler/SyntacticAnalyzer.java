/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.infrastructure.compiler;

import br.ifmath.compiler.domain.compiler.Token;
import br.ifmath.compiler.domain.grammar.GrammarSymbol;
import br.ifmath.compiler.domain.grammar.nonterminal.INonTerminal;
import br.ifmath.compiler.domain.grammar.nonterminal.UnrecognizedStructureException;
import br.ifmath.compiler.domain.grammar.semanticaction.ISemanticAction;
import br.ifmath.compiler.domain.grammar.terminal.ITerminal;
import br.ifmath.compiler.domain.grammar.terminal.IVariableTerminal;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;
import br.ifmath.compiler.infrastructure.compiler.iface.ISymbolTable;
import br.ifmath.compiler.infrastructure.compiler.iface.ISyntacticAnalyzer;
import br.ifmath.compiler.infrastructure.stack.Stack;
import br.ifmath.compiler.infrastructure.stack.exception.StackAddNullItemException;

/**
 *
 * @author alex_
 */
public class SyntacticAnalyzer implements ISyntacticAnalyzer {
    
    private final Stack<GrammarSymbol> stack;
    private final ISymbolTable symbolTable;
    private final IIntermediateCodeGenerator intermediateCodeGenerator;

    public SyntacticAnalyzer(Stack<GrammarSymbol> stack, ISymbolTable symbolTable, IIntermediateCodeGenerator intermediateCodeGenerator) {
        this.stack = stack;
        this.symbolTable = symbolTable;
        this.intermediateCodeGenerator = intermediateCodeGenerator;
    }
    
    @Override
    public void analyzeSyntactically(Token token) throws UnrecognizedStructureException {
        while (token != null) {
            GrammarSymbol symbol = stack.pop();
            
            if (symbol instanceof ITerminal) {
                if (!symbol.equals(token.getTerminal())) {
                    throw new UnrecognizedStructureException(symbol, token);
                }
                
                if (symbol instanceof IVariableTerminal) {
                    String value = token.getLexeme(symbolTable);
                    ((IVariableTerminal) symbol).setValue(value);
                }
                
                token = null;
            } else if (symbol instanceof ISemanticAction) {
                ISemanticAction semanticAction = (ISemanticAction) symbol;
                semanticAction.executeAction(intermediateCodeGenerator);
            } else {
                INonTerminal nonTerminal = (INonTerminal) symbol;
                GrammarSymbol[] production = nonTerminal.derivate(token);
                
                try {
                    stack.pushAll(production);
                } catch (StackAddNullItemException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
}
