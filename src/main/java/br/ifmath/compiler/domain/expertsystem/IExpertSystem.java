/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.expertsystem;


import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.input.ValueVariable;

import java.util.List;

/**
 * @author alex_
 */
public interface IExpertSystem {

    public IAnswer findBestAnswer(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException;

    public IAnswer findPossibleHandles(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException;

    public void validateExpressions(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException;

    public void setVariables(List<NumericValueVariable> variables);
}
