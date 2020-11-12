package br.ifmath.compiler.domain.expertsystem.polynomial.multiplication;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.PolynomialRuleSortSimilarTerms;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.PolynomialRuleGroupSimilarTerms;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.Monomial;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PolynomialMultiplicationExpertSystem implements IExpertSystem {


    private static PolynomialRuleSortSimilarTerms sortTerms;

    private static PolynomialMultiplicationRuleDistributive distributive;

    private static PolynomialMultiplicationRuleMultiplication multiplication;

    private static PolynomialRuleGroupSimilarTerms groupTerms;

    public PolynomialMultiplicationExpertSystem() {
        sortTerms = new PolynomialRuleSortSimilarTerms();
        groupTerms = new PolynomialRuleGroupSimilarTerms();
        distributive = new PolynomialMultiplicationRuleDistributive();
        multiplication = new PolynomialMultiplicationRuleMultiplication();
    }


    @Override
    public IAnswer findBestAnswer(List<ThreeAddressCode> sources) {
        List<Step> steps = new ArrayList<>();

        AnswerPolynomialMultiplication answer = new AnswerPolynomialMultiplication(steps);

        sources.get(0).setUp();

        steps.add(new Step(sources, sources.get(0).toLaTeXNotation().trim(), sources.get(0).toLaTeXNotation().trim(), "Equação inicial."));

        setUpQuadruples(sources);

        validateExpressions(sources);
        if (distributive.match(sources)) {
            steps.addAll(distributive.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        validateExpressions(sources);
        if (multiplication.match(sources)) {
            steps.addAll(multiplication.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        validateExpressions(sources);
        if (sortTerms.match(sources)) {
            steps.addAll(sortTerms.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        //Está sendo usado a regra de agrupar termos do addandsub, pois é equivalente
        validateExpressions(sources);
        if (groupTerms.match(sources)) {
            steps.addAll(groupTerms.handle(sources));
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

        StringUtil.validateVariable(sources.get(0).getLeft(), variables);

        StringUtil.validateVariable(sources.get(0).getRight(), variables);

        for (ExpandedQuadruple expandedQuadruple : sources.get(0).getExpandedQuadruples()) {
            StringUtil.validateVariable(expandedQuadruple.getArgument1(), variables);

            StringUtil.validateVariable(expandedQuadruple.getArgument2(), variables);
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
            String monomialLiteral = expandedQuadruple.getArgument1() + expandedQuadruple.getOperator() + expandedQuadruple.getArgument2();

            ExpandedQuadruple parent;
            parent = source.get(0).findQuadrupleByArgument1(result);

            if (parent != null) {
                parent.setArgument1(monomialLiteral);
            } else {
                parent = source.get(0).findQuadrupleByArgument2(result);
                if (parent != null) {
                    parent.setArgument2(monomialLiteral);
                }
            }


        }

    }

    @Override
    public void setVariables(List<Monomial> variables) {

    }


    private void getFinalResult(AnswerPolynomialMultiplication answer, ThreeAddressCode threeAddressCode, String possibleNumber) {
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
