package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.groupment;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.FatorationRuleIdentification;

import java.util.ArrayList;
import java.util.List;

import static br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.FatorationRuleIdentification.generateFirstCouple;

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
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Verificamos o elemento que temos em comum e colocamos em evidência."));
        return steps;
    }

    private void generateDoubleCommonFactor() throws InvalidAlgebraicExpressionException {
        ThreeAddressCode firstCouple = generateFirstCouple(source);

        ThreeAddressCode secondCouple = new ThreeAddressCode();
        List<ExpandedQuadruple> quadruples = new ArrayList<>();
        quadruples.add(source.getLastQuadruple(source.getRootQuadruple()));
        secondCouple.setExpandedQuadruples(quadruples);
        secondCouple.setLeft(secondCouple.getExpandedQuadruples().get(0).getResult());

        Couples couples = new Couples(firstCouple, secondCouple);

    }
}
