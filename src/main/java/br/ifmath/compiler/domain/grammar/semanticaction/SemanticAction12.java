/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.semanticaction;

import br.ifmath.compiler.domain.grammar.nonterminal.M;
import br.ifmath.compiler.domain.grammar.nonterminal.ML;
import br.ifmath.compiler.domain.grammar.nonterminal.P;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

/**
 *
 * @author alex_
 */
public class SemanticAction12 extends SemanticAction {

    private final P p;
    private final M m;
    private final ML ml;
            
    public SemanticAction12(P p, M m, ML ml) {
        super("AS12");
        
        this.p = p;
        this.m = m;
        this.ml = ml;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        p.setPosition(m.getPosition());
        ml.setPosition(m.getPosition());
        p.setLevel(m.getLevel());
        ml.setLevel(m.getLevel());
    }
    
}
