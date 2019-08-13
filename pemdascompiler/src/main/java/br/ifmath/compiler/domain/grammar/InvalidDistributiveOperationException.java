package br.ifmath.compiler.domain.grammar;

public class InvalidDistributiveOperationException extends Exception {

    public InvalidDistributiveOperationException() {
        super("Operação distributiva inválida. O operador de multiplicação (*) sempre deve ser utilizado!");
    }

}
