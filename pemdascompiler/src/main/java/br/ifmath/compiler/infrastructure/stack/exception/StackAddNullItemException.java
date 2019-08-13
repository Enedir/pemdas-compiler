package br.ifmath.compiler.infrastructure.stack.exception;

/**
 *
 * @author alex_
 */
public class StackAddNullItemException extends Exception {

    public StackAddNullItemException() {
        super("Não é possível inserir um item nulo.");
    }

}
