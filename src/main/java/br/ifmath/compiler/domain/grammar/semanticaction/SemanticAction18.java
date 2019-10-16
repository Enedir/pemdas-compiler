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
public class SemanticAction18 extends SemanticAction {

    private final O o;
    private final P p;
    private final PL pl;
    
    public SemanticAction18(O o, P p, PL pl) {
        super("AS18");
        
        this.o = o;
        this.p = p;
        this.pl = pl;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        o.setPosition(p.getPosition());
        pl.setPosition(p.getPosition());
        o.setLevel(p.getLevel());
        pl.setLevel(p.getLevel());
    }
    
}
