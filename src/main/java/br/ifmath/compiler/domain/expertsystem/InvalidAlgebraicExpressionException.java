/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.expertsystem;

/**
 *
 * @author alex_
 */
public class InvalidAlgebraicExpressionException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1396171472572015846L;

	public InvalidAlgebraicExpressionException(String message) {
        super(message);
    }
    
}
