package br.ifmath.compiler.infrastructure.stack.exception;

/**
 *
 * @author alex_
 */
public class StackNullClassException extends Exception {

    public StackNullClassException() {
        super("Não é possível pesquisar com uma classe nula");
    }

}
