package br.ifmath.compiler.infrastructure.compiler.iface;

import br.ifmath.compiler.domain.compiler.Token;
import br.ifmath.compiler.infrastructure.compiler.UnrecognizedLexemeException;

import java.util.List;

public interface ILexicalAnalyzer {

    public Token getNextToken();

    public String identifyNextToken(String expression) throws UnrecognizedLexemeException;

    public List<Token> lexicalAnalysis(String expression) throws UnrecognizedLexemeException;
}
