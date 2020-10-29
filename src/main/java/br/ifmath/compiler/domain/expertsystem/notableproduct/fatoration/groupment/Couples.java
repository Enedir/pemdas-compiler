package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.groupment;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.commonfactor.FatorationRuleCommonFactor;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.MathOperatorUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Couples {
    private String firstCoupleFactor, secondCoupleFactor, firstMultiplierNotation, secondMultiplierNotation;
    private final String secondCoupleOperator;
    private final List<ExpandedQuadruple> firstCoupleMultiplier, secondCoupleMultiplier;

    public Couples(ThreeAddressCode firstCouple, ThreeAddressCode secondCouple, String secondCoupleOperator) throws InvalidAlgebraicExpressionException {
        firstCoupleMultiplier = new ArrayList<>();
        secondCoupleMultiplier = new ArrayList<>();
        this.secondCoupleOperator = secondCoupleOperator;
        setCouples(firstCouple, true);
        if (secondCoupleOperator.equals("-"))
            setMinusToFirstQuadruple(secondCouple);
        setCouples(secondCouple, false);
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

    private void setCouples(ThreeAddressCode couple, boolean isFirstCouple) throws InvalidAlgebraicExpressionException {
        boolean isMinus = this.isFirstQuadrupleMinus(couple.getRootQuadruple(), couple);

        FatorationRuleCommonFactor commonFactor = new FatorationRuleCommonFactor();
        ThreeAddressCode source = getResultSource(commonFactor, couple);
        if (isFirstCouple)
            this.firstCoupleFactor = source.getRootQuadruple().getArgument1();
        else {
            this.secondCoupleFactor = source.getRootQuadruple().getArgument1();
            if (secondCoupleFactor.equals("1") && isMinus)
                this.changeAllOperations(couple);
        }


        String latexNotation = couple.toLaTeXNotation().trim();
        String multiplierNotation = latexNotation.substring(latexNotation.indexOf('*') + 1);
        if (isFirstCouple)
            this.firstMultiplierNotation = multiplierNotation;
        else
            this.secondMultiplierNotation = multiplierNotation;


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

    private void changeAllOperations(ThreeAddressCode couple) {
        for (ExpandedQuadruple expandedQuadruple : couple.getExpandedQuadruples()) {
            if (expandedQuadruple.isPlusOrMinus()) {
                if (expandedQuadruple.isNegative()) {
                    ExpandedQuadruple minusFather = couple.findQuadrupleByArgument(expandedQuadruple.getResult());
                    if (minusFather.getArgument1().equals(expandedQuadruple.getResult()))
                        minusFather.setArgument1(expandedQuadruple.getArgument1());
                    else
                        minusFather.setArgument2(expandedQuadruple.getArgument2());
                } else
                    expandedQuadruple.setOperator(MathOperatorUtil.signalRule(expandedQuadruple.getOperator(), "-"));
            }
        }
        couple.clearNonUsedQuadruples();
    }

    private boolean isFirstQuadrupleMinus(ExpandedQuadruple iterationQuadruple, ThreeAddressCode couple) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.isFirstQuadrupleMinus(couple.findQuadrupleByResult(iterationQuadruple.getArgument1()), couple);
        }
        return iterationQuadruple.isNegative();
    }

    private ThreeAddressCode getResultSource(IRule rule, ThreeAddressCode ruleSource) throws InvalidAlgebraicExpressionException {
        return rule.handle(Collections.singletonList(ruleSource)).get(0).getSource().get(0);
    }


    public boolean isFirstCoupleEqualsSecond() {
        if (firstCoupleMultiplier.size() != secondCoupleMultiplier.size())
            if (listDoesntContainsMinus(firstCoupleMultiplier))
                return false;

        return firstMultiplierNotation.equals(secondMultiplierNotation);
    }

    private boolean listDoesntContainsMinus(List<ExpandedQuadruple> expandedQuadruples) {
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.isNegative())
                return false;
        }
        return true;
    }


    private void setMinusToFirstQuadruple(ThreeAddressCode secondCouple) {
        ExpandedQuadruple root = secondCouple.getRootQuadruple();
        secondCouple.addQuadrupleToList("MINUS", root.getArgument1(), "", root, true);
    }
}
