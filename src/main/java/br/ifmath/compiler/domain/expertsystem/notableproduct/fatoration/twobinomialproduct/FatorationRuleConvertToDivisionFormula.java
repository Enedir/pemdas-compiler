package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.twobinomialproduct;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.FatorationRuleIdentification;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FatorationRuleConvertToDivisionFormula implements IRule {

    ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return FatorationRuleIdentification.isTwoBinomialProduct(source.get(0));
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);

        this.convertToDivisionFormula();

        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(),
                "Escrevemos a expressão no formato x^2 + (b/a)x + (c/a), identificando os elementos que estão " +
                        "elevados ao quadrado e as respectivas divisões. "));
        return steps;
    }

    private void convertToDivisionFormula() {
        ExpandedQuadruple actualQuadruple = this.source.getRootQuadruple();
        String rootArgument1 = actualQuadruple.getArgument1();
        if (StringUtil.match(actualQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple minusQuadruple = this.source.findQuadrupleByResult(rootArgument1);
            rootArgument1 = "-" + minusQuadruple.getArgument1();
        }
        NumericValueVariable term = new NumericValueVariable(rootArgument1);
        String a = String.valueOf(term.getValue());
        actualQuadruple.setArgument1(term.getLabel());
        String operation = actualQuadruple.getOperator();
        actualQuadruple.setOperator("+");

        actualQuadruple = this.source.findQuadrupleByResult(actualQuadruple.getArgument2());
        term.setAttributesFromString(actualQuadruple.getArgument1());
        if (operation.equals("-"))
            term.setValue(term.getValue() * -1);
        actualQuadruple.setArgument1("(" + term.getValue() + "/" + a + ")" + term.getLabel());
        operation = actualQuadruple.getOperator();
        actualQuadruple.setOperator("+");

        term.setAttributesFromString(actualQuadruple.getArgument2());
        if (operation.equals("-"))
            term.setValue(term.getValue() * -1);
        actualQuadruple.setArgument2("(" + term.getValue() + "/" + a + ")");
    }
}
