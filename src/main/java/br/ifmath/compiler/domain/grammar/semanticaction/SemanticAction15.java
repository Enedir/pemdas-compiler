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
public class SemanticAction15 extends SemanticAction {

    private final P p;
    private final ML ml;
    private final ML ml1;
    
    public SemanticAction15(P p, ML ml, ML ml1) {
        super("AS15");
        
        this.p = p;
        this.ml = ml;
        this.ml1 = ml1;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        ml.setValue(true);
        ml.setOperator("*");
        
        if (ml1.isValue()) {
            ml.setAddress(intermediateCodeGenerator.getNextTemporary().toString());
            intermediateCodeGenerator.addNewOperation(ml1.getOperator(), p.getAddress(), ml1.getAddress(), ml.getAddress(), ml.getPosition(), ml.getLevel());
        } else {
            ml.setAddress(p.getAddress());
        }
    }
    
}
