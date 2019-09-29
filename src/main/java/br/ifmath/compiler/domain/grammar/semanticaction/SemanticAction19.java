/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.semanticaction;


import br.ifmath.compiler.domain.grammar.nonterminal.O;
import br.ifmath.compiler.domain.grammar.nonterminal.P;
import br.ifmath.compiler.domain.grammar.nonterminal.PL;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

/**
 *
 * @author alex_
 */
public class SemanticAction19 extends SemanticAction {

    private final O o;
    private final P p;
    private final PL pl;
    
    public SemanticAction19(O o, P p, PL pl) {
        super("AS19");
        
        this.o = o;
        this.p = p;
        this.pl = pl;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        if (pl.isValue()) {
            p.setAddress(intermediateCodeGenerator.getNextTemporary().toString());
            intermediateCodeGenerator.addNewOperation(pl.getOperator(), o.getAddress(), pl.getAddress(), p.getAddress(), p.getPosition(), p.getLevel());
        } else {
            p.setAddress(o.getAddress());
        }
    }
    
}
