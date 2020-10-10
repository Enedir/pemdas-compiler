package br.ifmath.compiler.domain.expertsystem.notableproduct.notableproducts;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.Monomial;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class NotableProductsRulePower implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return true;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        this.source = source.get(0);

        this.numbersPower();
        this.adjustNegativeArgument();

        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        List<Step> steps = new ArrayList<>();
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Resolvendo as operações de potência."));

        return steps;
    }

    /**
     * Realiza a potenciação das quádruplas.
     */
    private void numbersPower() {
        //Tem de ser um fori, pois são incluídas novas quadruplas a lista, mas deve manter as iterações
        for (int i = 0; i < this.source.getExpandedQuadruples().size(); i++) {
            ExpandedQuadruple expandedQuadruple = this.source.getExpandedQuadruples().get(i);

            if (!expandedQuadruple.isNegative() && expandedQuadruple.isPotentiation()) {

                String base = this.getPowerBase(expandedQuadruple.getArgument1(), this.source);
                if (!base.equals("")) {
                    String result;
                    //Se a base (positiva ou negativa) é um monômio com coeficiente
                    if (StringUtil.matchAny(base.replace("-", ""), RegexPattern.VARIABLE_WITH_COEFFICIENT.toString(),
                            RegexPattern.VARIABLE_WITH_EXPONENT.toString())) {
                        Monomial monomial = new Monomial();
                        monomial.setAttributesFromString(base);

                        String exponent = expandedQuadruple.getArgument2();

                        //Potenciação do coeficiente
                        monomial.setCoefficient(
                                (int) Math.pow(monomial.getCoefficient(), Double.parseDouble(exponent)));

                        //Ajusta o grau do monomio
                        int power = monomial.getLiteralDegree();
                        if (power != 1)
                            exponent = String.valueOf(power * Integer.parseInt(expandedQuadruple.getArgument2()));

                        //Se for um monomio de grau 2 ou maior
                        if (monomial.getLiteral().contains("^")) {
                            monomial.setLiteralDegree(Integer.parseInt(exponent));
                        } else {
                            monomial.setLiteral(monomial.getLiteral() + "^" + exponent);
                        }

                        result = monomial.toString();
                    } else
                        //Se for um monômio sem coeficiente
                        result = String.valueOf(
                                Math.round(
                                        Math.pow(Double.parseDouble(base), Double.parseDouble(expandedQuadruple.getArgument2()))
                                )
                        );

                    //Ajusta as quádruplas de acordo com o resultado
                    ExpandedQuadruple father = this.source.findQuadrupleByArgument(expandedQuadruple.getResult());

                    //coloca o resultado no argumento certo
                    if (father.getArgument1().equals(expandedQuadruple.getResult()))
                        father.setArgument1(result);
                    else
                        father.setArgument2(result);

                } else {
                    //Se o primeiro argumento da quadrupla não for um monomio ou número
                    ExpandedQuadruple innerQuadruple = this.source.findQuadrupleByResult(expandedQuadruple.getArgument1());
                    String basePower = innerQuadruple.getArgument1();
                    if (innerQuadruple.isNegative())
                        basePower = innerQuadruple.getResult();

                    //Cria o monomio no formato  basepower ^ exponent
                    String argument = this.applyPower(basePower, expandedQuadruple.getArgument2());
                    ExpandedQuadruple father = this.source.findQuadrupleByArgument(expandedQuadruple.getResult());
                    if (father.getArgument1().equals(expandedQuadruple.getResult())) {
                        father.setArgument1(argument);
                    } else {
                        father.setArgument2(argument);
                    }
                }
            }
        }


    }

    /**
     * Monta um argumento com base e potência.
     *
     * @param argument {@link String} que representa a base.
     * @param exponent {@link String} que representa a potência.
     * @return {@link String} no formato {@code argument} ^ {@code exponent}.
     */
    private String applyPower(String argument, String exponent) {
        if (StringUtil.match(argument, RegexPattern.TEMPORARY_VARIABLE.toString()))
            return applyMinusPower(this.source.findQuadrupleByResult(argument).getArgument1(), exponent);
        if (StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString())) {
            return String.valueOf(Math.round(Math.pow(Double.parseDouble(argument), Double.parseDouble(exponent))));
        }
        return argument + "^" + exponent;
    }

    /**
     * Aplica a potência em casos de haver um valor negativo e ajusta seu sinal.
     *
     * @param argument {@link String} que representa a base.
     * @param exponent {@link String} que representa a potência.
     * @return {@link String} no formato {@code argument} ^ {@code exponent}.
     */
    private String applyMinusPower(String argument, String exponent) {
        int degree = Integer.parseInt(exponent);
        if (degree % 2 != 0) {
            String result = this.source.retrieveNextTemporary();
            this.source.getExpandedQuadruples().add(new ExpandedQuadruple("MINUS", argument + "^" + degree, "", result, 0, 1));
            return result;
        }
        return argument + "^" + degree;
    }

    /**
     * Obtém a base da potenciação a partir de um argumento.
     *
     * @param argument {@link String} que será analizado para identificar ou não a base.
     * @param source   {@link ThreeAddressCode} que contém todas as quádruplas.
     * @return {@link String} que contém a base, ou vazio ("") caso não ser identificado.
     */
    private String getPowerBase(String argument, ThreeAddressCode source) {
        //Se for um número
        if (StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString())) {
            return argument;
        }

        ExpandedQuadruple innerQuadruple = source.findQuadrupleByResult(argument);
        if (innerQuadruple != null) {
            //retorna a base com seu sinal
            if (StringUtil.matchAny(innerQuadruple.getArgument1(), RegexPattern.NATURAL_NUMBER.toString(),
                    RegexPattern.VARIABLE_WITH_COEFFICIENT.toString(), RegexPattern.VARIABLE_WITH_EXPONENT.toString())) {
                return (innerQuadruple.isNegative()) ? "-" + innerQuadruple.getArgument1() : innerQuadruple.getArgument1();
            }
        }
        return "";
    }


    /**
     * Ajusta as quadruplas com valores negativos para quadruplas de MINUS
     */
    private void adjustNegativeArgument() {
        ExpandedQuadruple root = this.source.findQuadrupleByResult(this.source.getLeft());
        if (root.getArgument1().startsWith("-")) {
            this.source.addQuadrupleToList("MINUS", root.getArgument1().replace("-", ""), "", root, true);
        }

    }

}
