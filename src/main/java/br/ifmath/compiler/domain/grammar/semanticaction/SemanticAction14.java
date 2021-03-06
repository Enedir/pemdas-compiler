/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.semanticaction;


import br.ifmath.compiler.domain.grammar.nonterminal.ML;
import br.ifmath.compiler.domain.grammar.nonterminal.P;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

/**
 *
 * @author alex_
 */
public class SemanticAction14 extends SemanticAction {

    private final P p;
    private final ML ml;
    private final ML ml1;
    
    public SemanticAction14(P p, ML ml, ML ml1) {
        super("AS14");
        
        this.p = p;
        this.ml = ml;
        this.ml1 = ml1;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        p.setPosition(ml.getPosition());
        ml1.setPosition(ml.getPosition());
        p.setLevel(ml.getLevel());
        ml1.setLevel(ml.getLevel());
    }
    
}
