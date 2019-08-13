/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.semanticaction;

import br.ifmath.compiler.domain.grammar.nonterminal.F;
import br.ifmath.compiler.domain.grammar.nonterminal.O;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

/**
 *
 * @author alex_
 */
public class SemanticAction22 extends SemanticAction {

    private final F f;
    private final O o;
    
    public SemanticAction22(F f, O o) {
        super("AS22");
        
        this.f = f;
        this.o = o;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        f.setPosition(o.getPosition());
        f.setLevel(o.getLevel());
    }
    
}
