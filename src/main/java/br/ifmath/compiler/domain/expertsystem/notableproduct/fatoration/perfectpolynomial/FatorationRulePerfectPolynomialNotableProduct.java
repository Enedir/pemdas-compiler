package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.perfectpolynomial;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

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
        String operation = (sign.equals("&plus;")) ? "soma" : "diferença";

        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Identificamos os " +
                "elementos &ascr; e &bscr;, e escrevemos o resultado como o quadrado da " + operation +
                ", no formato (&ascr; " + sign + " &bscr;)" + exponent));
        return steps;
    }

    private String generateSum() {
        ExpandedQuadruple argumentQuadruple = this.source.findQuadrupleByResult(this.source.getRootQuadruple().getArgument1());
        ExpandedQuadruple argument1Quadruple = getDirectSon(argumentQuadruple);

        String argument1 = (argument1Quadruple.isNegative()) ? "-" + argument1Quadruple.getArgument1() : argument1Quadruple.getArgument1();

        argumentQuadruple = this.source.getLastQuadruple(this.source.findQuadrupleByResult(source.getRootQuadruple().getArgument2()));
        String argument2 = argumentQuadruple.getArgument1();

        ExpandedQuadruple coreQuadruple = new ExpandedQuadruple(source.getRootQuadruple().getOperator(), argument1, argument2, "T1", 0, 1);

        List<ExpandedQuadruple> newQuadruples = new ArrayList<>();

        newQuadruples.add(coreQuadruple);

        String exponent = this.source.findQuadrupleByResult(source.getRootQuadruple().getArgument1()).getArgument2();
        ExpandedQuadruple squareQuadruple = new ExpandedQuadruple("^", "T1", exponent, "T2", 0, 0);

        newQuadruples.add(squareQuadruple);

        this.source = new ThreeAddressCode("T2", newQuadruples);

        return (exponent.equals("2")) ? "&sup2;." : "&sup3;.";
    }

    private String getSign() {
        ExpandedQuadruple root = this.source.getRootQuadruple();
        if (StringUtil.match(root.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            root = this.source.findQuadrupleByResult(root.getArgument1());
        return (root.getOperator().equals("+")) ? "&plus;" : "&minus;";
    }

    private ExpandedQuadruple getDirectSon(ExpandedQuadruple iterationQuadruple) {
        ExpandedQuadruple son = this.source.findQuadrupleByResult(iterationQuadruple.getArgument1());
        if (StringUtil.match(son.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getDirectSon(son);
        }
        return son;
    }
}
