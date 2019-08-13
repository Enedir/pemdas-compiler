/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.semanticaction;

import br.ifmath.compiler.domain.grammar.nonterminal.ML;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

/**
 *
 * @author alex_
 */
public class SemanticAction16 extends SemanticAction {

    private final ML ml;
    
    public SemanticAction16(ML ml) {
        super("AS16");
        
        this.ml = ml;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        ml.setValue(false);
    }
    
}
