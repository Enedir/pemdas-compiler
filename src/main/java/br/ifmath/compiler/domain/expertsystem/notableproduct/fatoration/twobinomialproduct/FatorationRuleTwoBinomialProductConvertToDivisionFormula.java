package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.twobinomialproduct;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.FatorationRuleIdentification;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.Monomial;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FatorationRuleTwoBinomialProductConvertToDivisionFormula implements IRule {

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
                "Escrevemos a expressão no formato &xscr;&sup2; &plus; (&bscr;&sol;&ascr;)&xscr; &plus; " +
                        "(&cscr;&sol;&ascr;), identificando os elementos que estão elevados ao quadrado e as respectivas divisões."));
        return steps;
    }

    private void convertToDivisionFormula() {
        ExpandedQuadruple actualQuadruple = this.source.getRootQuadruple();
        String rootArgument1 = actualQuadruple.getArgument1();
        if (StringUtil.match(actualQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple minusQuadruple = this.source.findQuadrupleByResult(rootArgument1);
            rootArgument1 = "-" + minusQuadruple.getArgument1();
        }
        Monomial term = new Monomial(rootArgument1);
        String a = String.valueOf(term.getCoefficient());
        actualQuadruple.setArgument1(term.getLiteral());
        String operation = actualQuadruple.getOperator();
        actualQuadruple.setOperator("+");

        actualQuadruple = this.source.findQuadrupleByResult(actualQuadruple.getArgument2());
        term.setAttributesFromString(actualQuadruple.getArgument1());
        if (operation.equals("-"))
            term.setCoefficient(term.getCoefficient() * -1);
        actualQuadruple.setArgument1("(" + term.getCoefficient() + "/" + a + ")" + term.getLiteral());
        operation = actualQuadruple.getOperator();
        actualQuadruple.setOperator("+");

        term.setAttributesFromString(actualQuadruple.getArgument2());
        if (operation.equals("-"))
            term.setCoefficient(term.getCoefficient() * -1);
        actualQuadruple.setArgument2("(" + term.getCoefficient() + "/" + a + ")");
    }
}
