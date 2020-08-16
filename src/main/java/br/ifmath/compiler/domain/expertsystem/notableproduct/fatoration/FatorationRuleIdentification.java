package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;

import java.util.ArrayList;
import java.util.List;

public class FatorationRuleIdentification implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return true;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);

        String correctExplanation = identify();

        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Identificação do tipo de fatoração a partir da equação inicial: " + correctExplanation));
        return steps;
    }

    private String identify() {
        if (isCommonFactor(this.source.getRootQuadruple(), this.source)) {
            return "Fator comum em evidência.";
        }

        if (isPerfectSquareTrinomial()) {

        }
        return "";
    }


    //<editor-fold desc="CommonFactor">

    public static boolean isCommonFactor(ExpandedQuadruple iterationQuadruple, ThreeAddressCode source) {
        String argument = (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
                ? source.findQuadrupleByResult(iterationQuadruple.getArgument1()).getArgument1() : iterationQuadruple.getArgument1();
        NumericValueVariable patternNVV = new NumericValueVariable(argument);
        return isThereAEqualPattern(iterationQuadruple, patternNVV.getLabel(), source);
    }

    private static boolean isThereAEqualPattern(ExpandedQuadruple iterationQuadruple, String pattern, ThreeAddressCode source) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return isThereAEqualPattern(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), pattern, source);
        }

        NumericValueVariable iterationArgumentNVV = new NumericValueVariable(iterationQuadruple.getArgument1());
        if (iterationArgumentNVV.getLabel().contains(pattern))
            return true;

        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return isThereAEqualPattern(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), pattern, source);
        }

        iterationArgumentNVV = new NumericValueVariable(iterationQuadruple.getArgument2());
        return iterationArgumentNVV.getLabel().contains(pattern);
    }
    //</editor-fold>>

    //<editor-fold desc="PerfectSquareTrinomial">
    //TODO testar
    private boolean isPerfectSquareTrinomial() {
        ExpandedQuadruple root = this.source.getRootQuadruple();
        if (root.isPlus()) {
            if (isValidTrinomialTerm(root.getArgument1())) {
                if (StringUtil.match(root.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    ExpandedQuadruple middleTermQuadruple = this.source.findQuadrupleByResult(root.getArgument2());
                    if (StringUtil.match(middleTermQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_EXPONENT.toString())) {
                        NumericValueVariable middleTerm = new NumericValueVariable(middleTermQuadruple.getArgument1());
                        if (isValidTrinomialTerm(middleTermQuadruple.getArgument2())) {

                            NumericValueVariable firstTerm = new NumericValueVariable(root.getArgument1());
                            NumericValueVariable secondTerm = new NumericValueVariable(middleTermQuadruple.getArgument1());
                            //Variável com variável
                            if (StringUtil.match(root.getArgument1(), RegexPattern.VARIABLE_WITH_EXPONENT.toString()) &&
                                    StringUtil.match(middleTermQuadruple.getArgument2(), RegexPattern.VARIABLE_WITH_EXPONENT.toString())) {
                                if (firstTerm.getLabelVariable().equals(secondTerm.getLabelVariable())) {
                                    if ((firstTerm.getLabelPower() + secondTerm.getLabelPower()) / 2 == middleTerm.getLabelPower()) {
                                        if (firstTerm.getValue() == 1 && secondTerm.getValue() == 1 && middleTerm.getValue() == 2)
                                            return true;
                                        else if (firstTerm.getValue() != 1 && secondTerm.getValue() != 1) {
                                            return (int) Math.sqrt(firstTerm.getValue()) + (int) Math.sqrt(secondTerm.getValue()) == middleTerm.getValue();
                                        } else {
                                            int value = (firstTerm.getValue() != 1) ? (int) Math.sqrt(firstTerm.getValue()) : (int) Math.sqrt(secondTerm.getValue());
                                            return value == middleTerm.getValue();
                                        }
                                    }

                                }
                            }

                            //Número com número
                            if (StringUtil.match(root.getArgument1(), RegexPattern.NATURAL_NUMBER.toString()) &&
                                    StringUtil.match(middleTermQuadruple.getArgument2(), RegexPattern.NATURAL_NUMBER.toString())) {
                                return Math.round(Math.sqrt(firstTerm.getValue()) + Math.sqrt(secondTerm.getValue())) == middleTerm.getValue();
                            }

                            //Variável com número
                            if (StringUtil.match(root.getArgument1(), RegexPattern.VARIABLE_WITH_EXPONENT.toString()) &&
                                    StringUtil.match(middleTermQuadruple.getArgument2(), RegexPattern.NATURAL_NUMBER.toString())) {
                                int variableValue = (firstTerm.getValue() == 1) ? 0 : (int) Math.sqrt(firstTerm.getValue());
                                if (variableValue + (int) Math.sqrt(secondTerm.getValue()) == middleTerm.getValue()) {
                                    return middleTerm.getLabelPower() == firstTerm.getLabelPower() / 2;
                                }
                            }

                            //Número com variável
                            if (StringUtil.match(root.getArgument1(), RegexPattern.NATURAL_NUMBER.toString()) &&
                                    StringUtil.match(middleTermQuadruple.getArgument2(), RegexPattern.VARIABLE_WITH_EXPONENT.toString())) {
                                int variableValue = (secondTerm.getValue() == 1) ? 0 : (int) Math.sqrt(secondTerm.getValue());
                                if (variableValue + (int) Math.sqrt(firstTerm.getValue()) == middleTerm.getValue()) {
                                    return middleTerm.getLabelPower() == secondTerm.getLabelPower() / 2;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isValidTrinomialTerm(String argument) {
        return StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString()) &&
                (Math.sqrt(Integer.parseInt(argument)) % 1 == 0) ||
                (StringUtil.match(argument, RegexPattern.VARIABLE_WITH_EXPONENT.toString()) &&
                        new NumericValueVariable(argument).getLabelPower() % 2 == 0);
    }
    //</editor-fold>>
}
