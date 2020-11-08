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
        return FatorationRuleIdentification.isSecondDegreeTrinomial(source.get(0));
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

    /**
     * Ajusta as quádruplas para ficarem na fórmula: x^2 + (b/a)x + (c/a).
     */
    private void convertToDivisionFormula() {

        ExpandedQuadruple argumentsQuadruple = this.source.getRootQuadruple();

        //obtém o monomio do valor a
        Monomial monomialA = this.getMonomialWithExponent(argumentsQuadruple, 2);

        //obtém o valor a em si
        String a = this.getElement(monomialA.toString());

        //obtém o monomio do valor b
        Monomial monomialB = this.getMonomialWithExponent(argumentsQuadruple, 1);

        //obtém o valor b em si
        String b = this.getElement(monomialB.toString());

        //obtém o monomio do valor c
        Monomial monomialC = this.getMonomialWithExponent(argumentsQuadruple, 0);

        //obtém o valor c em si
        String c = this.getElement(monomialC.toString());

        //cria a parte da formula que é x^2 +
        argumentsQuadruple.setArgument1(monomialA.getLiteral());
        argumentsQuadruple.setOperator("+");

        //cria a parte da formula que é (b/a)x +
        argumentsQuadruple = this.source.findQuadrupleByResult(argumentsQuadruple.getArgument2());
        argumentsQuadruple.setArgument1("(" + b + "/" + a + ")" + monomialB.getLiteral());
        argumentsQuadruple.setOperator("+");

        //cria a parte da formula que é (c/a) +
        argumentsQuadruple.setArgument2("(" + c + "/" + a + ")");
    }


    /**
     * Obtém um monômio de acordo com seu expoente, e assim identificando qual é o valor a, b e c.
     *
     * @param iterationQuadruple {@link ExpandedQuadruple} que representa a quádrupla inicial para ser buscado o monômio.
     * @param exponent           inteiro que representa o expoente a ser buscado, no qual podem ser:
     *                           <ul>
     *                              <li>2: significa um monomio do tipo x^2, ou seja, o valor a.</li>
     *                              <li>1: significa um monomio do tipo x, ou seja, o valor b.</li>
     *                              <li>0: significa um monomio que não tem parte literal, ou seja, o valor c.</li>
     *                           </ul>
     * @return {@link Monomial} representando o argumento encontrado para o valor de a, b ou c.
     */
    private Monomial getMonomialWithExponent(ExpandedQuadruple iterationQuadruple, int exponent) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return getMonomialWithExponent(this.source.findQuadrupleByResult(iterationQuadruple.getArgument1()), exponent);
        }

        Monomial iterationMonomialArgument = new Monomial(iterationQuadruple.getArgument1());

        //se não for o valor c
        if (exponent != 0) {
            if (iterationMonomialArgument.getCoefficient() != 0 && iterationMonomialArgument.getLiteralDegree() == exponent)
                return iterationMonomialArgument;
        } else {

            //valida se é somente um número diferente de 0
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

        //se não for o valor c
        if (exponent != 0) {
            if (iterationMonomialArgument.getCoefficient() != 0 && iterationMonomialArgument.getLiteralDegree() == exponent)
                return iterationMonomialArgument;
        } else {

            //valida se é somente um número diferente de 0
            if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.NATURAL_NUMBER.toString()) &&
                    !iterationQuadruple.getArgument2().equals("0")) {
                return iterationMonomialArgument;
            }
        }
        //se não identificar nenhum monomio para valor a, b ou c
        return new Monomial();
    }

    /**
     * Obtém o valor (a,b,c) a partir de um {@code argument}.
     *
     * @param argument {@link String} que representa um monômio que contém o valor desejado.
     * @return {@link String} que representa somente o valor a, b ou c.
     */
    private String getElement(String argument) {
        Monomial monomial = new Monomial(argument);
        ExpandedQuadruple argumentQuadruple = this.source.findQuadrupleByArgument(argument);

        //encontra o argumento nas quádruplas

        //se o argumento for igual ao argument1
        if (argumentQuadruple.getArgument1().equals(argument)) {

            //se for uma quádrupla "MINUS"
            if (argumentQuadruple.isNegative()) {
                return "-" + monomial.getCoefficient();
            }

            //se for a quádrupla inicial, quer dizer que é positivo
            if (this.source.getLeft().equals(argumentQuadruple.getResult())) {
                return String.valueOf(monomial.getCoefficient());
            }

            //verifica se é um valor positivo ou negativo e retorna
            ExpandedQuadruple father = this.source.findDirectFather(argumentQuadruple.getResult());
            return (father.isMinus()) ? "-" + monomial.getCoefficient() : String.valueOf(monomial.getCoefficient());
        }

        //verifica se o argument2 é um valor positivo ou negativo e retorna
        return (argumentQuadruple.isMinus()) ? "-" + monomial.getCoefficient() : String.valueOf(monomial.getCoefficient());
    }
}
