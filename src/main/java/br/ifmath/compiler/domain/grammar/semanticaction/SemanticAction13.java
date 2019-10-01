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
public class SemanticAction13 extends SemanticAction {

    private final P p;
    private final M m;
    private final ML ml;
    
    public SemanticAction13(P p, M m, ML ml) {
        super("AS13");
        
        this.p = p;
        this.m = m;
        this.ml = ml;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        if (ml.isValue()) {
            m.setAddress(intermediateCodeGenerator.getNextTemporary().toString());
            intermediateCodeGenerator.addNewOperation(ml.getOperator(), p.getAddress(), ml.getAddress(), m.getAddress(), m.getPosition(), m.getLevel());
        } else {
            m.setAddress(p.getAddress());
        }
    }
    
}
