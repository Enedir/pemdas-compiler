package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.groupment;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.FatorationRuleIdentification;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FatorationRuleGroupmentCommonFactor implements IRule {

    ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        try {
            return FatorationRuleIdentification.isGroupment(source.get(0));
        } catch (InvalidAlgebraicExpressionException e) {
            return false;
        }
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);

        this.generateDoubleCommonFactor();

        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(),
                "Colocamos em evidência o elemento que temos em comum nos primeiros termos e somamos ao elemento " +
                        "que possuímos em comum nos últimos termos."));
        return steps;
    }

    private void generateDoubleCommonFactor() throws InvalidAlgebraicExpressionException {
        int argumentsCount = FatorationRuleIdentification.argumentsCount(source.getRootQuadruple(), source);
        List<ThreeAddressCode> couplesSources = FatorationRuleIdentification.generateCouples(source, argumentsCount);

        ThreeAddressCode firstCouple = couplesSources.get(0);
        ThreeAddressCode secondCouple = couplesSources.get(1);

        String secondCoupleOperation = this.source.findQuadrupleByArgument(secondCouple.getLeft()).getOperator();

        Couples couples = new Couples(firstCouple, secondCouple, secondCoupleOperation);

        this.changeQuadruples(couples);

    }

    private void changeQuadruples(Couples couples) {
        ExpandedQuadruple root = this.source.getRootQuadruple();
        root.setArgument1(couples.getFirstCoupleFactor());
        root.setOperator("*");
        root.setLevel(0);

        List<ExpandedQuadruple> firstCouple = couples.getFirstCoupleMultiplier();
        this.adjustMinusQuadruple(firstCouple);
        ExpandedQuadruple innerQuadruple = this.source.findQuadrupleByResult(root.getArgument2());
        Collections.reverse(firstCouple);

        String lastInsertQuadruple = null;
        String minusQuadrupleResult = null;
        for (ExpandedQuadruple expandedQuadruple : firstCouple) {

            ExpandedQuadruple insertQuadruple = this.source.addQuadrupleToList(expandedQuadruple.getOperator(), expandedQuadruple.getArgument1(),
                    expandedQuadruple.getArgument2(), innerQuadruple, true);

            if (insertQuadruple.isNegative())
                minusQuadrupleResult = insertQuadruple.getResult();

            insertQuadruple.setLevel(1);

            if (StringUtil.match(insertQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                insertQuadruple.setArgument1(minusQuadrupleResult);
            }

            if (StringUtil.match(insertQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                insertQuadruple.setArgument2(lastInsertQuadruple);
            }

            lastInsertQuadruple = insertQuadruple.getResult();
        }

        innerQuadruple.setOperator(couples.getSecondCoupleOperator());

        innerQuadruple = this.source.findQuadrupleByResult(innerQuadruple.getArgument2());

        innerQuadruple.setArgument1(couples.getSecondCoupleFactor());
        innerQuadruple.setOperator("*");
        innerQuadruple.setLevel(0);

        List<ExpandedQuadruple> secondQuadruple = couples.getSecondCoupleMultiplier();
        Collections.reverse(secondQuadruple);

        for (ExpandedQuadruple expandedQuadruple : secondQuadruple) {
            ExpandedQuadruple insertQuadruple = this.source.addQuadrupleToList(expandedQuadruple.getOperator(), expandedQuadruple.getArgument1(),
                    expandedQuadruple.getArgument2(), innerQuadruple, false);

            if (insertQuadruple.isNegative())
                minusQuadrupleResult = insertQuadruple.getResult();

            if (StringUtil.match(insertQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                insertQuadruple.setArgument1(minusQuadrupleResult);
            }

            if (StringUtil.match(insertQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                insertQuadruple.setArgument2(lastInsertQuadruple);
            }

            insertQuadruple.setLevel(1);


            lastInsertQuadruple = insertQuadruple.getResult();
        }
    }

    private void adjustMinusQuadruple(List<ExpandedQuadruple> couple) {
        ExpandedQuadruple minusQuadruple = null;
        for (ExpandedQuadruple expandedQuadruple : couple) {
            if (expandedQuadruple.isNegative())
                minusQuadruple = expandedQuadruple;
        }

        if (minusQuadruple != null) {
            couple.remove(minusQuadruple);
            couple.add(minusQuadruple);
        }
    }
}
