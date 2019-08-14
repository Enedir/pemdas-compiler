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
public class LinearEquationRuleLeastCommonMultiple implements IRule {

    private LinearEquationRuleFinalResult linearEquationRuleFinalResult;

    private List<Fraction> fractionsLeft;
    private List<Fraction> fractionsRight;
    private int operationIndex;
    private List<ExpandedQuadruple> expandedQuadruples;
    
    public LinearEquationRuleLeastCommonMultiple(LinearEquationRuleFinalResult linearEquationRuleFinalResult) {
        this.linearEquationRuleFinalResult = linearEquationRuleFinalResult;
    }

    @Override
    public boolean match(List<ThreeAddressCode> sources) {
        for (ExpandedQuadruple expandedQuadruple : sources.get(0).getExpandedQuadruples()) {
            if (expandedQuadruple.isFraction())
               return !linearEquationRuleFinalResult.match(sources);
        }
        
        return false;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();
        fractionsLeft = new ArrayList<>();
        fractionsRight = new ArrayList<>();
        
        
        expandedQuadruples = new ArrayList<>();
        expandedQuadruples.addAll(sources.get(0).getExpandedQuadruples());
        operationIndex = expandedQuadruples.size() + 1;
        
        splitFractions(sources.get(0), sources.get(0).getLeft(), 0, false, fractionsLeft);
        splitFractions(sources.get(0), sources.get(0).getRight(), 1, false, fractionsRight);

        List<Integer> denominators = new ArrayList<>();
        getAllDenominator(fractionsLeft, denominators);
        getAllDenominator(fractionsRight, denominators);

        long lcm = NumberUtil.leastCommonMultiple(denominators);

        String left = generateFirstStep(fractionsLeft, lcm);
        String right = generateFirstStep(fractionsRight, lcm);

        ThreeAddressCode step1 = new ThreeAddressCode(left, sources.get(0).getComparison(), right, expandedQuadruples);
        eliminateNonUsedQuadruples(step1);
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step1);
        steps.add(new Step(codes, step1.toLaTeXNotation(), step1.toMathNotation(), 
            "Calcula-se o MMC de todos os denominadores (lembre-se que quando um termo está em uma fração, seu denominador é 1). "
            + "Este número será o novo denominador de todos os termos da equação. "
            + "Lembre-se que quando trocamos o denominador de uma fração, o numerador precisa sofrer uma correção. "
            + "Este processo é conhecido como \"dividir pelo debaixo, multiplicar pelo de cima\"."));

        left = generateSecondStep(step1, fractionsLeft);
        right = generateSecondStep(step1, fractionsRight);

        ThreeAddressCode step2 = new ThreeAddressCode(left, sources.get(0).getComparison(), right, expandedQuadruples);
        eliminateNonUsedQuadruples(step2);
        codes = new ArrayList<>();
        codes.add(step2);
        steps.add(new Step(codes, step2.toLaTeXNotation(), step2.toMathNotation(), 
            "Após deixar todos membros sobre um mesmo denominador, os denominadores podem ser cancelados e as multiplicações são efetuadas"));
        
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
    
    private void getAllDenominator(List<Fraction> fractions, List<Integer> denominators) {
        fractions.forEach((fraction) -> {
            denominators.add(fraction.denominator);
        });
    }
    
    private String generateFirstStep(List<Fraction> fractions, long lcm) {
        String firstOperation = "";
        
        int index = 0;
        ExpandedQuadruple operation = null, lastOperation = null;
            
        for (Fraction fraction : fractions) {
            fraction.createTimes(lcm);

            if (index == 0 && !fraction.positive) {
                operation = new ExpandedQuadruple(createNegativeNumberQuadruple(fraction.fraction.getResult(), fraction.position, 0), 
                        "T" + (this.operationIndex++), fraction.position, 0);
            } else {
                if (index == (fractions.size() - 1)) {
                    operation.setOperator(fraction.positive ? "+" : "-");
                    operation.setArgument2(fraction.fraction.getResult());
                } else {
                    operation = new ExpandedQuadruple(fraction.fraction.getResult(), "T" + (this.operationIndex++), fraction.position, 0);

                    if (lastOperation != null) {
                        lastOperation.setOperator(fraction.positive ? "+" : "-");
                        lastOperation.setArgument2(operation.getResult());
                    }
                }
            }

            if (firstOperation.isEmpty()) {
                firstOperation = operation.getResult();
            }

            lastOperation = operation;
            expandedQuadruples.add(operation);
            index++;
        }
        
        return firstOperation;
    }

    private String generateSecondStep(ThreeAddressCode source, List<Fraction> fractions) {
        String firstOperation = "";
        
        int index = 0;
        ExpandedQuadruple operation = null, lastOperation = null;
            
        for (Fraction fraction : fractions) {
            boolean negative = fraction.multiply(source, index == 0);

            
            if (index == (fractions.size() - 1)) {
                operation.setOperator(negative ? "-" : "+");
                operation.setArgument2(fraction.result);
            } else {
                operation = new ExpandedQuadruple(fraction.result, "T" + (this.operationIndex++), fraction.position, 0);

                if (lastOperation != null) {
                    lastOperation.setOperator(negative ? "-" : "+");
                    lastOperation.setArgument2(operation.getResult());
                }
            }
            
            if (firstOperation.isEmpty()) {
                firstOperation = operation.getResult();
            }

            lastOperation = operation;
            if (!expandedQuadruples.contains(operation))
                expandedQuadruples.add(operation);
            
            index++;
        }
        
        return firstOperation;
    }
    
