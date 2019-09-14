package br.ifmath.compiler.domain.expertsystem.polynomial;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PolynomialExpertSystem implements IExpertSystem {
    private static PolynomialRuleSubstituteVariables substituteVariable;
    private static PolynomialRuleSumNumbers sumNumbers;
    private static PolynomialRuleGroupSimilarTerms groupTerms;
    private static PolynomialRuleMultiplyNumber multiplyNumbers;

    public PolynomialExpertSystem() {
        substituteVariable = new PolynomialRuleSubstituteVariables();
        sumNumbers = new PolynomialRuleSumNumbers();
        groupTerms = new PolynomialRuleGroupSimilarTerms();
        multiplyNumbers = new PolynomialRuleMultiplyNumber();
    }


    @Override
    public IAnswer findBestAnswer(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();

        AnswerPolynomial answer = new AnswerPolynomial(steps);

        steps.add(new Step(sources, sources.get(0).toLaTeXNotation(), sources.get(0).toMathNotation(), "Equação inicial."));

//        validateExpressions(sources);
//        if (groupTerms.match(sources)) {
//            steps.addAll(groupTerms.handle(sources));
//            sources = steps.get(steps.size() - 1).getSource();
//        }


        validateExpressions(sources);
        if (substituteVariable.match(sources)) {
            steps.addAll(substituteVariable.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        validateExpressions(sources);
        if (multiplyNumbers.match(sources)) {
            steps.addAll(multiplyNumbers.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        if (sumNumbers.match(sources)) {
            steps.addAll(sumNumbers.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }




        if (isVariable(sources.get(0).getLeft())) {
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

        if (isVariable(sources.get(0).getLeft())) {
            variable = getVariable(sources.get(0).getLeft());
            coeficient += getVariableCoeficient(sources.get(0).getLeft());
            if (StringUtil.isNotEmpty(variable) && !variables.contains(variable))
                variables.add(variable);
        }

        if (isVariable(sources.get(0).getRight())) {
            variable = getVariable(sources.get(0).getRight());
            coeficient += getVariableCoeficient(sources.get(0).getRight());
            if (StringUtil.isNotEmpty(variable) && !variables.contains(variable))
                variables.add(variable);
        }

        for (ExpandedQuadruple expandedQuadruple : sources.get(0).getExpandedQuadruples()) {
            if (isVariable(expandedQuadruple.getArgument1())) {
                variable = getVariable(expandedQuadruple.getArgument1());
                coeficient += getVariableCoeficient(expandedQuadruple.getArgument1());
                if (StringUtil.isNotEmpty(variable) && !variables.contains(variable))
                    variables.add(variable);
            }

            if (isVariable(expandedQuadruple.getArgument2())) {
                variable = getVariable(expandedQuadruple.getArgument2());
                coeficient += getVariableCoeficient(expandedQuadruple.getArgument2());
                if (StringUtil.isNotEmpty(variable) && !variables.contains(variable))
                    variables.add(variable);
            }
        }

        if (variables.isEmpty() || coeficient == 0)
            throw new InvalidAlgebraicExpressionException("A expressão não possui nenhuma variável.");

    }

    private boolean isVariable(String param) {
        return StringUtil.matchAny(param, RegexPattern.VARIABLE.toString(), RegexPattern.VARIABLE_WITH_COEFICIENT.toString())
                && !StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString());
    }

    private String getVariable(String param) {
        return StringUtil.removeNumericChars(param);
    }

    private double getVariableCoeficient(String param) {
        String coeficient = StringUtil.removeNonNumericChars(param).replace(",", ".");

        if (StringUtil.isDecimalNumber(coeficient))
            return Double.parseDouble(coeficient);

        return 1d;
    }

    private void getFinalResult(AnswerPolynomial answer, ThreeAddressCode threeAddressCode, String possibleNumber) {
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
}
