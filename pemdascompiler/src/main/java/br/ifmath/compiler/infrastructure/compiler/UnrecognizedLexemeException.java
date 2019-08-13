/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.infrastructure.compiler;

/**
 *
 * @author alex_
 */
public class UnrecognizedLexemeException extends Exception {

    public UnrecognizedLexemeException(String lexeme) {
        super(String.format("O lexema %s não foi reconhecido.", lexeme));
    }
    
}
