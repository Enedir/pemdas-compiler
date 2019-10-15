/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.expertsystem;


import br.ifmath.compiler.domain.compiler.ThreeAddressCode;

import java.util.List;

/**
 * @author alex_
 */
public interface IRule {
    /**
     * Verifica se a regra atual pode ser aplicada.
     *
     * @param source {@link List} de {@link ThreeAddressCode} que representa a função dada pelo usuário.
     * @return true se a regra deve ser aplicada, e false caso contrário.
     */
    boolean match(List<ThreeAddressCode> source);

    /**
     * Efetivamente aplica a regra atual
     *
     * @param source {@link List} de {@link ThreeAddressCode} que representa a função dada pelo usuário.
     * @return nova {@link List} de {@link Step}s com a regra já aplicada.
     * @throws InvalidAlgebraicExpressionException caso a função esteja digitada de maneira incorreta
     */
    List<Step> handle(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException;

}
