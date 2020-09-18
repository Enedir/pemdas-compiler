package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.groupment;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.commonfactor.FatorationRuleCommonFactor;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.Collections;

public class Couples {
    private String firstCoupleFactor, secondCoupleFactor;
    private ExpandedQuadruple firstCoupleMultiplier, secondCoupleMultiplier;

    public Couples(ThreeAddressCode firstCouple, ThreeAddressCode secondCouple) throws InvalidAlgebraicExpressionException {
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
            if (isFirstCouple)
                this.firstCoupleMultiplier = source.findQuadrupleByResult(firstSourceRoot.getArgument2());
            else
                this.secondCoupleMultiplier = source.findQuadrupleByResult(firstSourceRoot.getArgument2());
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

    public ExpandedQuadruple getFirstCoupleMultiplier() {
        return firstCoupleMultiplier;
    }

    public ExpandedQuadruple getSecondCoupleMultiplier() {
        return secondCoupleMultiplier;
    }

    public boolean isFirstCoupleEqualsSecond() {
        boolean isEquals = firstCoupleMultiplier.getArgument1().equals(secondCoupleMultiplier.getArgument1());
        if (isEquals) {
            isEquals = firstCoupleMultiplier.getOperator().equals(secondCoupleMultiplier.getOperator());
            if (isEquals) {
                return firstCoupleMultiplier.getArgument2().equals(secondCoupleMultiplier.getArgument2());
            }
        }
        return false;
    }

    public boolean areEmpty() {
        return firstCoupleFactor == null || firstCoupleMultiplier == null
                || secondCoupleFactor == null || secondCoupleMultiplier == null;
    }
}
