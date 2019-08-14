package br.ifmath.compiler.infrastructure.compiler.iface;


import br.ifmath.compiler.domain.grammar.terminal.Terminal;

public interface ILanguageToken {

    public Terminal getMatchingTerminal();

    public boolean matchPattern(String pattern);

    public boolean possibleMatch(String pattern);
}
