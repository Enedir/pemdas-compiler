package br.ifmath.compiler.domain.grammar.semanticaction;

import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

public interface ISemanticAction {

    public String getName();

    public void executeAction(IIntermediateCodeGenerator intermediateCodeGenerator);
}
