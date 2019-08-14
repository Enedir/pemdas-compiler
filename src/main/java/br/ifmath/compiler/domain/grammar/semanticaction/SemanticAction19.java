/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.semanticaction;


import br.ifmath.compiler.domain.grammar.nonterminal.O;
import br.ifmath.compiler.domain.grammar.nonterminal.PL;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

/**
 *
 * @author alex_
 */
public class SemanticAction19 extends SemanticAction {

    private final O o;
    private final PL pl;
    private final PL pl1;
    
    public SemanticAction19(O o, PL pl, PL pl1) {
        super("AS19");
        
        this.o = o;
        this.pl = pl;
        this.pl1 = pl1;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        o.setPosition(pl.getPosition());
        pl1.setPosition(pl.getPosition());
        o.setLevel(pl.getLevel());
        pl1.setLevel(pl.getLevel());
    }
    
}
