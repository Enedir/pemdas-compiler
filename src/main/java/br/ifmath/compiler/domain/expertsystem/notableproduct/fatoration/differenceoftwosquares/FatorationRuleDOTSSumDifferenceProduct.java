package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.differenceoftwosquares;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;

import java.util.ArrayList;
import java.util.List;

public class FatorationRuleDOTSSumDifferenceProduct implements IRule {

    ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return true;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source){
        this.source = source.get(0);

        this.sumDifferenceProduct();

        this.source.clearNonUsedQuadruples();

        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        List<Step> steps = new ArrayList<>();
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Escrevemos a " +
                "expressão como o produto da soma pela diferença de dois termos."));
        return steps;
    }

    /**
     * Altera as quádruplas para representar a fórmula: (a + b) * (a - b)
     */
    private void sumDifferenceProduct() {
        ExpandedQuadruple root = this.source.getRootQuadruple();
        String firstTerm = this.source.findDirectSonArgument(root.getArgument1(), true);
        String secondTerm = this.source.findDirectSonArgument(root.getArgument2(), true);

        this.source.addQuadrupleToList("-",firstTerm,secondTerm,root,false).setLevel(1);
        this.source.addQuadrupleToList("+",firstTerm,secondTerm,root,true).setLevel(1);
        root.setOperator("*");
    }
}
