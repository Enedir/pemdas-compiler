package br.ifmath.compiler.domain.expertsystem.polynomial.multiplication;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.addandsub.AnswerPolynomialAddAndSub;
import br.ifmath.compiler.domain.expertsystem.polynomial.addandsub.PolynomialAddAndSubRuleGroupSimilarTerms;
import br.ifmath.compiler.domain.expertsystem.polynomial.addandsub.PolynomialAddAndSubRuleShiftSign;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.NumberUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PolynomialMultiplicationExpertSystem implements IExpertSystem {

    private static PolynomialAddAndSubRuleShiftSign shiftSign;

    private static PolynomialAddAndSubRuleGroupSimilarTerms groupTerms;

    private static PolynomialMultiplicationRuleDistributive distributive;

    private static PolynomialMultiplicationRuleMultiplication multiplication;

    public PolynomialMultiplicationExpertSystem() {
        shiftSign = new PolynomialAddAndSubRuleShiftSign();
        groupTerms = new PolynomialAddAndSubRuleGroupSimilarTerms();
        distributive = new PolynomialMultiplicationRuleDistributive();
        multiplication = new PolynomialMultiplicationRuleMultiplication();
    }


    @Override
    public IAnswer findBestAnswer(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();

        AnswerPolynomialMultiplication answer = new AnswerPolynomialMultiplication(steps);

        steps.add(new Step(sources, sources.get(0).toLaTeXNotation(), sources.get(0).toMathNotation(), "Equação inicial."));


        setUpQuadruples(sources);

        validateExpressions(sources);


        if (distributive.match(sources)) {
            steps.addAll(distributive.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        if (multiplication.match(sources)) {
            steps.addAll(multiplication.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }
/*
        if (shiftSign.match(sources)) {
            steps.addAll(shiftSign.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }

        validateExpressions(sources);
        if (groupTerms.match(sources)) {
            steps.addAll(groupTerms.handle(sources));
            sources = steps.get(steps.size() - 1).getSource();
        }*/


        sources = substituteNullFields(sources);

        if (StringUtil.isVariable(sources.get(0).getLeft())) {
            getFinalResult(answer, sources.get(0), sources.get(0).getRight());
        } else {
            getFinalResult(answer, sources.get(0), sources.get(0).getLeft());
        }

        return answer;
    }

    /**
     * Ajusta as quadruplas que tem algum grau e estao com o formato incorreto. Necessario para os casos que ha uma
     * quadrupla de MINUS com um grau de potenciacao (Ex.: -2x^2), essas ficam como temporarias com grau de potenciação
     * (Ex.: T4^2, sendo T4 = MINUS 2x), o que não é desejável. Assim, o expoente é retirado do lado da variavel temporaria e adicionado a
     * quadrupla de MINUS, no caso do exemplo, seria T4 = MINUS 2x^2,
     *
     * @param source {@link ThreeAddressCode} que contém todas as quadruplas a serem analisadas e formatadas
     */
    private void handlePotentiation(ThreeAddressCode source) {
        for (ExpandedQuadruple expandedQuadruple : source.getExpandedQuadruples()) {
            // Verifica se não é uma quadrupla de MINUS que contém um expoente, pois não tem o argument2 para obter depois
            if (!expandedQuadruple.isNegative() && this.isTemporaryVariableWithPotentiation(expandedQuadruple)) {
                String argument = "";

                //Variável responsável por identificar qual argumento está sendo tratado
                boolean isArgument1 = true;

                if (expandedQuadruple.getArgument1().contains("^"))
                    argument = expandedQuadruple.getArgument1();
                if (expandedQuadruple.getArgument2().contains("^")) {
                    argument = expandedQuadruple.getArgument2();
                    isArgument1 = false;
                }

                /* Nesse caso não há como fazer comparação através do {@link StringUtil.match} pois uma
                 * variável temporaria não deveria ter um expoente, e então sempre daria um resultado false */
                if (argument.startsWith("T")) {
                    String potentiation = argument.substring(argument.indexOf("^"));
                    argument = argument.replace(potentiation, "");

                    //Retirado expoente da variável temporaria
                    if (isArgument1)
                        expandedQuadruple.setArgument1(argument);
                    else
                        expandedQuadruple.setArgument2(argument);

                    //Adicionado o expoente ao argument1 da quadrupla MINUS.
                    ExpandedQuadruple quadruple = source.findQuadrupleByResult(argument);
                    quadruple.setArgument1(quadruple.getArgument1() + potentiation);
                }

            }
        }
    }

    private boolean isTemporaryVariableWithPotentiation(ExpandedQuadruple expandedQuadruple) {
        return (expandedQuadruple.getArgument1().contains("T") && expandedQuadruple.getArgument1().contains("^"))
                || (expandedQuadruple.getArgument2().contains("T") && expandedQuadruple.getArgument2().contains("^"));
    }

    private void setUpQuadruples(List<ThreeAddressCode> source) {
        setUpExponent(source, source.get(0).getLeft());
        handlePotentiation(source.get(0));
        source.get(0).clearNonUsedQuadruples();
    }

    @Override
    public IAnswer findPossibleHandles(List<ThreeAddressCode> sources) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    @Override
    public void validateExpressions(List<ThreeAddressCode> sources) {
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
