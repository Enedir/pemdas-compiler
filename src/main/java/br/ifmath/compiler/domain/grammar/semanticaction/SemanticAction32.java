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
public class SemanticAction32 extends SemanticAction {

    private final Func func;
    private final T t;
    
    public SemanticAction32(Func func, T t) {
        super("AS32");
        
        this.func = func;
        this.t = t;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        func.setAddress(intermediateCodeGenerator.getNextTemporary().toString());
        intermediateCodeGenerator.addNewOperation(func.getOperator(), t.getAddress(), func.getAddress(), func.getPosition(), func.getLevel());
    }
    
}
