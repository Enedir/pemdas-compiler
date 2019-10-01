/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.semanticaction;

import br.ifmath.compiler.domain.grammar.nonterminal.C;
import br.ifmath.compiler.domain.grammar.nonterminal.EL;
import br.ifmath.compiler.domain.grammar.nonterminal.T;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

/**
 *
 * @author alex_
 */
public class SemanticAction4 extends SemanticAction {

    private final EL el;
    private final T t;
    private final C c;
    
    public SemanticAction4(EL el, T t, C c) {
        super("AS4");
        
        this.el = el;
        this.t = t;
        this.c = c;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        el.setParameter(t.getAddress());
        el.setComparison(c.getAddress());
    }
    
}
