package br.ifmath.compiler.infrastructure.compiler.iface;

import br.ifmath.compiler.domain.compiler.Token;
import br.ifmath.compiler.domain.grammar.nonterminal.UnrecognizedStructureException;

public interface ISyntacticAnalyzer {

    public void analyzeSyntactically(Token token) throws UnrecognizedStructureException;
}
