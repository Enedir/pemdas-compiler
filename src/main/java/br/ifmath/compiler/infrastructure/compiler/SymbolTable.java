/*
 * Paga nois que vc pode usar essa porra
 */
package br.ifmath.compiler.infrastructure.compiler;

import br.ifmath.compiler.domain.compiler.Symbol;
import br.ifmath.compiler.infrastructure.compiler.iface.ISymbolTable;

import java.util.ArrayList;
import java.util.List;

/**
 * It represents the symbol table
 * 
 * @author alexjravila
 */
public class SymbolTable implements ISymbolTable {

    private final List<Symbol> symbols;
        
    public SymbolTable() {
        super();
        
        symbols = new ArrayList<>();
    }
    
    /**
     * It adds a symbol in symbol table
     *
     * @param symbol - symbol to be added in symbol table
     */
    @Override
    public void addSymbol(Symbol symbol) {
        this.symbols.add(symbol);
    } 
    
    /**
     * It removes a symbol of symbol table
     *
     * @param symbol - symbol to be removed of symbol table
     */
    @Override
    public void removeSymbol(Symbol symbol) {
        this.symbols.remove(symbol);
    } 
    
    /**
     * It returns all symbols
     * 
     * @return the list of symbols
     */
    @Override
    public List<Symbol> getSymbols() {
        return this.symbols;
    }
    
    @Override
    public int getNextId() {
        return this.symbols.size();
    }
    
    @Override
    public Symbol get(int id) {
        return this.symbols.get(id);
    }
    
    /**
     * It cleans the symbol table
     */
    @Override
    public void cleanSymbolTable() {
        this.symbols.clear();
    }
    
}
