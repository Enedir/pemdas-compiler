package br.ifmath.compiler.domain.grammar.nonterminal;


import br.ifmath.compiler.domain.compiler.Token;
import br.ifmath.compiler.domain.grammar.GrammarSymbol;

/**
 *
 * @author alex_
 */
public interface INonTerminal {

    public String getName();

    public GrammarSymbol[] derivate(Token token) throws UnrecognizedStructureException;
}
