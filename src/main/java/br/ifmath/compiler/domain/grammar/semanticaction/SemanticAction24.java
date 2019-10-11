/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.semanticaction;

import br.ifmath.compiler.domain.grammar.nonterminal.F;
import br.ifmath.compiler.domain.grammar.nonterminal.O;
import br.ifmath.compiler.domain.grammar.nonterminal.S;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

/**
 *
 * @author alex_
 */
public class SemanticAction24 extends SemanticAction {

    private final S s;
    private final F f;
    private final O o;
    
    public SemanticAction24(S s, F f, O o) {
        super("AS24");
        
        this.s = s;
        this.f = f;
        this.o = o;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        if (s.isValue()) {
            o.setAddress(intermediateCodeGenerator.getNextTemporary().toString());
            intermediateCodeGenerator.addNewOperation("MINUS", f.getAddress(), o.getAddress(), o.getPosition(), o.getLevel());
        } else {
            o.setAddress(f.getAddress());
        }    
    }
    
}
