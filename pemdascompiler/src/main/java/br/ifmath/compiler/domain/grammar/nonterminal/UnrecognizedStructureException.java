package br.ifmath.compiler.domain.grammar.nonterminal;

import br.ifmath.compiler.domain.compiler.Token;
import br.ifmath.compiler.domain.grammar.GrammarSymbol;

public class UnrecognizedStructureException  extends Exception {

    public UnrecognizedStructureException(GrammarSymbol symbol, Token token) {
        super(String.format("O padrão {%s; %s} não foi reconhecido.", symbol.toString(), token.toString()));
    }
}
