/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.semanticaction;


import br.ifmath.compiler.domain.grammar.nonterminal.C;
import br.ifmath.compiler.domain.grammar.terminal.comparison.Comparison;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

/**
 *
 * @author alex_
 */
public class SemanticAction4 extends SemanticAction {

    private final C c;
    private final Comparison comparison;
    
    public SemanticAction4(C c, Comparison comparison) {
        super("AS4");
        
        this.c = c;
        this.comparison = comparison;
    }
    
    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        c.setAddress(comparison.getValue());
    }
    
}
