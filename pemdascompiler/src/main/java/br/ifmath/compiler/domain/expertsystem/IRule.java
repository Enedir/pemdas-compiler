/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.expertsystem;


import br.ifmath.compiler.domain.compiler.ThreeAddressCode;

import java.util.List;

/**
 *
 * @author alex_
 */
public interface IRule {
    
    public boolean match(List<ThreeAddressCode> source);
    
    public List<Step> handle(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException;
    
}
