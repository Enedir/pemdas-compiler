package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.differenceoftwosquares;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.FatorationRuleIdentification;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.perfectsquaretrinomial.FatorationRulePerfectSquareTrinomialExpandedFormulaConversion;

import java.util.ArrayList;
import java.util.List;

public class FatorationRuleDOTSExpandedFormula implements IRule {

    ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return FatorationRuleIdentification.isDifferenceOfTwoSquares(source.get(0));
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        this.source = source.get(0);

        this.adjustToExpandedFormula();

        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        List<Step> steps = new ArrayList<>();
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Escrevemos a " +
                "expressão no formato a^2 - b^2, identificando os elementos que estão elevados ao quadrado."));
        return steps;
    }

    private void adjustToExpandedFormula() {
        ExpandedQuadruple root = this.source.getRootQuadruple();
        root.setArgument1(this.reduceToRaisedByTwo(root.getArgument1()));
        root.setArgument2(this.reduceToRaisedByTwo(root.getArgument2()));
    }

    private String reduceToRaisedByTwo(String argument) {
        return FatorationRulePerfectSquareTrinomialExpandedFormulaConversion.reduceToRaisedValue(argument, this.source, true);
    }
}
