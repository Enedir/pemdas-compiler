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
public class LinearEquationRuleNegativeVariable implements IRule {

    private List<ExpandedQuadruple> expandedQuadruples;
    private int operationIndex;

    @Override
    public boolean match(List<ThreeAddressCode> sources) {
        return (isThereNegativeVariable(sources.get(0), sources.get(0).getLeft(), false)
                    || isThereNegativeVariable(sources.get(0), sources.get(0).getRight(), false)) 
                && isThereOnlyOneVariable(sources.get(0));
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        this.expandedQuadruples = new ArrayList<>();
        this.expandedQuadruples.addAll(sources.get(0).getExpandedQuadruples());
        operationIndex = maxIndex(sources.get(0).getExpandedQuadruples()) + 1;
        
        List<Step> steps = new ArrayList<>();
         
        String left = addMultiplication(sources.get(0).getLeft(), 0);
        String right = addMultiplication(sources.get(0).getRight(), 1);
        
        double leftValue = sumTerms(sources.get(0), sources.get(0).getLeft(), false);
        double rightValue = sumTerms(sources.get(0), sources.get(0).getRight(), false);
        
        String leftVariable = findVariable(sources.get(0), sources.get(0).getLeft());
        String rightVariable = findVariable(sources.get(0), sources.get(0).getRight());
        
        ThreeAddressCode step1 = new ThreeAddressCode(left, sources.get(0).getComparison(), right, expandedQuadruples);
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step1);
        steps.add(new Step(codes, step1.toLaTeXNotation(), step1.toMathNotation(), "Multiplicação de todos os termos da equação por -1 pois a variável é negativa."));
        
        leftValue *= -1;
        rightValue *= -1;
        
        left = generateParameter(leftValue, leftVariable, 0);
        right = generateParameter(rightValue, rightVariable, 1);
        
        ThreeAddressCode step2 = new ThreeAddressCode(left, sources.get(0).getComparison(), right, expandedQuadruples);
        codes = new ArrayList<>();
        codes.add(step2);
        steps.add(new Step(codes, step2.toLaTeXNotation(), step2.toMathNotation(), "Ao multiplicar todos os termos da equação por -1, as operações realizadas sobre os termos são invertidas para uma inversamente proporcional."));
        
        return steps;
    }
    
    private String addMultiplication(String param, int position) {
        String multiplier = createNegativeNumberQuadruple("1", position, 1);
         
        ExpandedQuadruple times = new ExpandedQuadruple("*", param, multiplier, "T" + (this.operationIndex++), position, 0);
        this.expandedQuadruples.add(times);
        
        return times.getResult();
    }
        
    private String createNegativeNumberQuadruple(String parameter, int position, int level) {
        ExpandedQuadruple negativeQuadruple = new ExpandedQuadruple("MINUS", parameter, "T" + (this.operationIndex++), position, level);
        this.expandedQuadruples.add(negativeQuadruple);
        
        return negativeQuadruple.getResult();
    }
    
    private int maxIndex(List<ExpandedQuadruple> quadruples) {
        int maxIndex = 1;
        for (ExpandedQuadruple quadruple : quadruples) {
            int index = Integer.parseInt(StringUtil.removeNonNumericChars(quadruple.getResult()));
            if (index > maxIndex)
                maxIndex = index;
        }
        
        return maxIndex;
    }
    private String generateParameter(double value, String variable, int position) {
        String parameter = "";
        
        if ((value != 1 && value != -1) || StringUtil.isEmpty(variable)) {
            if (NumberUtil.isInteger(value)) {
                parameter = String.valueOf(NumberUtil.parseInt(value));
            } else {
                parameter = String.valueOf(value).replace(".", ",");
            }
        }
        
        if (StringUtil.isNotEmpty(variable)) {
            parameter += variable;
        }
        
        if (value < 0) {
            parameter = parameter.replace("-", "");
            ExpandedQuadruple expandedQuadruple = new ExpandedQuadruple("MINUS", parameter, "T" + (expandedQuadruples.size() + 1), position, 0);
            expandedQuadruples.add(expandedQuadruple);
            parameter = expandedQuadruple.getResult();
        }
        
        return parameter;
    }
    
    private double sumTerms(ThreeAddressCode threeAddressCode, String param, boolean lastOperationIsMinus) {
        double sum = 0;
        
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(param);
            
            if (expandedQuadruple.isNegative()) {
                sum -= sumTerms(threeAddressCode, expandedQuadruple.getArgument1(), false);
            } else {
                sum += sumTerms(threeAddressCode, expandedQuadruple.getArgument1(), lastOperationIsMinus);
                sum += sumTerms(threeAddressCode, expandedQuadruple.getArgument2(), expandedQuadruple.isMinus());
            }
        } else if (isVariable(param)) {
            String aux = StringUtil.removeNonNumericChars(param);
            if (StringUtil.isEmpty(aux)) {
                if (lastOperationIsMinus)
                    sum -= 1;
                else
                    sum += 1;
            } else {
                if (lastOperationIsMinus)
                    sum -= Double.parseDouble(aux.replace(",", "."));
                else
                    sum += Double.parseDouble(aux.replace(",", "."));
            }
        } else {
            if (lastOperationIsMinus)
                sum -= Double.parseDouble(param.replace(",", "."));
            else
                sum += Double.parseDouble(param.replace(",", "."));
        }
        
        return sum;
    }
    
    
    private String findVariable(ThreeAddressCode threeAddressCode, String param) {
        String variable = null;
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(param);
            
            variable = findVariable(threeAddressCode, expandedQuadruple.getArgument1());
                
            if (StringUtil.isEmpty(variable)) {
                variable = findVariable(threeAddressCode, expandedQuadruple.getArgument2());
            }
        } else if (isVariable(param)) {
            variable = StringUtil.removeNumericChars(param);
        }
        
        return variable;
    }
    
    private boolean isThereOnlyOneVariable(ThreeAddressCode threeAddressCode) {
        int variableAmount = 0;
        
        if (isVariable(threeAddressCode.getLeft())) { 
            variableAmount++;
        } 
                
        if (isVariable(threeAddressCode.getRight())) { 
            variableAmount++;
        } 
        
        for (ExpandedQuadruple expandedQuadruple : threeAddressCode.getExpandedQuadruples()) {            
            if (isVariable(expandedQuadruple.getArgument1())) { 
                variableAmount++;
            }

            if (isVariable(expandedQuadruple.getArgument2())) { 
                variableAmount++;
            }             
        }
        
        return variableAmount == 1;
    }
    
    private boolean isVariable(String param) {
        return StringUtil.matchAny(param, RegexPattern.VARIABLE.toString(), RegexPattern.VARIABLE_WITH_COEFICIENT.toString())
                    && !StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString());
    }        
    
    private boolean isThereNegativeVariable(ThreeAddressCode threeAddressCode, String param, boolean lastOperationIsMinus) {        
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) { 
            ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(param);
            
            if (isVariable(expandedQuadruple.getArgument1())) {
                if (expandedQuadruple.isNegative() || lastOperationIsMinus)
                    return true;
            }
            
            if (isVariable(expandedQuadruple.getArgument2())) {
                if (expandedQuadruple.isMinus())
                    return true;
            }
            
            return isThereNegativeVariable(threeAddressCode, expandedQuadruple.getArgument1(), lastOperationIsMinus) 
                    || isThereNegativeVariable(threeAddressCode, expandedQuadruple.getArgument2(), expandedQuadruple.isMinus());
        }
        
        return false;
    }
}
