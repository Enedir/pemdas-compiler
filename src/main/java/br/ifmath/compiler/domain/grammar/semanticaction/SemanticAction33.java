/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.semanticaction;


import br.ifmath.compiler.domain.grammar.nonterminal.Func;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

/**
 *
 * @author alex_
 */
public class SemanticAction33 extends SemanticAction {

    private final Func func;
    
    public SemanticAction33(Func func) {
        super("AS33");
        
        this.func = func;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        func.setParameter(2);
    }
    
}
