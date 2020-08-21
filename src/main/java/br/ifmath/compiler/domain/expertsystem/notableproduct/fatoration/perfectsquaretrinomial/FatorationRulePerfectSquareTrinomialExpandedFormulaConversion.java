package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.perfectsquaretrinomial;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.FatorationRuleIdentification;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FatorationRulePerfectSquareTrinomialExpandedFormulaConversion implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return FatorationRuleIdentification.isPerfectSquareTrinomial(source.get(0));
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);

        this.adjustToExpandedFormula();

        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Escrevemos a " +
                "expressão no formato a^2 + 2 * a * b &#177; b^2, identificando os elementos que estão elevados ao " +
                "quadrado e os respectivos produtos."));
        return steps;
    }

    private void adjustToExpandedFormula() {
        ExpandedQuadruple root = this.source.getRootQuadruple();
        if (StringUtil.match(root.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            root = this.source.findQuadrupleByResult(root.getArgument1());
        ExpandedQuadruple middleQuadruple = this.source.findQuadrupleByResult(root.getArgument2());
        this.convertMiddleTerm(middleQuadruple, root);
        middleQuadruple.setArgument2(this.convertToRaisedByTwo(middleQuadruple.getArgument2()));
        root.setArgument1(this.convertToRaisedByTwo(this.source.getRootQuadruple().getArgument1()));
    }

    private String convertToRaisedByTwo(String argument) {
        NumericValueVariable nvv = new NumericValueVariable(argument);
        if (nvv.getValue() != 1)
            nvv.setValue((int) Math.sqrt(nvv.getValue()));
        if (nvv.getLabelPower() > 1)
            nvv.setLabelPower(nvv.getLabelPower() / 2);
        ExpandedQuadruple parenthesesQuadruple = new ExpandedQuadruple("", nvv.toString(), "", this.source.retrieveNextTemporary(), 0, 1);
        this.source.getExpandedQuadruples().add(parenthesesQuadruple);
        ExpandedQuadruple exponentQuadruple = new ExpandedQuadruple("^", parenthesesQuadruple.getResult(), "2", this.source.retrieveNextTemporary(), 0, 0);
        this.source.getExpandedQuadruples().add(exponentQuadruple);
        return exponentQuadruple.getResult();
    }

    private void convertMiddleTerm(ExpandedQuadruple middleQuadruple, ExpandedQuadruple firstArgument) {
        middleQuadruple.setArgument1(getConvertedTerm(this.source.findDirectSonArgument(middleQuadruple.getArgument2(), true)));
        this.source.addQuadrupleToList("*", this.getConvertedTerm(this.source.findDirectSonArgument(firstArgument.getArgument1(), true)), middleQuadruple.getResult(), firstArgument, false);
        this.source.addQuadrupleToList("*", "2", firstArgument.getArgument2(), firstArgument, false);
    }

    private String getConvertedTerm(String argument) {
        if (StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString()))
            return String.valueOf((int) Math.sqrt(Integer.parseInt(argument)));

        NumericValueVariable nvv = new NumericValueVariable(argument);
        nvv.setLabelPower(nvv.getLabelPower() / 2);
        if (nvv.getValue() != 1)
            nvv.setValue((int) Math.sqrt(nvv.getValue()));
        return nvv.toString();
    }
}
