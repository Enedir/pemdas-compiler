package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
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
    public List<Step> handle(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);

        this.adjustToExpandedFormula();

        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Identificação do tipo de fatoração a partir da equação inicial: "));
        return steps;
    }

    private void adjustToExpandedFormula() {
        ExpandedQuadruple root = this.source.getRootQuadruple();
        if (StringUtil.match(root.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            root = this.source.findQuadrupleByResult(root.getArgument1());
        ExpandedQuadruple middleQuadruple = this.source.findQuadrupleByResult(root.getArgument2());
        root.setArgument1(this.convertToRaisedByTwo(this.source.getRootQuadruple().getArgument1()));
        middleQuadruple.setArgument2(this.convertToRaisedByTwo(middleQuadruple.getArgument2()));
        this.convertMiddleTerm(middleQuadruple, root.getArgument1());


    }

    private String convertToRaisedByTwo(String argument) {
        if (StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString())) {
            int sqrtValue = (int) Math.sqrt(Integer.parseInt(argument));
            return sqrtValue + "^2";
        }
        NumericValueVariable nvv = new NumericValueVariable(argument);
        if (nvv.getValue() > 1)
            nvv.setValue((int) Math.sqrt(nvv.getValue()));

        if (nvv.getLabelPower() >= 4)
            nvv.setLabelPower(nvv.getLabelPower() / 2);

        return nvv.toString();
    }

    private void convertMiddleTerm(ExpandedQuadruple middleQuadruple, String firstArgument) {
        //TODO fazer incluir novas quadruplas uma dentro da outra, parecido como foi feito nos produtos notaveis
    }

    private String getConvertedTerm(String argument) {
        if (StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString()))
            return argument.substring(0, argument.indexOf('^'));
        NumericValueVariable nvv = new NumericValueVariable(argument);
        if (nvv.getLabelPower() == 2)
            nvv.setLabelPower(nvv.getLabelPower() - 1);
        return nvv.toString();
    }
}
