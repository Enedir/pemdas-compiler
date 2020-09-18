package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.groupment;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;

import java.util.ArrayList;
import java.util.List;

public class FatorationRuleGroupmentSumProduct implements IRule {
    ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return true;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);

        this.changeToSumProduct();

        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(),
                "Escrevemos a expressão como o produto da soma de dois termos, sem alterar o resultado final."));
        return steps;
    }

    private void changeToSumProduct() {
        ExpandedQuadruple root = this.source.getRootQuadruple();
        String firstTermArgument1 = root.getArgument1();
        ExpandedQuadruple innerQuadruple = this.source.findQuadrupleByResult(root.getArgument2());
        String firstTermOperation = innerQuadruple.getOperator();
        ExpandedQuadruple lastQuadruple = this.source.findQuadrupleByResult(innerQuadruple.getArgument2());
        String firstTermArgument2 = lastQuadruple.getArgument1();

        this.source.addQuadrupleToList(firstTermOperation, firstTermArgument1, firstTermArgument2,
                root, true).setLevel(1);
        root.setArgument2(lastQuadruple.getArgument2());
    }
}
