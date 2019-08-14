/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.semanticaction;

import br.ifmath.compiler.domain.grammar.nonterminal.M;
import br.ifmath.compiler.domain.grammar.nonterminal.TL;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

/**
 *
 * @author alex_
 */
public class SemanticAction7 extends SemanticAction {

    private final M m;
    private final TL tl;
    private final TL tl1;
    
    public SemanticAction7(M m, TL tl, TL tl1) {
        super("AS7");
        
        this.m = m;
        this.tl = tl;
        this.tl1 = tl1;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        m.setPosition(tl.getPosition());
        tl1.setPosition(tl.getPosition());
        m.setLevel(tl.getLevel());
        tl1.setLevel(tl.getLevel());
    }
    
}
