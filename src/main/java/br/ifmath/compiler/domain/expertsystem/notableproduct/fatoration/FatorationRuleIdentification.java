package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FatorationRuleIdentification implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return true;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException {
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

    private String identify() throws InvalidAlgebraicExpressionException {
        if (isPerfectSquareTrinomial(this.source)) {
            return "Trinômio quadrado perfeito.\n\nNote que a expressão é formada " +
                    "por três monômios em que o primeiro e o último termo são quadrados e o termo cental é o dobro do " +
                    "produto entre o priemiro termo e o segundo termo.";
        }

        if (isPerfectCube(this.source)) {
//            String result = "Cubo perfeito ";
            //TODO implementar o isSumCube
//            if (isSumCube())
//                return "(cubo da soma)";
//            return "(cubo da diferença)";
        }

        if (isDifferenceOfTwoSquares(this.source)) {
            return "Diferença de dois quadrados.";
        }

        if (isCommonFactor(this.source.getRootQuadruple(), this.source)) {
            return "Fator comum em evidência.";
        }


        throw new InvalidAlgebraicExpressionException("Regra não identificada");
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
    public static boolean isPerfectSquareTrinomial(ThreeAddressCode source) {
        ExpandedQuadruple root = source.getRootQuadruple();
        if (root.isPlus()) {
            if (isSquareReducibleTerm(root.getArgument1())) {
                if (StringUtil.match(root.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    ExpandedQuadruple middleTermQuadruple = source.findQuadrupleByResult(root.getArgument2());
                    if (StringUtil.matchAny(middleTermQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_EXPONENT.toString(),
                            RegexPattern.VARIABLE_WITH_COEFFICIENT.toString(), RegexPattern.NATURAL_NUMBER.toString())) {
                        NumericValueVariable middleTerm = new NumericValueVariable(middleTermQuadruple.getArgument1());
                        if (isSquareReducibleTerm(middleTermQuadruple.getArgument2())) {

                            NumericValueVariable firstTerm = new NumericValueVariable(root.getArgument1());
                            NumericValueVariable secondTerm = new NumericValueVariable(middleTermQuadruple.getArgument2());
                            //Variável com variável
                            if (StringUtil.matchAny(middleTermQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_EXPONENT.toString(),
                                    RegexPattern.VARIABLE_WITH_COEFFICIENT.toString()) &&
                                    (StringUtil.matchAny(middleTermQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_EXPONENT.toString(),
                                            RegexPattern.VARIABLE_WITH_COEFFICIENT.toString()))) {
                                if (firstTerm.getLabelVariable().equals(secondTerm.getLabelVariable())) {
                                    if ((firstTerm.getLabelPower() + secondTerm.getLabelPower()) / 2 == middleTerm.getLabelPower()) {
                                        if (firstTerm.getValue() == 1 && secondTerm.getValue() == 1 && middleTerm.getValue() == 2)
                                            return true;
                                        else if (firstTerm.getValue() != 1 && secondTerm.getValue() != 1) {
                                            return (((int) Math.sqrt(firstTerm.getValue()) * (int) Math.sqrt(secondTerm.getValue())) * 2) == middleTerm.getValue();
                                        } else {
                                            int value = (firstTerm.getValue() != 1) ? (int) Math.sqrt(firstTerm.getValue()) : (int) Math.sqrt(secondTerm.getValue());
                                            return (value * 2) == middleTerm.getValue();
                                        }
                                    }
                                }
                            }

                            //Número com número
                            if (StringUtil.match(root.getArgument1(), RegexPattern.NATURAL_NUMBER.toString()) &&
                                    StringUtil.match(middleTermQuadruple.getArgument2(), RegexPattern.NATURAL_NUMBER.toString())) {
                                return ((Math.round(Math.sqrt(firstTerm.getValue()) * Math.sqrt(secondTerm.getValue()))) * 2) == middleTerm.getValue();
                            }

                            //Variável com número
                            if (StringUtil.matchAny(middleTermQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_EXPONENT.toString(),
                                    RegexPattern.VARIABLE_WITH_COEFFICIENT.toString()) &&
                                    StringUtil.match(middleTermQuadruple.getArgument2(), RegexPattern.NATURAL_NUMBER.toString())) {
                                int variableValue = (firstTerm.getValue() == 1) ? 1 : (int) Math.sqrt(firstTerm.getValue());
                                if (((variableValue * (int) Math.sqrt(secondTerm.getValue())) * 2) == middleTerm.getValue()) {
                                    return middleTerm.getLabelPower() == firstTerm.getLabelPower() / 2;
                                }
                            }

                            //Número com variável
                            if (StringUtil.match(root.getArgument1(), RegexPattern.NATURAL_NUMBER.toString()) &&
                                    StringUtil.matchAny(middleTermQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_EXPONENT.toString(),
                                            RegexPattern.VARIABLE_WITH_COEFFICIENT.toString())) {
                                int variableValue = (secondTerm.getValue() == 1) ? 1 : (int) Math.sqrt(secondTerm.getValue());
                                if (((variableValue * (int) Math.sqrt(firstTerm.getValue())) * 2) == middleTerm.getValue()) {
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

    private static boolean isSquareReducibleTerm(String argument) {
        return StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString()) &&
                (Math.sqrt(Integer.parseInt(argument)) % 1 == 0) ||
                (StringUtil.match(argument, RegexPattern.VARIABLE_WITH_EXPONENT.toString()) &&
                        (new NumericValueVariable(argument).getLabelPower() % 2 == 0 &&
                                (Math.sqrt(new NumericValueVariable(argument).getValue()) % 1 == 0)));
    }
    //</editor-fold>>

    //<editor-fold desc="DifferenceOfTwoSquares">
    public static boolean isDifferenceOfTwoSquares(ThreeAddressCode source) {
        if (source.getExpandedQuadruples().size() == 1) {
            ExpandedQuadruple root = source.getRootQuadruple();
            if (root.isMinus()) {
                return isSquareReducibleTerm(root.getArgument1()) && isSquareReducibleTerm(root.getArgument2());
            }
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="PerfectCube">

    public static boolean isPerfectCube(ThreeAddressCode source) {
        ExpandedQuadruple root = source.getRootQuadruple();
        ExpandedQuadruple last = source.getLastQuadruple();

        //TODO fazer ifs apropriados

        //Variável com variável
        if (isCubeReducibleVariableTerm(root.getArgument1()) && isCubeReducibleVariableTerm(last.getArgument2())) {

        }

        //Número com número
        if (isCubeReducibleNumericTerm(root.getArgument1()) && isCubeReducibleNumericTerm(last.getArgument2())) {

        }

        //Variável com número
        if (isCubeReducibleVariableTerm(root.getArgument1()) && isCubeReducibleNumericTerm(last.getArgument2())) {

        }

        //Número com variável
        if (isCubeReducibleNumericTerm(root.getArgument1()) && isCubeReducibleVariableTerm(last.getArgument2())) {

        }
        return false;
    }

    private static boolean isCubeReducibleNumericTerm(String argument) {
        return StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString()) &&
                (Math.cbrt(Integer.parseInt(argument)) % 1 == 0);

    }

    private static boolean isCubeReducibleVariableTerm(String argument) {
        return StringUtil.match(argument, RegexPattern.VARIABLE_WITH_EXPONENT.toString()) &&
                (new NumericValueVariable(argument).getLabelPower() % 3 == 0 &&
                        (Math.cbrt(new NumericValueVariable(argument).getValue()) % 1 == 0));

    }

    //</editor-fold>

}
