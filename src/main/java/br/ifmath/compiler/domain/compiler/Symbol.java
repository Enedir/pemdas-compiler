package br.ifmath.compiler.domain.compiler;

/**
 * It represents a symbol of symbol's table
 *
 * @author alexjravila
 */
public class Symbol {
    private int id;
    private String value;

    public Symbol(int id) {
        super();
        this.id = id;
    }

    public Symbol(int id, String value) {
        this(id);
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final Symbol other = (Symbol) obj;
        return this.id != other.id;
    }

    @Override
    public String toString() {
        return "Symbol{" + "id=" + id + ", value=" + value + '}';
    }

}
