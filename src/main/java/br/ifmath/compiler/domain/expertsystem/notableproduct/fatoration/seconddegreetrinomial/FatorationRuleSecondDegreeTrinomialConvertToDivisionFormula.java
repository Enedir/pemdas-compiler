package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.seconddegreetrinomial;

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

public class FatorationRuleSecondDegreeTrinomialConvertToDivisionFormula implements IRule {

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

        ExpandedQuadruple argumentsQuadruple = this.source.getRootQuadruple();

        Monomial monomialA = this.getMonomialWithExponent(argumentsQuadruple, 2);
        String a = this.getElement(monomialA.toString());

        Monomial monomialB = this.getMonomialWithExponent(argumentsQuadruple, 1);
        String b = this.getElement(monomialB.toString());

        Monomial monomialC = this.getMonomialWithExponent(argumentsQuadruple, 0);
        String c = this.getElement(monomialC.toString());

        argumentsQuadruple.setArgument1(monomialA.getLiteral());
        argumentsQuadruple.setOperator("+");

        argumentsQuadruple = this.source.findQuadrupleByResult(argumentsQuadruple.getArgument2());
        argumentsQuadruple.setArgument1("(" + b + "/" + a + ")" + monomialB.getLiteral());
        argumentsQuadruple.setOperator("+");

        argumentsQuadruple.setArgument2("(" + c + "/" + a + ")");
    }


    private Monomial getMonomialWithExponent(ExpandedQuadruple iterationQuadruple, int exponent) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return getMonomialWithExponent(this.source.findQuadrupleByResult(iterationQuadruple.getArgument1()), exponent);
        }

        Monomial iterationMonomialArgument = new Monomial(iterationQuadruple.getArgument1());
        if (exponent != 0) {
            if (iterationMonomialArgument.getCoefficient() != 0 && iterationMonomialArgument.getLiteralDegree() == exponent)
                return iterationMonomialArgument;
        } else {
            if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.NATURAL_NUMBER.toString()) &&
                    !iterationQuadruple.getArgument1().equals("0")) {
                return iterationMonomialArgument;
            }
        }

        if (iterationQuadruple.isNegative())
            iterationQuadruple = this.source.findQuadrupleByArgument(iterationQuadruple.getResult());

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return getMonomialWithExponent(this.source.findQuadrupleByResult(iterationQuadruple.getArgument2()), exponent);
        }

        iterationMonomialArgument = new Monomial(iterationQuadruple.getArgument2());

        if (exponent != 0) {
            if (iterationMonomialArgument.getCoefficient() != 0 && iterationMonomialArgument.getLiteralDegree() == exponent)
                return iterationMonomialArgument;
        } else {
            if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.NATURAL_NUMBER.toString()) &&
                    !iterationQuadruple.getArgument2().equals("0")) {
                return iterationMonomialArgument;
            }
        }
        return new Monomial();
    }

    private String getElement(String argument) {
        Monomial monomial = new Monomial(argument);
        ExpandedQuadruple argumentQuadruple = this.source.findQuadrupleByArgument(argument);
        if (argumentQuadruple.getArgument1().equals(argument)) {
            if (argumentQuadruple.isNegative()) {
                return "-" + monomial.getCoefficient();
            }
            if (this.source.getLeft().equals(argumentQuadruple.getResult())) {
                return String.valueOf(monomial.getCoefficient());
            }
            ExpandedQuadruple father = this.source.findDirectFather(argumentQuadruple.getResult());
            return (father.isMinus()) ? "-" + monomial.getCoefficient() : String.valueOf(monomial.getCoefficient());
        }
        return (argumentQuadruple.isMinus()) ? "-" + monomial.getCoefficient() : String.valueOf(monomial.getCoefficient());
    }
}
