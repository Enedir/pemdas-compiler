/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.semanticaction;

import br.ifmath.compiler.domain.grammar.nonterminal.Func;
import br.ifmath.compiler.domain.grammar.terminal.number.NaturalNumber;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

/**
 *
 * @author alex_
 */
public class SemanticAction36 extends SemanticAction {

    private final Func func;
    private final NaturalNumber integerNumber;
    
    public SemanticAction36(Func func, NaturalNumber integerNumber) {
        super("AS36");
        
        this.func = func;
        this.integerNumber = integerNumber;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        func.setParameter(Integer.parseInt(integerNumber.getValue()));
    }
    
}
