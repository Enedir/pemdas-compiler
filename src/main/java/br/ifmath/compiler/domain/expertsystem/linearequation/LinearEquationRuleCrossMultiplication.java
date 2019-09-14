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
import br.ifmath.compiler.infrastructure.util.MathOperatorUtil;
import br.ifmath.compiler.infrastructure.util.NumberUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alex_
 */
public class LinearEquationRuleCrossMultiplication implements IRule {

    private List<ExpandedQuadruple> expandedQuadruples;
    private int operationIndex;
    private boolean divisionIsntBothSides;
    
    private final LinearEquationRuleFinalResult linearEquationRuleFinalResult;
    
    public LinearEquationRuleCrossMultiplication(LinearEquationRuleFinalResult linearEquationRuleFinalResult) {
        this.linearEquationRuleFinalResult = linearEquationRuleFinalResult;
    }

    @Override
    public boolean match(List<ThreeAddressCode> sources) {
        return (isThereAFraction(sources.get(0), sources.get(0).getLeft())
                    || isThereAFraction(sources.get(0), sources.get(0).getRight()))
                && isThereOnlyOneTerm(sources.get(0), sources.get(0).getLeft())
                && isThereOnlyOneTerm(sources.get(0), sources.get(0).getRight())
                && !linearEquationRuleFinalResult.match(sources);
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();
        divisionIsntBothSides = false;
       
        expandedQuadruples = new ArrayList<>();
        expandedQuadruples.addAll(sources.get(0).getExpandedQuadruples());
        operationIndex = expandedQuadruples.size() + 1;
        
        String[] left = getFraction(sources.get(0), sources.get(0).getLeft());
        String[] right = getFraction(sources.get(0), sources.get(0).getRight());
        
        ExpandedQuadruple leftTimes = createTimesOperation(left[0], right[1], 0);
        expandedQuadruples.add(leftTimes);
        
        ExpandedQuadruple rightTimes = createTimesOperation(right[0], left[1], 1);        
        expandedQuadruples.add(rightTimes);
        
        ThreeAddressCode step1 = new ThreeAddressCode(leftTimes.getResult(), sources.get(0).getComparison(), rightTimes.getResult(), expandedQuadruples);
        eliminateNonUsedQuadruples(step1);
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step1);
        steps.add(new Step(codes, step1.toLaTeXNotation(), step1.toMathNotation(), 
                "O numerador da fração antes do sinal de comparação é multiplicado pelo denominador da fração após o sinal de comparação, assim como, "
                    + "o numerador da fração após o sinal de comparação é multiplicado pelo denominador da fração antes do sinal de comparação. "
                    + "Uma informação importante a ser destacada é que este é o princípio da regra de 3. " 
                    + (divisionIsntBothSides ? "Por fim, quando um operando não está em uma fração, assume-se que seu denominador é 1." : "")));
        
        expandedQuadruples = new ArrayList<>();
        expandedQuadruples.addAll(step1.getExpandedQuadruples());
        
        String leftOperation = multiply(step1, leftTimes);
        String rightOperation = multiply(step1, rightTimes);
                
        if (!leftOperation.equals(leftTimes.getResult()) || !rightOperation.equals(rightTimes.getResult())) {
            ThreeAddressCode step2 = new ThreeAddressCode(leftOperation, sources.get(0).getComparison(), rightOperation, expandedQuadruples);
            eliminateNonUsedQuadruples(step2);
            codes = new ArrayList<>();
            codes.add(step2);
            steps.add(new Step(codes, step2.toLaTeXNotation(), step2.toMathNotation(), "Multiplicação dos elementos."));        
        }
        
