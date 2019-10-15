/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.expertsystem.linearequation;


import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.NumberUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alex_
 */
public class LinearEquationExpertSystem implements IExpertSystem {

    private final LinearEquationRuleCrossMultiplication crossMultiplication;
    private final LinearEquationRuleLeastCommonMultiple leastCommonMultiple;
    private final LinearEquationRuleDistributive distributive;
    private final LinearEquationRuleShiftSignal shiftSignal;
    private final LinearEquationRuleIsolateTerms isolateTerms;
    private final LinearEquationRuleSumTerms sumTerms;
    private final LinearEquationRuleNegativeVariable negativeVariable;
    private final LinearEquationRuleGeneralWithCoeficientInVariable generalWithCoeficientInVariable;
    private final LinearEquationRuleFractionCanBeSimplified fractionCanBeSimplified;
    private final LinearEquationRuleFinalResult finalResult;

    public LinearEquationExpertSystem() {
        finalResult = new LinearEquationRuleFinalResult();
        crossMultiplication = new LinearEquationRuleCrossMultiplication(finalResult);
        leastCommonMultiple = new LinearEquationRuleLeastCommonMultiple(finalResult);
        distributive = new LinearEquationRuleDistributive();
        shiftSignal = new LinearEquationRuleShiftSignal();
        isolateTerms = new LinearEquationRuleIsolateTerms();
        sumTerms = new LinearEquationRuleSumTerms();
        negativeVariable = new LinearEquationRuleNegativeVariable();
        generalWithCoeficientInVariable = new LinearEquationRuleGeneralWithCoeficientInVariable();
        fractionCanBeSimplified = new LinearEquationRuleFractionCanBeSimplified();
    }