    private void splitFractions(ThreeAddressCode source, String initialOperation, int position, boolean lastOperationIsMinus, List<Fraction> fractions) throws InvalidAlgebraicExpressionException {
        ExpandedQuadruple operation = source.findQuadrupleByResult(initialOperation);
        
        String numerator = initialOperation;
        int denominator = 1;
        
        if (operation != null) {
            if (operation.isPlusOrMinus() && !operation.isNegative()) {
                splitFractions(source, operation.getArgument1(), position, lastOperationIsMinus, fractions);
                splitFractions(source, operation.getArgument2(), position, operation.isMinus(), fractions);
                
                expandedQuadruples.remove(operation);
                
                return;
            } else {
                if (operation.isFraction()) {
                    numerator = operation.getArgument1();
                    denominator = getDenominator(source, operation.getArgument2());
                    
                    expandedQuadruples.remove(operation);
                } else if (operation.isNegative() && StringUtil.match(operation.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())){
                    ExpandedQuadruple innerOperation = source.findQuadrupleByResult(operation.getArgument1());
                    
                    if (innerOperation.isFraction()) {
                        lastOperationIsMinus = true;
                        numerator = innerOperation.getArgument1();
                        denominator = getDenominator(source, innerOperation.getArgument2());
                        
                        expandedQuadruples.remove(innerOperation);
                        expandedQuadruples.remove(operation);
                    } else {                        
                        numerator = operation.getResult();
                    }
                }
            }
        }
        
        fractions.add(new Fraction(position, !lastOperationIsMinus, numerator, denominator));
    }

    private int getDenominator(ThreeAddressCode source, String possibleDenominator) throws InvalidAlgebraicExpressionException {
        if (StringUtil.match(possibleDenominator, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple innerOperation = source.findQuadrupleByResult(possibleDenominator);
                    
            if (innerOperation.isNegative() && StringUtil.match(innerOperation.getArgument1(), RegexPattern.INTEGER_NUMBER.toString())) {
                return -Integer.parseInt(innerOperation.getArgument1());
            }
        } else if (StringUtil.match(possibleDenominator, RegexPattern.INTEGER_NUMBER.toString())) {
            return Integer.parseInt(possibleDenominator);
        }
        
        throw new InvalidAlgebraicExpressionException("Os denominadores devem ser apenas numéros inteiros.");
    }

    private String generateParameter(double value) {
        if (NumberUtil.isInteger(value))
            return String.valueOf(NumberUtil.parseInt(value)).replace("-", "");
        else
            return String.valueOf(value).replace(".", ",").replace("-", "");
    }
    
    private String createNegativeNumberQuadruple(String parameter, int position, int level) {
        ExpandedQuadruple negativeQuadruple = new ExpandedQuadruple("MINUS", parameter, "T" + (this.operationIndex++), position, level);
        this.expandedQuadruples.add(negativeQuadruple);
        
        return negativeQuadruple.getResult();
    }
    
    private String getVariable(String a) {
        a = StringUtil.removeNumericChars(a).replace("-", "").replace(",", "").replace(".", "");
        if (StringUtil.isNotEmpty(a))
            return a;
        
        return "";
    }
    
    private class Fraction {
        
        public int position;
        public boolean positive;
        public String numerator;
        public int denominator;
        public long multiplier;
        public String result;
        
        private ExpandedQuadruple times;
        private ExpandedQuadruple fraction;

        public Fraction(int position, boolean positive, String numerator, int denominator) {
            this.position = position;
            this.positive = positive;
            this.numerator = numerator;
            this.denominator = denominator;
        }

        @Override
        public String toString() {
            return "+: " + positive + " (" + numerator + "/" + denominator + ")" + " P: " + position;
        }
        
        public void createTimes(long multiplier) {
            this.multiplier = multiplier / denominator;
            
            if (this.multiplier < 0) {
                times = new ExpandedQuadruple("*", createNegativeNumberQuadruple(generateParameter(this.multiplier), position, 1), numerator, "T" + (operationIndex++), position, 0);
            } else {
                times = new ExpandedQuadruple("*", generateParameter(this.multiplier), numerator, "T" + (operationIndex++), position, 0);
            }
                        
            expandedQuadruples.add(times);
            
            fraction = new ExpandedQuadruple("/", times.getResult(), generateParameter(multiplier), "T" + (operationIndex++), position, 0);
            expandedQuadruples.add(fraction);
        }
        
        public boolean multiply(ThreeAddressCode source, boolean firstOperation) {
            String a = "";

            if (StringUtil.match(numerator, RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple innerOperation = source.findQuadrupleByResult(numerator);
                if (innerOperation.isNegative() && !StringUtil.match(innerOperation.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    a = "-" + innerOperation.getArgument1();
                }
            } else {
                a = numerator;
            }
            
            expandedQuadruples.remove(fraction);
            
            if (StringUtil.isEmpty(a)) {
                result = times.getResult();
                if (!positive && firstOperation) { 
                    result = createNegativeNumberQuadruple(result, times.getPosition(), times.getLevel());
                }
                
                return !positive;
            }

            expandedQuadruples.remove(times);

            double resultNumeric = MathOperatorUtil.times(a, String.valueOf(multiplier));
            result = "" + generateParameter(resultNumeric) + getVariable(a);
            
            if (!positive && multiplier >= 0)
                resultNumeric *= (-1);
            
            if (firstOperation && resultNumeric < 0) { 
                result = createNegativeNumberQuadruple(result, times.getPosition(), times.getLevel());
                return false;
            }
            
            return resultNumeric < 0;
        }
        
    }
    
}
