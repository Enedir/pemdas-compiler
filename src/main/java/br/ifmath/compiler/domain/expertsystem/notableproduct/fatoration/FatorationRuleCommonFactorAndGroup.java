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

public class FatorationRuleCommonFactorAndGroup implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return FatorationRuleIdentification.isCommonFactor(source.get(0).getRootQuadruple(), source.get(0));
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);
        String commonFactor = this.getCommonFactor();
        if (!commonFactor.isEmpty())
            this.groupCommonFactor(commonFactor);

        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Verificamos o elemento que temos em comum e colocamos em evidência."));
        return steps;
    }

    //<editor-fold desc="getCommonFactor">
    private String getCommonFactor() throws InvalidAlgebraicExpressionException {
        NumericValueVariable patternNVV = new NumericValueVariable(this.getSmallestUnit());

        if (this.isEqualPattern(this.source.getRootQuadruple(), patternNVV))
            return patternNVV.toString();

        Integer patternNVVValue = patternNVV.getValue();
        patternNVV.setValue(null);
        if (this.isEqualPattern(this.source.getRootQuadruple(), patternNVV))
            return patternNVV.getLabel();

        patternNVV.setLabel("");
        patternNVV.setValue(patternNVVValue);
        if (this.isEqualPattern(this.source.getRootQuadruple(), patternNVV))
            return patternNVV.getValue().toString();
        throw new InvalidAlgebraicExpressionException("Padrão não encontrado");
    }

    private String getSmallestUnit() {
        String firstArgument = this.source.getRootQuadruple().getArgument1();
        if (StringUtil.match(firstArgument, RegexPattern.TEMPORARY_VARIABLE.toString()))
            firstArgument = this.source.findQuadrupleByResult(firstArgument).getArgument1();
        return getLowestValue(this.source.getRootQuadruple(), firstArgument);
    }

    private String getLowestValue(ExpandedQuadruple iterationQuadruple, String lowestValue) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getLowestValue(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), lowestValue);
        }

        lowestValue = this.getLowerTerm(iterationQuadruple, lowestValue, true);
        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getLowestValue(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), lowestValue);
        }

        lowestValue = this.getLowerTerm(iterationQuadruple, lowestValue, false);
        return lowestValue;
    }

    private String getLowerTerm(ExpandedQuadruple quadruple, String lowestValue, boolean isArgument1) {
        String argument = (isArgument1) ? quadruple.getArgument1() : quadruple.getArgument2();

        if (StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString())) {
            if (StringUtil.match(lowestValue, RegexPattern.NATURAL_NUMBER.toString())) {
                if (Integer.parseInt(argument) < Integer.parseInt(lowestValue))
                    return argument;
            }
            return lowestValue;
        }

        if (StringUtil.match(argument, RegexPattern.VARIABLE.toString())) {
            return argument;
        }

        if (StringUtil.match(lowestValue, RegexPattern.VARIABLE_WITH_COEFFICIENT.toString())) {
            if (StringUtil.matchAny(argument, RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.VARIABLE.toString()))
                return argument;
            else if (StringUtil.match(argument, RegexPattern.VARIABLE_WITH_COEFFICIENT.toString())) {
                int argumentNVVValue = new NumericValueVariable(argument).getValue();
                int lowestValueNVVValue = new NumericValueVariable(lowestValue).getValue();
                if (argumentNVVValue < lowestValueNVVValue)
                    return argument;
            }
            return lowestValue;
        }

        if (StringUtil.matchAny(argument,
                RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.VARIABLE.toString(), RegexPattern.VARIABLE_WITH_COEFFICIENT.toString())) {
            return argument;
        }
        NumericValueVariable lowestValueNVV = new NumericValueVariable(lowestValue);
        NumericValueVariable argumentNVV = new NumericValueVariable(argument);
        if (argumentNVV.getLabelPower() < lowestValueNVV.getLabelPower())
            return argument;

        if (lowestValueNVV.getLabelPower() < argumentNVV.getLabelPower())
            return lowestValue;

        return (argumentNVV.getValue() < lowestValueNVV.getValue()) ? argument : lowestValue;
    }

    //</editor-fold>>

    //<editor-fold desc="isEqualPattern">
    private boolean isEqualPattern(ExpandedQuadruple iterationQuadruple, NumericValueVariable pattern) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.isEqualPattern(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), pattern);
        }

        if (this.isDifferentPattern(iterationQuadruple.getArgument1(), pattern))
            return false;

        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.isEqualPattern(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), pattern);
        }

        return !this.isDifferentPattern(iterationQuadruple.getArgument2(), pattern);

    }

    private boolean isDifferentPattern(String argument, NumericValueVariable pattern) {
        if (StringUtil.match(argument, RegexPattern.TEMPORARY_VARIABLE.toString()))
            argument = this.source.findQuadrupleByResult(argument).getArgument1();
        NumericValueVariable nvv = new NumericValueVariable(argument);
        if (pattern.getLabel().isEmpty()) {
            return nvv.getValue() % pattern.getValue() != 0;

        } else if (pattern.getValue() == null) {
            if (!StringUtil.match(nvv.toString(), RegexPattern.VARIABLE_WITH_EXPONENT.toString()))
                return !nvv.getLabel().contains(pattern.getLabel());
            return itDoesntMatchMonomy(pattern.getLabelPower(), nvv.getLabelPower(), nvv.getLabelVariable(), pattern.getLabelVariable());
        }
        return itDoesntMatchMonomy(pattern.getValue(), nvv.getValue(), nvv.getLabelVariable(), pattern.getLabelVariable());
    }

    private boolean itDoesntMatchMonomy(int numeralPart1, int numeralPart2, String literalPart1, String literalPart2) {
        return !((numeralPart1 % numeralPart2 == 0 || numeralPart2 % numeralPart1 == 0)
                && literalPart1.contains(literalPart2));
    }
    //</editor-fold>>

    //<editor-fold desc="groupCommonFactor">
    private void groupCommonFactor(String commonFactor) {
        this.removeCommonFactor(this.source.getRootQuadruple(), commonFactor);
        this.surroundQuadruplesWithParentheses(this.source.getLeft());
        ExpandedQuadruple newRoot = new ExpandedQuadruple("*", commonFactor, this.source.getLeft(), this.source.retrieveNextTemporary(), 0, 0);
        this.source.getExpandedQuadruples().add(newRoot);
        this.source.setLeft(newRoot.getResult());
    }

    private void removeCommonFactor(ExpandedQuadruple iterationQuadruple, String commonFactor) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.removeCommonFactor(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), commonFactor);
            return;
        }

        this.adjustTermsByFactor(iterationQuadruple, commonFactor, true);

        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());


        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.removeCommonFactor(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), commonFactor);
            return;
        }

        this.adjustTermsByFactor(iterationQuadruple, commonFactor, false);
    }

    private void adjustTermsByFactor(ExpandedQuadruple argumentQuadruple, String commonFactor, boolean isArgument1) {
        String argument = (isArgument1) ? argumentQuadruple.getArgument1() : argumentQuadruple.getArgument2();
        if (StringUtil.match(argument, RegexPattern.TEMPORARY_VARIABLE.toString()))
            argument = this.source.findQuadrupleByResult(argument).getArgument1();
        String newArgument = "";
        if (StringUtil.match(commonFactor, RegexPattern.VARIABLE.toString()) && argument.contains(commonFactor))
            newArgument = (argument.equals(commonFactor)) ? "1" : argument.replace(commonFactor, "");

        else if (StringUtil.match(commonFactor, RegexPattern.NATURAL_NUMBER.toString())) {
            int commonFactorValue = Integer.parseInt(commonFactor);
            NumericValueVariable nvv = new NumericValueVariable(argument);
            int iterationValue = nvv.getValue();
            if (iterationValue % commonFactorValue == 0)
                nvv.setValue(iterationValue / commonFactorValue);
            newArgument = nvv.toString();

        } else {
            NumericValueVariable argumentNVV = new NumericValueVariable(argument);
            NumericValueVariable factorNVV = new NumericValueVariable(commonFactor);

            if (argumentNVV.getLabel().equals(factorNVV.getLabel()))
                argumentNVV.setLabel("");
            else
                argumentNVV.setLabelPower(argumentNVV.getLabelPower() - factorNVV.getLabelPower());

            if (StringUtil.match(commonFactor, RegexPattern.VARIABLE_WITH_COEFFICIENT.toString()))
                argumentNVV.setValue(argumentNVV.getValue() / factorNVV.getValue());

            newArgument = argumentNVV.toString();
        }

        if (isArgument1)
            argumentQuadruple.setArgument1(newArgument);
        else
            argumentQuadruple.setArgument2(newArgument);
    }

    private void surroundQuadruplesWithParentheses(String companionResult) {
        ExpandedQuadruple iterationQuadruple = this.source.findQuadrupleByResult(companionResult);
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.surroundQuadruplesWithParentheses(source.findQuadrupleByResult(iterationQuadruple.getArgument1()).getResult());
        }

        iterationQuadruple.setLevel(1);

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.surroundQuadruplesWithParentheses(source.findQuadrupleByResult(iterationQuadruple.getArgument2()).getResult());
        }
    }
    //</editor-fold>>
}
