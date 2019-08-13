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
import br.ifmath.compiler.infrastructure.util.NumberUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alex_
 */
public class LinearEquationRuleFractionCanBeSimplified implements IRule {

    private final List<ExpandedQuadruple> expandedQuadruples;
    private String reason;
    
    public LinearEquationRuleFractionCanBeSimplified() {
        this.expandedQuadruples = new ArrayList<>();
    }
    
    @Override
    public boolean match(List<ThreeAddressCode> sources) {
        return isVariableEqualFraction(sources.get(0), sources.get(0).getLeft(), sources.get(0).getRight())
                || isVariableEqualFraction(sources.get(0), sources.get(0).getRight(), sources.get(0).getLeft());
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        expandedQuadruples.clear();
        
        List<Step> steps = new ArrayList<>();
        
        String left = "";
        String right = "";
        if (StringUtil.match(sources.get(0).getRight(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = sources.get(0).findQuadrupleByResult(sources.get(0).getRight());
            left = sources.get(0).getLeft();

            double a = getNumber(sources.get(0), expandedQuadruple.getArgument1());
            double b = getNumber(sources.get(0), expandedQuadruple.getArgument2());
            if (NumberUtil.isIntegerDivision(a, b)) {
                double result = a/b;
                right = generateParameter(result, 1);
                
                reason = "O resultado foi simplificado pois é uma divisão inteira.";
            } else {
                ExpandedQuadruple fraction = asFraction(a, b, 
                        isNegativeNumber(sources.get(0), expandedQuadruple.getArgument1()), 
                        isNegativeNumber(sources.get(0), expandedQuadruple.getArgument2()), 1);
                expandedQuadruples.add(fraction);
                right = fraction.getResult();
            }            
        } else {
            ExpandedQuadruple expandedQuadruple = sources.get(0).findQuadrupleByResult(sources.get(0).getLeft());
            right = sources.get(0).getRight();
            
            double a = getNumber(sources.get(0), expandedQuadruple.getArgument1());
            double b = getNumber(sources.get(0), expandedQuadruple.getArgument2());
            if (NumberUtil.isIntegerDivision(a, b)) {
                double result = a/b;
                left = generateParameter(result, 0);
                
                reason = "O resultado foi simplificado pois é uma divisão inteira.";
            } else {
                ExpandedQuadruple fraction = asFraction(a, b, 
                        isNegativeNumber(sources.get(0), expandedQuadruple.getArgument1()), 
                        isNegativeNumber(sources.get(0), expandedQuadruple.getArgument2()), 0);
                expandedQuadruples.add(fraction);
                left = fraction.getResult();
            } 
        }
        
        ThreeAddressCode step = new ThreeAddressCode(left, sources.get(0).getComparison(), right, expandedQuadruples);
        
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        
        steps.add(new Step(codes, step.toLaTeXNotation(), step.toMathNotation(), reason));
        
        return steps;
    }
    
    
    private String generateParameter(double value, int position) {
        String parameter = "";
        
        if (NumberUtil.isInteger(value)) {
            parameter = String.valueOf(NumberUtil.parseInt(value));
        } else {
            parameter = String.valueOf(value).replace(".", ",");
        }

        if (value < 0) {
            parameter = parameter.replace("-", "");
            ExpandedQuadruple expandedQuadruple = new ExpandedQuadruple("MINUS", parameter, "T" + (expandedQuadruples.size() + 1), position, 0);
            expandedQuadruples.add(expandedQuadruple);
            parameter = expandedQuadruple.getResult();
        }
        
        return parameter;
    }
    
    public ExpandedQuadruple asFraction(double a, double b, boolean aIsNegative, boolean bIsNegative, int position) {
        double greatestCommonDenominator = NumberUtil.greatestCommonDenominator(a, b);
        
        reason = "O resultado foi simplificado pelo número " + greatestCommonDenominator + ", que é MDC entre o numerador e o denomicador da fração.";
        
        a = (a / greatestCommonDenominator);
        b = (b / greatestCommonDenominator);
                
        ExpandedQuadruple negative = null;
        if (aIsNegative ^ bIsNegative) {
            negative = new ExpandedQuadruple("MINUS", getValue(a), "T" + (expandedQuadruples.size() + 1), position, 0);
            expandedQuadruples.add(negative);
        }
        
        if (negative != null) 
            return new ExpandedQuadruple("/", negative.getResult(), getValue(b), "T" + (expandedQuadruples.size() + 1), position, 0);
        
        return new ExpandedQuadruple("/", getValue(a), getValue(b), "T" + (expandedQuadruples.size() + 1), position, 0);
    }
    
    private String getValue(double value) {
        if (value < 0)
            value *= -1;
        if (NumberUtil.isInteger(value)) {
            return String.valueOf(NumberUtil.parseInt(value));
        }
        
        return String.valueOf(value);
    }
        
    private boolean isVariableEqualFraction(ThreeAddressCode threeAddressCode, String possibleVariable, String possibleFraction) {        
        return StringUtil.match(possibleVariable, RegexPattern.VARIABLE.toString()) 
                && !StringUtil.match(possibleVariable, RegexPattern.TEMPORARY_VARIABLE.toString()) 
                && isFractionThatCanBeSimplified(threeAddressCode, possibleFraction);
    }
    
    private boolean isFractionThatCanBeSimplified(ThreeAddressCode threeAddressCode, String possibleNumber) {
        ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(possibleNumber);

        if (expandedQuadruple != null && expandedQuadruple.isFraction() 
                && isNumber(threeAddressCode, expandedQuadruple.getArgument1())
                && isNumber(threeAddressCode, expandedQuadruple.getArgument2())) {            
            double a = getNumber(threeAddressCode, expandedQuadruple.getArgument1());
            double b = getNumber(threeAddressCode, expandedQuadruple.getArgument2());
            return NumberUtil.canBeSimplified(a, b);
        }
               
        return false;
    }
    
    private double getNumber(ThreeAddressCode threeAddressCode, String number) {
        if (StringUtil.match(number, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple innerOperation = threeAddressCode.findQuadrupleByResult(number);
            return Double.parseDouble(innerOperation.getArgument1()) * (-1);
        }
        
        return Double.parseDouble(number);
    }
    
    private boolean isNumber(ThreeAddressCode threeAddressCode, String possibleNumber) {
        return StringUtil.matchAny(possibleNumber, RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString()) 
                || isNegativeNumber(threeAddressCode, possibleNumber);
    }
    
    private boolean isNegativeNumber(ThreeAddressCode threeAddressCode, String possibleNumber) {
        if (StringUtil.match(possibleNumber, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple innerOperation = threeAddressCode.findQuadrupleByResult(possibleNumber);
            return innerOperation.isNegative() && StringUtil.matchAny(innerOperation.getArgument1(), RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString());
        }
        
        return false;
    }
}
