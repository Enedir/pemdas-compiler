/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.semanticaction;

import br.ifmath.compiler.domain.grammar.nonterminal.M;
import br.ifmath.compiler.domain.grammar.nonterminal.T;
import br.ifmath.compiler.domain.grammar.nonterminal.TL;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

/**
 *
 * @author alex_
 */
public class SemanticAction5 extends SemanticAction {

    private final M m;
    private final TL tl;
    private final T t;
    
    public SemanticAction5(M m, TL tl, T t) {
        super("AS5");
        
        this.m = m;
        this.tl = tl;
        this.t = t;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        m.setPosition(t.getPosition());
        tl.setPosition(t.getPosition());
        m.setLevel(t.getLevel());
        tl.setLevel(t.getLevel());
    }
    
}
