package br.ifmath.compiler.domain.compiler;

/**
 *
 * @author alex_
 */
public class Temporary {

    private int id;

    public Temporary(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("T%d", id);
    }

}
