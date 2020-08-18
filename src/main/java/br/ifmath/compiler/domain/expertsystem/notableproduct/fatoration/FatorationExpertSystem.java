package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration;


import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.commonfactorandgroup.FatorationRuleCommonFactorAndGroup;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.perfectsquaretrinomial.FatorationRulePerfectSquareTrinomialExpandedFormulaConversion;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.perfectsquaretrinomial.FatorationRulePerfectSquareTrinomialSumSquare;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.NumberUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FatorationExpertSystem implements IExpertSystem {

    private FatorationRuleIdentification identification;

    private FatorationRuleCommonFactorAndGroup commonFactor;

    private FatorationRulePerfectSquareTrinomialExpandedFormulaConversion trinomialFormula;

    private FatorationRulePerfectSquareTrinomialSumSquare trinomialSumSquare;

    public FatorationExpertSystem() {
        this.identification = new FatorationRuleIdentification();
        this.commonFactor = new FatorationRuleCommonFactorAndGroup();
        this.trinomialFormula = new FatorationRulePerfectSquareTrinomialExpandedFormulaConversion();
        this.trinomialSumSquare = new FatorationRulePerfectSquareTrinomialSumSquare();
    }

    @Override
    public IAnswer findBestAnswer(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();

        AnswerFatoration answer = new AnswerFatoration(steps);

        sources.get(0).setUp();

        setUpQuadruples(sources);

        /*TODO Pensar no caso de colocar uma variável como uma flag, que no identification vai identificar qual regra vai
           entrar. Isso para não precisar ficar verificando a mesma coisa antes de entrar em toda regra.
         */

        validateExpressions(sources);
        if (identification.match(sources)) {
            steps.addAll(identification.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        validateExpressions(sources);
        if (trinomialFormula.match(sources)) {
            steps.addAll(trinomialFormula.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();

            validateExpressions(sources);
            if (trinomialSumSquare.match(sources)) {
                steps.addAll(trinomialSumSquare.handle(sources));
                sources = steps.get(steps.size() - 1).getSource();
            }

        } else if (commonFactor.match(sources)) {
            steps.addAll(commonFactor.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        substituteNullFields(sources);

        if (StringUtil.isVariable(sources.get(0).getLeft())) {
            this.getFinalResult(answer, sources.get(0), sources.get(0).getRight());
        } else {
            this.getFinalResult(answer, sources.get(0), sources.get(0).getLeft());
        }

        return answer;
    }


    private void setUpQuadruples(List<ThreeAddressCode> source) {
        setUpExponent(source, source.get(0).getLeft());
        source.get(0).handlePotentiation();
        source.get(0).clearNonUsedQuadruples();
    }

    @Override
    public IAnswer findPossibleHandles(List<ThreeAddressCode> sources) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void validateExpressions(List<ThreeAddressCode> sources) {
        List<String> variables = new ArrayList<>();
        String variable;

        if (StringUtil.isVariable(sources.get(0).getLeft())) {
            variable = StringUtil.getVariable(sources.get(0).getLeft());
            NumberUtil.getVariableCoeficient(sources.get(0).getLeft());
            if (StringUtil.isNotEmpty(variable))
                variables.add(variable);
        }

        if (StringUtil.isVariable(sources.get(0).getRight())) {
            variable = StringUtil.getVariable(sources.get(0).getRight());
            NumberUtil.getVariableCoeficient(sources.get(0).getRight());
            if (StringUtil.isNotEmpty(variable) && !variables.contains(variable))
                variables.add(variable);
        }

        for (ExpandedQuadruple expandedQuadruple : sources.get(0).getExpandedQuadruples()) {
            if (StringUtil.isVariable(expandedQuadruple.getArgument1())) {
                variable = StringUtil.getVariable(expandedQuadruple.getArgument1());
                NumberUtil.getVariableCoeficient(expandedQuadruple.getArgument1());
                if (StringUtil.isNotEmpty(variable) && !variables.contains(variable))
                    variables.add(variable);
            }

            if (StringUtil.isVariable(expandedQuadruple.getArgument2())) {
                variable = StringUtil.getVariable(expandedQuadruple.getArgument2());
                NumberUtil.getVariableCoeficient(expandedQuadruple.getArgument2());
                if (StringUtil.isNotEmpty(variable) && !variables.contains(variable))
                    variables.add(variable);
            }
        }
    }

    private void setUpExponent(List<ThreeAddressCode> source, String result) {

        ExpandedQuadruple expandedQuadruple = source.get(0).findQuadrupleByResult(result);

        if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.setUpExponent(source, expandedQuadruple.getArgument1());
        }

        if (StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.setUpExponent(source, expandedQuadruple.getArgument2());
        }

        if (expandedQuadruple.isPotentiation()) {
            String nvvLabel = expandedQuadruple.getArgument1() + expandedQuadruple.getOperator() + expandedQuadruple.getArgument2();

            ExpandedQuadruple parent;
            parent = source.get(0).findQuadrupleByArgument1(result);

            if (parent != null) {
                parent.setArgument1(nvvLabel);
            } else {
                parent = source.get(0).findQuadrupleByArgument2(result);
                if (parent != null) {
                    parent.setArgument2(nvvLabel);
                }
            }


        }

    }

    @Override
    public void setVariables(List<NumericValueVariable> variables) {
    }


    private void getFinalResult(AnswerFatoration answer, ThreeAddressCode threeAddressCode, String possibleNumber) {
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
