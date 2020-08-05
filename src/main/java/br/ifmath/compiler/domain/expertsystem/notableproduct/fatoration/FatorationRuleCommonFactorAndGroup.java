package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
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

    private String getCommonFactor() {
        NumericValueVariable initialValue = new NumericValueVariable(this.source.getRootQuadruple().getArgument1());
        NumericValueVariable patternNVV = new NumericValueVariable(this.getLowestValue(this.source.getRootQuadruple(), initialValue));

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

    private String getLowestValue(ExpandedQuadruple iterationQuadruple, NumericValueVariable lowestValue) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getLowestValue(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), lowestValue);
        }

        lowestValue = this.getLowerTerm(iterationQuadruple, lowestValue, true);

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getLowestValue(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), lowestValue);
        }

        lowestValue = this.getLowerTerm(iterationQuadruple, lowestValue, false);
        return lowestValue.toString();
    }

    private NumericValueVariable getLowerTerm(ExpandedQuadruple quadruple, NumericValueVariable lowestValue, boolean isArgument1) {

        String argument = (isArgument1) ? quadruple.getArgument1() : quadruple.getArgument2();
        if (StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString())) {
            if (StringUtil.match(lowestValue.toString(), RegexPattern.NATURAL_NUMBER.toString())) {
                int numberArgument = Integer.parseInt(argument);
                if (numberArgument % lowestValue.getValue() == 0 || lowestValue.getValue() % numberArgument == 0) {
                    if (numberArgument < lowestValue.getValue())
                        return new NumericValueVariable(argument);
                    return lowestValue;
                }
                return new NumericValueVariable();
            } else if (StringUtil.match(lowestValue.toString(), RegexPattern.VARIABLE.toString())) {
                return lowestValue;
            } else {
                return new NumericValueVariable(argument);
            }
        }
        if (StringUtil.match(argument, RegexPattern.VARIABLE.toString())) {
            if (StringUtil.match(lowestValue.toString(), RegexPattern.NATURAL_NUMBER.toString())) {
                return new NumericValueVariable(argument);
            } else if (StringUtil.match(lowestValue.toString(), RegexPattern.VARIABLE.toString())) {
                return (lowestValue.toString().equals(argument)) ? lowestValue : new NumericValueVariable();
            } else {
                return new NumericValueVariable(argument);
            }
        }

        if (StringUtil.match(argument, RegexPattern.VARIABLE_WITH_COEFFICIENT.toString())) {
            if (StringUtil.match(lowestValue.toString(), RegexPattern.NATURAL_NUMBER.toString()) ||
                    StringUtil.match(lowestValue.toString(), RegexPattern.VARIABLE.toString())) {
                return lowestValue;
            } else if (StringUtil.match(lowestValue.toString(), RegexPattern.VARIABLE_WITH_COEFFICIENT.toString())) {
                NumericValueVariable argumentNVV = new NumericValueVariable(argument);
                if (argumentNVV.getValue() <= lowestValue.getValue()) {
                    //TODO verificar porque está retornando o valor ao inves de vazio
                    String argumentValue = this.getLowestValue(
                            this.source.getRootQuadruple(), new NumericValueVariable(String.valueOf(argumentNVV.getValue())));
                    if (!argumentValue.isEmpty())
                        return new NumericValueVariable(argumentValue);

                    String argumentLabel = this.getLowestValue(
                            this.source.getRootQuadruple(), new NumericValueVariable(argumentNVV.getLabel()));
                    if (!argumentLabel.isEmpty())
                        return new NumericValueVariable(argumentLabel);
                    return argumentNVV;
                }
                return lowestValue;
            } else {
                return new NumericValueVariable(argument);
            }
        }

        NumericValueVariable argumentNVV = new NumericValueVariable(argument);
        if (!StringUtil.match(lowestValue.toString(), RegexPattern.VARIABLE_WITH_EXPONENT.toString()))
            return lowestValue;

        if (argumentNVV.getLabelPower() < lowestValue.getLabelPower())
            return argumentNVV;
        return (argumentNVV.getValue() < lowestValue.getValue()) ? argumentNVV : lowestValue;
    }

    private boolean isEqualPattern(ExpandedQuadruple iterationQuadruple, NumericValueVariable pattern) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.isEqualPattern(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), pattern);
        }

        if (this.isDifferentPattern(iterationQuadruple.getArgument1(), pattern))
            return false;

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.isEqualPattern(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), pattern);
        }

        return !this.isDifferentPattern(iterationQuadruple.getArgument2(), pattern);

    }

    private boolean isDifferentPattern(String argument, NumericValueVariable pattern) {
        NumericValueVariable nvv = new NumericValueVariable(argument);
        if (pattern.getLabel().isEmpty()) {
            return nvv.getValue() % pattern.getValue() != 0;

        } else if (pattern.getValue() == null) {
            return !nvv.getLabel().contains(pattern.getLabel());
        }
        return !(pattern.getValue() % nvv.getValue() == 0 && nvv.getLabel().contains(pattern.getLabel()));
    }

    private void groupCommonFactor(String commonFactor) {
        this.removeCommonFactor(this.source.getRootQuadruple(), commonFactor);
        this.surroundQuadruplesWithParentheses(this.source.getLeft());
        ExpandedQuadruple newRoot = new ExpandedQuadruple("*", commonFactor, this.source.getLeft(), this.source.retrieveNextTemporary(), 0, 0);
        this.source.getExpandedQuadruples().add(newRoot);
        this.source.setLeft(newRoot.getResult());
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

    private void removeCommonFactor(ExpandedQuadruple iterationQuadruple, String commonFactor) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.removeCommonFactor(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), commonFactor);
            return;
        }

        this.adjustTermsByFactor(iterationQuadruple, commonFactor, true);


        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.removeCommonFactor(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), commonFactor);
            return;
        }

        this.adjustTermsByFactor(iterationQuadruple, commonFactor, false);
    }

    private void adjustTermsByFactor(ExpandedQuadruple argumentQuadruple, String commonFactor, boolean isArgument1) {
        String argument = (isArgument1) ? argumentQuadruple.getArgument1() : argumentQuadruple.getArgument2();
        if (StringUtil.match(argument, RegexPattern.VARIABLE.toString()) && argument.contains(commonFactor)) {
            String newArgument = (argument.equals(commonFactor)) ? "1" : argument.replace(commonFactor, "");
            if (isArgument1)
                argumentQuadruple.setArgument1(newArgument);
            else
                argumentQuadruple.setArgument2(newArgument);

        } else if (StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString())) {
            int commonFactorValue = Integer.parseInt(commonFactor);
            int iterationValue = Integer.parseInt(argument);
            if (iterationValue % commonFactorValue == 0) {
                String newArgument = String.valueOf(iterationValue / commonFactorValue);
                if (isArgument1)
                    argumentQuadruple.setArgument1(newArgument);
                else
                    argumentQuadruple.setArgument2(newArgument);
            }
        }
    }
}