        return steps;
    }
    private void eliminateNonUsedQuadruples(ThreeAddressCode source) {
        List<ExpandedQuadruple> toRemove = new ArrayList<>();
        
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            boolean used = false;
            
            if (expandedQuadruple != null) {
                for (int i = 0; i < expandedQuadruples.size(); i++) {
                    ExpandedQuadruple quadruple = expandedQuadruples.get(i);
                    if (quadruple != null && (expandedQuadruple.getResult().equals(quadruple.getArgument1()) 
                            || expandedQuadruple.getResult().equals(quadruple.getArgument2()))) {
                        used = true;
                        break;
                    }
                }

                used = used || expandedQuadruple.getResult().equals(source.getLeft()) 
                            || expandedQuadruple.getResult().equals(source.getRight());
            }
            
            if(!used) {
                toRemove.add(expandedQuadruple);
            }
        }
        
        expandedQuadruples.removeAll(toRemove);
    }
    
    private String multiply(ThreeAddressCode source, ExpandedQuadruple times) {
        String a = "", b = "";
        boolean changeSignal = false;
        
        if (StringUtil.match(times.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple innerOperation = source.findQuadrupleByResult(times.getArgument1());
            if (innerOperation.isNegative() && !StringUtil.match(innerOperation.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                a = "-" + innerOperation.getArgument1();
                changeSignal = !changeSignal;
            }
        } else {
            a = times.getArgument1();
        }
        
        if (StringUtil.match(times.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple innerOperation = source.findQuadrupleByResult(times.getArgument2());
            if (innerOperation.isNegative() && !StringUtil.match(innerOperation.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                b = "-" + innerOperation.getArgument1();
                changeSignal = !changeSignal;
            }
        } else {
            b = times.getArgument2();
        }
        
        if (StringUtil.isEmpty(a) || StringUtil.isEmpty(b)) {
            return times.getResult();
        }
        
        expandedQuadruples.remove(times);
        
        double resultNumeric = MathOperatorUtil.times(a, b);
        String variable = getVariable(a, b);
        
        if (changeSignal && StringUtil.isNotEmpty(variable))
            resultNumeric *= -1;
        
        String result = "" + generateParameter(resultNumeric) + variable;
        if (resultNumeric < 0) { 
            return createNegativeNumberQuadruple(result, times.getPosition(), times.getLevel());
        }
         
        return result;
    }
    
    private String createNegativeNumberQuadruple(String parameter, int position, int level) {
        ExpandedQuadruple negativeQuadruple = new ExpandedQuadruple("MINUS", parameter, "T" + (this.operationIndex++), position, level);
        this.expandedQuadruples.add(negativeQuadruple);
        
        return negativeQuadruple.getResult();
    }
    
    private String generateParameter(double value) {
        if (NumberUtil.isInteger(value))
            return String.valueOf(NumberUtil.parseInt(value)).replace("-", "");
        else
            return String.valueOf(value).replace(".", ",").replace("-", "");
    }
    
    private String getVariable(String a, String b) {
        a = StringUtil.removeNumericChars(a).replace("-", "").replace(",", "").replace(".", "");
        if (StringUtil.isNotEmpty(a))
            return a;
        
        b = StringUtil.removeNumericChars(b).replace("-", "").replace(",", "").replace(".", "");
        if (StringUtil.isNotEmpty(b))
            return b;
        
        return "";
    }
    
    private ExpandedQuadruple createTimesOperation(String param1, String param2, int position) {
        return new ExpandedQuadruple("*", createNegativeNumberQuadrupleIfNecessary(param1), param2, "T" + (this.operationIndex++), position, 0);
    }
    
    private String[] getFraction(ThreeAddressCode source, String param) {
        if (!StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            divisionIsntBothSides = true;
            return new String[] { param, "1"};
        }
        
        ExpandedQuadruple expandedQuadruple = source.findQuadrupleByResult(param);
                
        if (expandedQuadruple.isNegative() && StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple innerOperation = source.findQuadrupleByResult(expandedQuadruple.getArgument1());

            if (innerOperation.isFraction()) {
                expandedQuadruples.remove(expandedQuadruple);
                expandedQuadruples.remove(innerOperation);

                return new String[] { "-" + innerOperation.getArgument1(), innerOperation.getArgument2()};
            }

        }
            
        if (expandedQuadruple.isFraction()) {
            expandedQuadruples.remove(expandedQuadruple);

            return new String[] { expandedQuadruple.getArgument1(), expandedQuadruple.getArgument2()};
        }
        
        divisionIsntBothSides = true;
        return new String[] { expandedQuadruple.getResult(), "1"};
    }
    
    private String createNegativeNumberQuadrupleIfNecessary(String parameter) {
        if (!parameter.startsWith("-"))
            return parameter;
        
        ExpandedQuadruple negativeQuadruple = new ExpandedQuadruple("MINUS", parameter, "T" + (this.operationIndex++), 0, 0);
        this.expandedQuadruples.add(negativeQuadruple);
        
        return negativeQuadruple.getResult();
    }
      
    private boolean isThereAFraction(ThreeAddressCode threeAddressCode, String param) {
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(param);
            
            if (expandedQuadruple.isNegative() && StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple innerOperation = threeAddressCode.findQuadrupleByResult(expandedQuadruple.getArgument1());
                return innerOperation.isFraction();
            }
            
            return expandedQuadruple.isFraction();
        }
        
        return false;
    }    
    
    private boolean isThereOnlyOneTerm(ThreeAddressCode threeAddressCode, String param) {
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(param);
            
            if (expandedQuadruple.isNegative()) {
                
                if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    ExpandedQuadruple innerOperation = threeAddressCode.findQuadrupleByResult(expandedQuadruple.getArgument1());
                    return innerOperation.isFraction()
                            && !StringUtil.match(innerOperation.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString());
                }
                
                return true;
            }
            
            return expandedQuadruple.isFraction()
                    || !StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString());
        }
        
        return true;
    }    
    
    
}
