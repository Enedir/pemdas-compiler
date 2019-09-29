/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.semanticaction;


import br.ifmath.compiler.domain.grammar.nonterminal.F;
import br.ifmath.compiler.domain.grammar.terminal.VariableTerminal;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

/**
 *
 * @author alex_
 */
public class SemanticAction31 extends SemanticAction {

    private final F f;
    private final VariableTerminal variableTerminal;
    
    public SemanticAction31(F f, VariableTerminal variableTerminal) {
        super("AS31");
        
        this.f = f;
        this.variableTerminal = variableTerminal;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        f.setAddress(variableTerminal.getValue());
    }
    
}
