/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.semanticaction;


import br.ifmath.compiler.domain.grammar.nonterminal.F;
import br.ifmath.compiler.domain.grammar.nonterminal.Func;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

/**
 *
 * @author alex_
 */
public class SemanticAction27 extends SemanticAction {

    private final Func func;
    private final F f;
    
    public SemanticAction27(Func func, F f) {
        super("AS27");
        
        this.func = func;
        this.f = f;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        f.setAddress(func.getAddress());
    }
    
}
