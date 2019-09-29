/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.semanticaction;

import br.ifmath.compiler.domain.grammar.nonterminal.EL;
import br.ifmath.compiler.domain.grammar.nonterminal.E;
import br.ifmath.compiler.domain.grammar.nonterminal.T;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

/**
 *
 * @author alex_
 */
public class SemanticAction2 extends SemanticAction {

    private final E e;
    private final T t;
    private final EL el;
    
    public SemanticAction2(E e, T t, EL el) {
        super("AS2");
        
        this.e = e;
        this.t = t;
        this.el = el;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        e.setParameter1(t.getAddress());
        e.setComparison(el.getComp());
        e.setParameter2(el.getParam());
    }
    
}
