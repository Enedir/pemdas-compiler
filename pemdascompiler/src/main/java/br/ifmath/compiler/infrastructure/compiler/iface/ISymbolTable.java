package br.ifmath.compiler.infrastructure.compiler.iface;

import br.ifmath.compiler.domain.compiler.Symbol;

import java.util.List;

/**
 *
 * @author alex_
 */
public interface ISymbolTable {

    public void addSymbol(Symbol symbol);

    public void removeSymbol(Symbol symbol);

    public List<Symbol> getSymbols();

    public int getNextId();

    public Symbol get(int id);

    public void cleanSymbolTable();
}
