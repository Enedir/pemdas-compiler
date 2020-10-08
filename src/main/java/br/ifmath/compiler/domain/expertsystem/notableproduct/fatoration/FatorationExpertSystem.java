package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration;


import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.commonfactor.FatorationRuleCommonFactor;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.differenceoftwosquares.FatorationRuleDOTSExpandedFormula;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.differenceoftwosquares.FatorationRuleDOTSSumDifferenceProduct;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.perfectpolynomial.FatorationRulePerfectPolynomialExpandedFormulaConversion;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.perfectpolynomial.FatorationRulePerfectPolynomialNotableProduct;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.twobinomialproduct.FatorationRuleTwoBinomialProduct;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.twobinomialproduct.FatorationRuleTwoBinomialProductConvertToDivisionFormula;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.NumberUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FatorationExpertSystem implements IExpertSystem {

    private final FatorationRuleIdentification identification;

    private final FatorationRuleCommonFactor commonFactor;

    private final FatorationRulePerfectPolynomialExpandedFormulaConversion polynomialFormula;

    private final FatorationRulePerfectPolynomialNotableProduct polynomialNotableProduct;

    private final FatorationRuleDOTSExpandedFormula diffSquaresFormula;

    private final FatorationRuleDOTSSumDifferenceProduct diffSquaresProduct;

    private final FatorationRuleTwoBinomialProductConvertToDivisionFormula binomialDivisionFormula;

    private final FatorationRuleTwoBinomialProduct binomialProduct;

//    private final FatorationRuleGroupmentCommonFactor groupFactor;
//
//    private final FatorationRuleGroupmentSumProduct groupSumProduct;

    public FatorationExpertSystem() {
        this.identification = new FatorationRuleIdentification();
        this.commonFactor = new FatorationRuleCommonFactor();
        this.polynomialFormula = new FatorationRulePerfectPolynomialExpandedFormulaConversion();
        this.polynomialNotableProduct = new FatorationRulePerfectPolynomialNotableProduct();
        this.diffSquaresFormula = new FatorationRuleDOTSExpandedFormula();
        this.diffSquaresProduct = new FatorationRuleDOTSSumDifferenceProduct();
        this.binomialDivisionFormula = new FatorationRuleTwoBinomialProductConvertToDivisionFormula();
        this.binomialProduct = new FatorationRuleTwoBinomialProduct();
//        this.groupFactor = new FatorationRuleGroupmentCommonFactor();
//        this.groupSumProduct = new FatorationRuleGroupmentSumProduct();
    }

    @Override
    public IAnswer findBestAnswer(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();

        AnswerFatoration answer = new AnswerFatoration(steps);

        sources.get(0).setUp();

        setUpQuadruples(sources);

        /*As fórmulas das explicações foram escritas usando entidades HTML, para ficar com uma melhor visualização.
         * Por exemplo, para escrever "a", usa-se o código "&ascr;".
         *
         * Abaixo está um site com as entidades e seus códigos.
         *
         * https://dev.w3.org/html5/html-author/charref
         *
         */

        validateExpressions(sources);
        if (identification.match(sources)) {
            steps.addAll(identification.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        validateExpressions(sources);
        if (polynomialFormula.match(sources)) {
            steps.addAll(polynomialFormula.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();

            validateExpressions(sources);
            if (polynomialNotableProduct.match(sources)) {
                steps.addAll(polynomialNotableProduct.handle(sources));
                sources = steps.get(steps.size() - 1).getSource();
            }

        } else if (diffSquaresFormula.match(sources)) {
            steps.addAll(diffSquaresFormula.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();

            if (diffSquaresProduct.match(sources)) {
                steps.addAll(diffSquaresProduct.handle(sources));
                sources = steps.get(steps.size() - 1).getSource();
            }
        } else if (binomialDivisionFormula.match(sources)) {

            steps.addAll(binomialDivisionFormula.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
            if (binomialProduct.match(sources)) {
                steps.addAll(binomialProduct.handle(sources));
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
