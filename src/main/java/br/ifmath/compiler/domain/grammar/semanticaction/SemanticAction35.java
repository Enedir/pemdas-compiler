/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.semanticaction;

import br.ifmath.compiler.domain.grammar.nonterminal.Func;
import br.ifmath.compiler.domain.grammar.nonterminal.T;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

/**
 *
 * @author alex_
 */
public class SemanticAction35 extends SemanticAction {

    private final Func func;
    private final T t;
    
    public SemanticAction35(Func func, T t) {
        super("AS35");
        
        this.func = func;
        this.t = t;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        func.setAddress(intermediateCodeGenerator.getNextTemporary().toString());
        intermediateCodeGenerator.addNewOperation(func.getOperator(), t.getAddress(), Integer.toString(func.getParameter()), 
                func.getAddress(), func.getPosition(), func.getLevel());
    }
    
}
