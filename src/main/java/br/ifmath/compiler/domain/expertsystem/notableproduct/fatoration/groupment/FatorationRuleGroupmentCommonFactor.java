package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.groupment;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.FatorationRuleIdentification;

import java.util.ArrayList;
import java.util.List;

import static br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.FatorationRuleIdentification.generateCouple;

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
        ThreeAddressCode firstCouple = generateCouple(this.source.getRootQuadruple(), source, true);

        ThreeAddressCode secondCouple = generateCouple(this.source.getRootQuadruple(), source, false);

        Couples couples = new Couples(firstCouple, secondCouple);

        this.changeQuadruples(couples);

    }

    private void changeQuadruples(Couples couples) {
        ExpandedQuadruple root = this.source.getRootQuadruple();
        root.setArgument1(couples.getFirstCoupleFactor());
        root.setOperator("*");
        root.setLevel(0);

        ExpandedQuadruple firstCouple = couples.getFirstCoupleMultiplier();
        ExpandedQuadruple innerQuadruple = this.source.findQuadrupleByResult(root.getArgument2());

        this.source.addQuadrupleToList(firstCouple.getOperator(), firstCouple.getArgument1(),
                firstCouple.getArgument2(), innerQuadruple, true).setLevel(1);


        innerQuadruple = this.source.findQuadrupleByResult(innerQuadruple.getArgument2());

        innerQuadruple.setArgument1(couples.getSecondCoupleFactor());
        innerQuadruple.setOperator("*");
        innerQuadruple.setLevel(0);

        ExpandedQuadruple secondQuadruple = couples.getSecondCoupleMultiplier();
        this.source.addQuadrupleToList(secondQuadruple.getOperator(), secondQuadruple.getArgument1(),
                secondQuadruple.getArgument2(), innerQuadruple, false).setLevel(1);
    }
}
