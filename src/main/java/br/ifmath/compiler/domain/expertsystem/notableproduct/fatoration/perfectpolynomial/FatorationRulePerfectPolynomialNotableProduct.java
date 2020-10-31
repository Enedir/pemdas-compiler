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

    /**
     * Altera as quádruplas para ficarem na fórmula: (a +- b)^2 ou (a +- b)^3
     *
     * @return {@link String} que representa qual o expoente utilizado para construir a fórmula.
     */
    private String generateSum() {

        ExpandedQuadruple argumentQuadruple = this.source.findQuadrupleByResult(this.source.getRootQuadruple().getArgument1());

        //obtém a quadrupla que tem o valor de a
        ExpandedQuadruple quadrupleA = getDirectSon(argumentQuadruple);

        //obtém o valor de a
        String argument1 = (quadrupleA.isNegative()) ? "-" + quadrupleA.getArgument1() : quadrupleA.getArgument1();

        //obtém a quadrupla que tem o valor de b
        argumentQuadruple = this.source.getLastQuadruple(this.source.findQuadrupleByResult(source.getRootQuadruple().getArgument2()));

        //obtém o valor de b
        String argument2 = argumentQuadruple.getArgument1();

        //cria a quadrupla principal que terá os dois valores
        List<ExpandedQuadruple> newQuadruples = new ArrayList<>();
        ExpandedQuadruple coreQuadruple = new ExpandedQuadruple(source.getRootQuadruple().getOperator(), argument1, argument2, "T1", 0, 1);
        newQuadruples.add(coreQuadruple);

        //cria a quádrupla que terá o exponent elevando os dois valores
        String exponent = this.source.findQuadrupleByResult(source.getRootQuadruple().getArgument1()).getArgument2();
        ExpandedQuadruple exponentQuadruple = new ExpandedQuadruple("^", "T1", exponent, "T2", 0, 0);
        newQuadruples.add(exponentQuadruple);

        this.source = new ThreeAddressCode("T2", newQuadruples);

        return (exponent.equals("2")) ? "&sup2;." : "&sup3;.";
    }

    /**
     * Identifica se existe uma operação de soma ou subtração entre os valores a e b.
     *
     * @return {@link String} que representa se é uma operçaão de soma ou subtração.
     */
    private String getSign() {
        ExpandedQuadruple root = this.source.getRootQuadruple();
        if (StringUtil.match(root.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            root = this.source.findQuadrupleByResult(root.getArgument1());
        return (root.getOperator().equals("+")) ? "&plus;" : "&minus;";
    }

    /**
     * Obtém o filho direto válido da quádrupla {@code iterationQuadruple}.
     *
     * @param iterationQuadruple {@link ExpandedQuadruple} de onde será o começo da busca pelo filho.
     * @return {@link ExpandedQuadruple} que contém o filho direto da {@code iterationQuadruple}.
     */
    private ExpandedQuadruple getDirectSon(ExpandedQuadruple iterationQuadruple) {
        ExpandedQuadruple son = this.source.findQuadrupleByResult(iterationQuadruple.getArgument1());
        if (StringUtil.match(son.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getDirectSon(son);
        }
        return son;
    }
}
