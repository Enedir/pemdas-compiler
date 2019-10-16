/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.grammar.semanticaction;

import br.ifmath.compiler.domain.grammar.nonterminal.Func;
import br.ifmath.compiler.domain.grammar.terminal.function.Function;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

/**
 *
 * @author alex_
 */
public class SemanticAction32 extends SemanticAction {

    private final Func func;
    private final Function function;
    
    public SemanticAction32(Func func, Function function) {
        super("AS32");
        
        this.func = func;
        this.function = function;
    }

    @Override
    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator) {
        func.setOperator(function.getValue());
    }
    
}
