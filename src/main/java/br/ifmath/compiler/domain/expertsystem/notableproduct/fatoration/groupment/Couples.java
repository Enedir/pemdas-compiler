package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.groupment;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.commonfactor.FatorationRuleCommonFactor;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Couples {
    private String firstCoupleFactor;
    private String secondCoupleFactor;
    private final String secondCoupleOperator;
    private final List<ExpandedQuadruple> firstCoupleMultiplier, secondCoupleMultiplier;

    public Couples(ThreeAddressCode firstCouple, ThreeAddressCode secondCouple, String secondCoupleOperator) throws InvalidAlgebraicExpressionException {
        firstCoupleMultiplier = new ArrayList<>();
        secondCoupleMultiplier = new ArrayList<>();
        this.secondCoupleOperator = secondCoupleOperator;
        setCouples(firstCouple, true);
        setCouples(secondCouple, false);
    }

    private void setCouples(ThreeAddressCode couple, boolean isFirstCouple) throws InvalidAlgebraicExpressionException {
        FatorationRuleCommonFactor commonFactor = new FatorationRuleCommonFactor();
        ThreeAddressCode source = getResultSource(commonFactor, couple);
        if (isFirstCouple)
            this.firstCoupleFactor = source.getRootQuadruple().getArgument1();
        else
            this.secondCoupleFactor = source.getRootQuadruple().getArgument1();
        ExpandedQuadruple firstSourceRoot = couple.getRootQuadruple();
        if (StringUtil.match(firstSourceRoot.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            for (ExpandedQuadruple expandedQuadruple : couple.getExpandedQuadruples()) {
                if (!expandedQuadruple.equals(couple.getRootQuadruple())) {
                    if (isFirstCouple)
                        this.firstCoupleMultiplier.add(expandedQuadruple);
                    else
                        this.secondCoupleMultiplier.add(expandedQuadruple);
                }
            }
        }
    }

    private ThreeAddressCode getResultSource(IRule rule, ThreeAddressCode ruleSource) throws InvalidAlgebraicExpressionException {
        return rule.handle(Collections.singletonList(ruleSource)).get(0).getSource().get(0);
    }

    public String getFirstCoupleFactor() {
        return firstCoupleFactor;
    }

    public String getSecondCoupleFactor() {
        return secondCoupleFactor;
    }

    public List<ExpandedQuadruple> getFirstCoupleMultiplier() {
        return firstCoupleMultiplier;
    }

    public List<ExpandedQuadruple> getSecondCoupleMultiplier() {
        return secondCoupleMultiplier;
    }

    public String getSecondCoupleOperator() {
        return secondCoupleOperator;
    }

    public boolean areEmpty() {
        return firstCoupleFactor == null || firstCoupleMultiplier == null
                || secondCoupleFactor == null || secondCoupleMultiplier == null;
    }

    public boolean isFirstCoupleEqualsSecond() {
        if (firstCoupleMultiplier.size() != secondCoupleMultiplier.size())
            return false;
        for (int i = 0; i < firstCoupleMultiplier.size(); i++) {

            ExpandedQuadruple firstMultiplierQuadruple = firstCoupleMultiplier.get(i);
            ExpandedQuadruple secondMultiplierQuadruple = secondCoupleMultiplier.get(i);

            String argument = firstMultiplierQuadruple.getArgument1();
            if (!StringUtil.match(argument, RegexPattern.TEMPORARY_VARIABLE.toString())) {
                if (!argument.equals(secondMultiplierQuadruple.getArgument1())) {
                    return false;
                }
            }

            if (!firstMultiplierQuadruple.getOperator().equals(secondMultiplierQuadruple.getOperator()))
                return false;
            argument = firstMultiplierQuadruple.getArgument2();
            if (!StringUtil.match(argument, RegexPattern.TEMPORARY_VARIABLE.toString())) {
                if (!argument.equals(secondMultiplierQuadruple.getArgument2())) {
                    return false;
                }
            }
        }
        return true;
    }
}
