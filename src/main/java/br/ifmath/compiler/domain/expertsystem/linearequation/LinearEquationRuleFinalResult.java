/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.expertsystem.linearequation;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alex_
 */
public class LinearEquationRuleFinalResult implements IRule {

    @Override
    public boolean match(List<ThreeAddressCode> sources) {
        return isFinalResult(sources.get(0), sources.get(0).getLeft(), sources.get(0).getRight())
                || isFinalResult(sources.get(0), sources.get(0).getRight(), sources.get(0).getLeft());
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();
        steps.add(new Step(sources, sources.get(0).toLaTeXNotation(), sources.get(0).toMathNotation(), "Resultado final."));
        return steps;
    }
        
    private boolean isFinalResult(ThreeAddressCode threeAddressCode, String possibleVariable, String possibleNumber) {        
        return StringUtil.match(possibleVariable, RegexPattern.VARIABLE.toString())
                && !StringUtil.match(possibleVariable, RegexPattern.TEMPORARY_VARIABLE.toString()) 
                && (StringUtil.matchAny(possibleNumber, RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString()) 
                    || isNegativeOrFraction(threeAddressCode, possibleNumber));
    }
    
    private boolean isNegativeOrFraction(ThreeAddressCode threeAddressCode, String possibleNumber) {
        ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(possibleNumber);
        if (expandedQuadruple == null)
            return false;
        
        if (expandedQuadruple.isNegative()) {

            if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple innerOperation = threeAddressCode.findQuadrupleByResult(expandedQuadruple.getArgument1());
                return innerOperation.isFraction() 
                    && StringUtil.matchAny(innerOperation.getArgument1(), RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString())
                    && StringUtil.matchAny(innerOperation.getArgument2(), RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString());
            }

            return true;
        }

        return expandedQuadruple.isFraction() 
                && isNumber(threeAddressCode, expandedQuadruple.getArgument1())
                && isNumber(threeAddressCode, expandedQuadruple.getArgument2());
    }
    
    private boolean isNumber(ThreeAddressCode threeAddressCode, String possibleNumber) {
        if (StringUtil.match(possibleNumber, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple innerOperation = threeAddressCode.findQuadrupleByResult(possibleNumber);
            return innerOperation.isNegative() && StringUtil.matchAny(innerOperation.getArgument1(), RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString());
        }
        
        return StringUtil.matchAny(possibleNumber, RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString());
    }
}
