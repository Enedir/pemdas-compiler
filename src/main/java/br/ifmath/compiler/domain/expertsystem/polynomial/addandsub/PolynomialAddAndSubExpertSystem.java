package br.ifmath.compiler.domain.expertsystem.polynomial.addandsub;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.input.ValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.NumberUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PolynomialAddAndSubExpertSystem implements IExpertSystem {


    public PolynomialAddAndSubExpertSystem() {

    }


    @Override
    public IAnswer findBestAnswer(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();

        AnswerPolynomialAddAndSub answer = new AnswerPolynomialAddAndSub(steps);

        steps.add(new Step(sources, sources.get(0).toLaTeXNotation(), sources.get(0).toMathNotation(), "Equação inicial."));


        sources = substituteNullFields(sources);

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
        }
    }

    @Override
    public void setVariables(List<NumericValueVariable> variables) {
        //FIXME: Utilizar para as outras expressoes polinomiais
//        for (ValueVariable variable : variables) {
//            this.substituteVariable.Add((NumericValueVariable) variable);
//        }
    }


    private void getFinalResult(AnswerPolynomialAddAndSub answer, ThreeAddressCode threeAddressCode, String possibleNumber) {
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
     * @return o proprio parametro sources, ja alterados
     */
    private List<ThreeAddressCode> substituteNullFields(List<ThreeAddressCode> sources) {
        for (ExpandedQuadruple expandedQuadruple : sources.get(0).getExpandedQuadruples()) {
            if (expandedQuadruple.getArgument1() == null)
                expandedQuadruple.setArgument1("");
            if (expandedQuadruple.getArgument2() == null)
                expandedQuadruple.setArgument2("");
            if (expandedQuadruple.getOperator() == null)
                expandedQuadruple.setOperator("");
        }
        return sources;
    }
}