    @Override
    public IAnswer findBestAnswer(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();

        AnswerLinearEquation answer = new AnswerLinearEquation(steps);

        steps.add(new Step(sources, sources.get(0).toLaTeXNotation(), sources.get(0).toMathNotation(), "Equação inicial."));

        validateExpressions(sources);
        if (crossMultiplication.match(sources)) {
            steps.addAll(crossMultiplication.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        validateExpressions(sources);
        if (leastCommonMultiple.match(sources)) {
            steps.addAll(leastCommonMultiple.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        validateExpressions(sources);
        while (distributive.match(sources)) {
            steps.addAll(distributive.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        validateExpressions(sources);
        if (shiftSignal.match(sources)) {
            steps.addAll(shiftSignal.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        validateExpressions(sources);
        if (isolateTerms.match(sources)) {
            steps.addAll(isolateTerms.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        validateExpressions(sources);
        if (sumTerms.match(sources)) {
            steps.addAll(sumTerms.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        getFunction(answer, sources.get(0));

        validateExpressions(sources);
        if (negativeVariable.match(sources)) {
            steps.addAll(negativeVariable.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        validateExpressions(sources);
        if (generalWithCoeficientInVariable.match(sources)) {
            steps.addAll(generalWithCoeficientInVariable.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        validateExpressions(sources);
        if (fractionCanBeSimplified.match(sources)) {
            steps.addAll(fractionCanBeSimplified.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        validateExpressions(sources);
        if (finalResult.match(sources)) {
            steps.addAll(finalResult.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        if (StringUtil.isVariable(sources.get(0).getLeft())) {
            getFinalResult(answer, sources.get(0), sources.get(0).getRight());
        } else {
            getFinalResult(answer, sources.get(0), sources.get(0).getLeft());
        }

        return answer;
    }

    @Override
    public IAnswer findPossibleHandles(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void validateExpressions(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        List<String> variables = new ArrayList<>();
        double coeficient = 0d;
        String variable;
 
        if (StringUtil.isEmpty(sources.get(0).getComparison())) {
            throw new InvalidAlgebraicExpressionException("Equação deve possuir uma igualdade.");
        }
        
        if (StringUtil.isVariable(sources.get(0).getLeft())) {
            variable = StringUtil.getVariable(sources.get(0).getLeft());
            coeficient += NumberUtil.getVariableCoeficient(sources.get(0).getLeft());
            if (StringUtil.isNotEmpty(variable) && !variables.contains(variable))
                variables.add(variable);
        }

        if (StringUtil.isVariable(sources.get(0).getRight())) {
            variable = StringUtil.getVariable(sources.get(0).getRight());
            coeficient += NumberUtil.getVariableCoeficient(sources.get(0).getRight());
            if (StringUtil.isNotEmpty(variable) && !variables.contains(variable))
                variables.add(variable);
        }

        for (ExpandedQuadruple expandedQuadruple : sources.get(0).getExpandedQuadruples()) {
            if (StringUtil.isVariable(expandedQuadruple.getArgument1())) {
                variable = StringUtil.getVariable(expandedQuadruple.getArgument1());
                coeficient += NumberUtil.getVariableCoeficient(expandedQuadruple.getArgument1());
                if (StringUtil.isNotEmpty(variable) && !variables.contains(variable))
                    variables.add(variable);
            }

            if (StringUtil.isVariable(expandedQuadruple.getArgument2())) {
                variable = StringUtil.getVariable(expandedQuadruple.getArgument2());
                coeficient += NumberUtil.getVariableCoeficient(expandedQuadruple.getArgument2());
                if (StringUtil.isNotEmpty(variable) && !variables.contains(variable))
                    variables.add(variable);
            }
            
            if (!expandedQuadruple.isPlus()
                    && !expandedQuadruple.isFraction()
                    && !expandedQuadruple.isTimes()
                    && !expandedQuadruple.isMinusOrNegative()) {
                throw new InvalidAlgebraicExpressionException("A expressão deve possuir apenas as seguintes operações: +, -, * e /.");
            }
        }

        if (variables.isEmpty() || coeficient == 0)
            throw new InvalidAlgebraicExpressionException("A expressão não possui nenhuma variável.");

        if (variables.size() > 1)
            throw new InvalidAlgebraicExpressionException("A expressão possui mais que uma variável.");
    }


    private void getFunction(AnswerLinearEquation answer, ThreeAddressCode sources) {
        double leftValue = sumTerms(sources, sources.getLeft(), false);
        double rightValue = sumTerms(sources, sources.getRight(), false);

        String leftVariable = findVariable(sources, sources.getLeft());

        if (StringUtil.isEmpty(leftVariable)) {
            leftValue *= -1;

            answer.setA(String.valueOf(rightValue));
            answer.setB(String.valueOf(leftValue));
        } else {
            rightValue *= -1;

            answer.setA(String.valueOf(leftValue));
            answer.setB(String.valueOf(rightValue));
        }
    }

    private void getFinalResult(AnswerLinearEquation answer, ThreeAddressCode threeAddressCode, String possibleNumber) {
        ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(possibleNumber);
        if (expandedQuadruple == null) {
            answer.setX(possibleNumber.replace(",", "."));
            return;
        }

        if (expandedQuadruple.isNegative()) {
            if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple innerOperation = threeAddressCode.findQuadrupleByResult(expandedQuadruple.getArgument1());
                answer.setX("-" + innerOperation.getArgument1().replace(",", ".") + "/" + innerOperation.getArgument2().replace(",", "."));
            } else {
                answer.setX("-" + expandedQuadruple.getArgument1().replace(",", "."));
            }
        } else {
            String numerator;
            if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple innerOperation = threeAddressCode.findQuadrupleByResult(expandedQuadruple.getArgument1());
                numerator = "-" + innerOperation.getArgument1().replace(",", ".");
            } else {
                numerator = expandedQuadruple.getArgument1().replace(",", ".");
            }

            String denominator;
            if (StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple innerOperation = threeAddressCode.findQuadrupleByResult(expandedQuadruple.getArgument2());
                denominator = "-" + innerOperation.getArgument1().replace(",", ".");
            } else {
                denominator = expandedQuadruple.getArgument2().replace(",", ".");
            }

            answer.setX(numerator + "/" + denominator);
        }
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
        } else if (StringUtil.isVariable(param)) {
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
        } else if (StringUtil.isVariable(param)) {
            variable = StringUtil.removeNumericChars(param);
        }

        return variable;
    }

}
