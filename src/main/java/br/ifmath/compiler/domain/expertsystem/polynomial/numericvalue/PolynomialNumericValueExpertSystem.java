package br.ifmath.compiler.domain.expertsystem.polynomial.numericvalue;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.Monomial;
import br.ifmath.compiler.infrastructure.input.Polynomial;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PolynomialNumericValueExpertSystem implements IExpertSystem {
    private static PolynomialRuleSubstituteVariables substituteVariable;
    private static PolynomialRuleSumNumbers sumNumbers;
    private static PolynomialRuleMultiplyNumbers multiplyNumbers;
    private static PolynomialRuleNumbersPotentiation powerNumbers;
    private static PolynomialRuleParenthesesOperations parenOperations;

    public PolynomialNumericValueExpertSystem() {
        substituteVariable = new PolynomialRuleSubstituteVariables();
        sumNumbers = new PolynomialRuleSumNumbers();
        multiplyNumbers = new PolynomialRuleMultiplyNumbers();
        powerNumbers = new PolynomialRuleNumbersPotentiation();
        parenOperations = new PolynomialRuleParenthesesOperations();
    }


    @Override
    public IAnswer findBestAnswer(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();

        AnswerPolynomialNumericValue answer = new AnswerPolynomialNumericValue(steps);

        sources.get(0).setUp();

        steps.add(new Step(sources, sources.get(0).toLaTeXNotation().trim(), sources.get(0).toMathNotation().trim(), "Equação inicial."));

        validateExpressions(sources);
        if (substituteVariable.match(sources)) {
            steps.addAll(substituteVariable.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        validateExpressions(sources);
        if (parenOperations.match(sources)) {
            steps.addAll(parenOperations.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        validateExpressions(sources);
        if (powerNumbers.match(sources)) {
            steps.addAll(powerNumbers.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        validateExpressions(sources);
        if (multiplyNumbers.match(sources)) {
            steps.addAll(multiplyNumbers.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        validateExpressions(sources);
        if (sumNumbers.match(sources)) {
            steps.addAll(sumNumbers.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        substituteNullFields(sources);

        if (StringUtil.isVariable(sources.get(0).getLeft())) {
            getFinalResult(answer, sources.get(0), sources.get(0).getRight());
        } else {
            getFinalResult(answer, sources.get(0), sources.get(0).getLeft());
        }

        return answer;
    }

    @Override
    public IAnswer findPossibleHandles(List<ThreeAddressCode> sources) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    @Override
    public void validateExpressions(List<ThreeAddressCode> sources) {
        List<String> variables = new ArrayList<>();

        StringUtil.validateVariable(sources.get(0).getLeft(), variables);

        StringUtil.validateVariable(sources.get(0).getRight(), variables);

        for (ExpandedQuadruple expandedQuadruple : sources.get(0).getExpandedQuadruples()) {
            StringUtil.validateVariable(expandedQuadruple.getArgument1(), variables);

            StringUtil.validateVariable(expandedQuadruple.getArgument2(), variables);
        }
    }

    @Override
    public void setVariables(List<Monomial> variables) {
        for (Polynomial variable : variables) {
            substituteVariable.Add((Monomial) variable);
        }
    }

    private void getFinalResult(AnswerPolynomialNumericValue answer, ThreeAddressCode threeAddressCode, String possibleNumber) {
        ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(possibleNumber);
        if (expandedQuadruple == null) {
            answer.setResult(possibleNumber.replace(",", "."));
            return;
        }

        if (expandedQuadruple.isNegative()) {
            if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple innerOperation = threeAddressCode.findQuadrupleByResult(expandedQuadruple.getArgument1());
                answer.setResult("-" + innerOperation.getArgument1().replace(",", ".") + "/" + innerOperation.getArgument2().replace(",", "."));
            } else {
                answer.setResult("-" + expandedQuadruple.getArgument1().replace(",", "."));
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

            answer.setResult(numerator + "/" + denominator);
        }
    }

    /**
     * Substitui caso os argumentos ou o operador sejam nulos, por ""
     *
     * @param sources a {@link List} de {@link ThreeAddressCode} com o polinomio
     */
    private void substituteNullFields(List<ThreeAddressCode> sources) {
        for (ExpandedQuadruple expandedQuadruple : sources.get(0).getExpandedQuadruples()) {
            if (expandedQuadruple.getArgument1() == null)
                expandedQuadruple.setArgument1("");
            if (expandedQuadruple.getArgument2() == null)
                expandedQuadruple.setArgument2("");
            if (expandedQuadruple.getOperator() == null)
                expandedQuadruple.setOperator("");
        }
    }
}
