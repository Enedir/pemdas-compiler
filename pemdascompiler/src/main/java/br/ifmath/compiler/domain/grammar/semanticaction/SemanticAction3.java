/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.semanticaction;

import br.ifmath.compiler.domain.grammar.nonterminal.C;
import br.ifmath.compiler.domain.grammar.nonterminal.E;
import br.ifmath.compiler.domain.grammar.nonterminal.T;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

/**
 *
 * @author alex_
 */
public class SemanticAction3 extends SemanticAction {

    private final E e;
    private final T t1;
    private final T t2;
    private final C c;
    
    public SemanticAction3(E e, T t1, T t2, C c) {
        super("AS3");
        
        this.e = e;
        this.t1 = t1;
        this.t2 = t2;
        this.c = c;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        e.setParameter1(t1.getAddress());
        e.setComparison(c.getAddress());
        e.setParameter2(t2.getAddress());
    }
    
}
