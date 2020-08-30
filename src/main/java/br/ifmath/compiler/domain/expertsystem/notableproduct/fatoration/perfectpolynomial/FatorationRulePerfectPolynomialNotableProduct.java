package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.perfectpolynomial;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;

import java.util.ArrayList;
import java.util.List;

public class FatorationRulePerfectPolynomialNotableProduct implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return true;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);

        String exponent = this.generateSum();
        String sign = this.getSign();
        String operation = (sign.equals("+")) ? "soma" : "diferença";

        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Identificamos os " +
                "elementos a e b e escrevemos o resultado como o quadrado da " + operation +
                ", no formato (a " + sign + " b)^" + exponent + "."));
        return steps;
    }

    private String generateSum() {
        ExpandedQuadruple argumentQuadruple = this.source.findQuadrupleByResult(this.source.getRootQuadruple().getArgument1());
        String argument1 = this.source.findDirectSonArgument(argumentQuadruple.getResult(), true);

        argumentQuadruple = this.source.getLastQuadruple(this.source.findQuadrupleByResult(source.getRootQuadruple().getArgument2()));
        String argument2 = argumentQuadruple.getArgument1();

        ExpandedQuadruple lastQuadrupleExponent = this.source.findQuadrupleByArgument(argumentQuadruple.getResult());
        ExpandedQuadruple lastQuadruple = this.source.findQuadrupleByArgument(lastQuadrupleExponent.getResult());
        ExpandedQuadruple coreQuadruple = new ExpandedQuadruple(lastQuadruple.getOperator(), argument1, argument2, "T1", 0, 1);

        List<ExpandedQuadruple> newQuadruples = new ArrayList<>();

        newQuadruples.add(coreQuadruple);

        String exponent = this.source.findQuadrupleByResult(source.getRootQuadruple().getArgument1()).getArgument2();
        ExpandedQuadruple squareQuadruple = new ExpandedQuadruple("^", "T1", exponent, "T2", 0, 0);

        newQuadruples.add(squareQuadruple);

        this.source = new ThreeAddressCode("T2", newQuadruples);

        return exponent;
    }

    private String getSign() {
        ExpandedQuadruple root = this.source.getRootQuadruple();
        ExpandedQuadruple insideQuadruple = this.source.findQuadrupleByResult(root.getArgument1());
        return insideQuadruple.getOperator();
    }
}
