package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.commonfactor;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.FatorationRuleIdentification;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FatorationRuleCommonFactor implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return FatorationRuleIdentification.isCommonFactor(source.get(0).getRootQuadruple(), source.get(0));
    }


    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
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
    private String getCommonFactor() {
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
        return "";
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

        lowestValue = this.getLowerTerm(iterationQuadruple.getArgument1(), lowestValue);
        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getLowestValue(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), lowestValue);
        }

        lowestValue = this.getLowerTerm(iterationQuadruple.getArgument2(), lowestValue);
        return lowestValue;
    }

    private String getLowerTerm(String argument, String lowestValue) {
        NumericValueVariable argumentNVV = new NumericValueVariable(argument);
        NumericValueVariable lowestNVV = new NumericValueVariable(lowestValue);

        if(argumentNVV.getValue() < lowestNVV.getValue())
            lowestNVV.setValue(argumentNVV.getValue());

        if(argumentNVV.getLabelPower() < lowestNVV.getLabelPower())
            if(argumentNVV.getLabelPower() == 0)
                lowestNVV.setLabel("");
            else
                lowestNVV.setLabelPower(argumentNVV.getLabelPower());

        return lowestNVV.toString();
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
        if (pattern.getLabel().isEmpty() && pattern.getValue() == null) {
            return true;
        } else if (pattern.getLabel().isEmpty()) {
            return nvv.getValue() % pattern.getValue() != 0;

        } else if (pattern.getValue() == null) {
            if (!StringUtil.match(nvv.toString(), RegexPattern.VARIABLE_WITH_EXPONENT.toString()))
                return !nvv.getLabel().contains(pattern.getLabel());
            return itDoesntMatchMonomy(pattern.getLabelPower(), nvv.getLabelPower(), nvv.getLabelVariable(), pattern.getLabelVariable(), false);
        }
        if (pattern.getValue() == 1)
            return !nvv.getLabel().contains(pattern.getLabelVariable());
        return itDoesntMatchMonomy(pattern.getValue(), nvv.getValue(), nvv.getLabelVariable(), pattern.getLabelVariable(), true);
    }

    private boolean itDoesntMatchMonomy(int numeralPart1, int numeralPart2, String literalPart1, String literalPart2,
                                        boolean isValue) {
        return !((this.isMultiple(numeralPart1, numeralPart2, isValue))
                && literalPart1.contains(literalPart2));
    }

    private boolean isMultiple(int n1, int n2, boolean isLabel) {
        return (isLabel) ? (n1 % n2 == 0 || n2 % n1 == 0) && !(n1 == 1 || n2 == 1) :
                (n1 % n2 == 0 || n2 % n1 == 0) || ((n1 == 1 && n2 != 1) || (n2 == 1) && n1 != 1);
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
        String newArgument;
        if (StringUtil.match(commonFactor, RegexPattern.VARIABLE.toString()) && argument.contains(commonFactor)) {
            if (argument.equals(commonFactor))
                newArgument = "1";
            else if (StringUtil.match(argument, RegexPattern.VARIABLE_WITH_COEFFICIENT.toString()))
                newArgument = argument.replace(commonFactor, "");
            else {
                NumericValueVariable nvv = new NumericValueVariable(argument);
                nvv.setLabelPower(nvv.getLabelPower() - 1);
                newArgument = nvv.toString();
            }
        } else if (StringUtil.match(commonFactor, RegexPattern.NATURAL_NUMBER.toString())) {
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
