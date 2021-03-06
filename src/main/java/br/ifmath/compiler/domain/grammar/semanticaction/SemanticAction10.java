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
public class SemanticAction10 extends SemanticAction {

    private final M m;
    private final TL tl;
    private final TL tl1;
    
    public SemanticAction10(M m, TL tl, TL tl1) {
        super("AS10");
        
        this.m = m;
        this.tl = tl;
        this.tl1 = tl1;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
       tl.setValue(true);
        tl.setOperator("-");
        
        if (tl1.isValue()) {
            tl.setAddress(intermediateCodeGenerator.getNextTemporary().toString());
            intermediateCodeGenerator.addNewOperation(tl1.getOperator(), m.getAddress(), tl1.getAddress(), tl.getAddress(), tl.getPosition(), tl.getLevel());
        } else {
            tl.setAddress(m.getAddress());
        }
    }
    
}
