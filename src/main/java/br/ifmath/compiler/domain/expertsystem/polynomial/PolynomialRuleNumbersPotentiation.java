package br.ifmath.compiler.domain.expertsystem.polynomial;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PolynomialRuleNumbersPotentiation implements IRule {
    private List<ExpandedQuadruple> expandedQuadruples;


    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return isThereTermsToPowerTo(source.get(0).getExpandedQuadruples());
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();
        expandedQuadruples = new ArrayList<>();

        expandedQuadruples.addAll(sources.get(0).getExpandedQuadruples());
        List<ExpandedQuadruple> rightPower = checkPotentiationOperation(sources.get(0).getExpandedQuadruples());

        power(sources, rightPower);

        generateParameter(expandedQuadruples, rightPower);
        if (!expandedQuadruples.isEmpty())
            sources.get(0).setExpandedQuadruples(expandedQuadruples);
        else
            expandedQuadruples = rightPower;
        ThreeAddressCode step = new ThreeAddressCode("x", sources.get(0).getComparison(), sources.get(0).getRight(), expandedQuadruples);
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation(), step.toMathNotation().trim(), "Elevando os valores a suas potências."));

        return steps;
    }


    /**
     * Verifica quais quadruplas possuem a operaçao de potencia.
     *
     * @param expandedQuadruples lista de quadruplas expandidas que será avaliada.
     * @return expandedQuadruples1 retorna apenas as quadruplas que possuem o operador de potenciaçao.
     */
    private List<ExpandedQuadruple> checkPotentiationOperation(List<ExpandedQuadruple> expandedQuadruples) {
        List<ExpandedQuadruple> expandedQuadruples1 = new ArrayList<>();
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.getOperator().equals("^")) {
                expandedQuadruples1.add(expandedQuadruple);
            }
        }
        return expandedQuadruples1;
    }


    /**
     * Realiza a operaçao de potencia nas quadruplas que possuem a operaçao
     *
     * @param source Lista de codigos de tres endereços que foi gerado pelas etapaas iniciais do compilador.
     * @param powers Lista de quadruplas expandidas que possuem a operacação de multiplicação gerada pelo {@link #checkPotentiationOperation}.
     */
    private void power(List<ThreeAddressCode> source, List<ExpandedQuadruple> powers) {
        String a, b;

        for (ExpandedQuadruple eq : powers) {
            if (StringUtil.match(eq.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                a = findInnerPower(source, eq.getArgument1());
            } else {
                a = eq.getArgument1();
            }

            if (StringUtil.match(eq.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                b = findInnerPower(source, eq.getArgument2());
            } else {
                b = eq.getArgument2();
            }

            if (!StringUtil.isEmpty(a) || !StringUtil.isEmpty(b)) {
                expandedQuadruples.remove(eq);

                /**
                 * Mascara para limitar as casas decimais em caso de potencia negativa.
                 */
                DecimalFormat decimalFormat = new DecimalFormat("#.0000");
                double result = Math.pow(Double.parseDouble(a), Double.parseDouble(b));
                result = Double.parseDouble(decimalFormat.format(result).replace(",", "."));

                /**
                 * Verifica se o numero é um inteiro ou um double, e realiza o cast do resultado para o formato adequado.
                 */
                if (result % 1 == 0)
                    eq.setArgument1(String.valueOf((int) result));
                else
                    eq.setArgument1(String.valueOf(result));
                eq.setOperator("");
                eq.setArgument2("");
            }
        }

    }


    /**
     * @param expandedQuadruples Lista de quadruplas expandidadas geradas pelas etapas anteriores do compilador.
     * @param powers             Lista de quadruplas expandidas que possuem a operacação de multiplicação gerada pelo {@link #checkPotentiationOperation}.
     */
    private void generateParameter(List<ExpandedQuadruple> expandedQuadruples, List<ExpandedQuadruple> powers) {
        for (ExpandedQuadruple poweredQuadruple : powers) {
            String temporaryVarLabel = poweredQuadruple.getResult();
            for (ExpandedQuadruple remainingQuadruple : expandedQuadruples) {
                if (remainingQuadruple.getArgument1().equals(temporaryVarLabel)) {
                    remainingQuadruple.setArgument1(poweredQuadruple.getArgument1());
                }
                if (!remainingQuadruple.getOperator().equals("MINUS")) {
                    if (remainingQuadruple.getArgument2().equals(temporaryVarLabel)) {
                        remainingQuadruple.setArgument2(poweredQuadruple.getArgument1());
                    }
                }

            }
        }
    }


    /**
     * Encontra o valor  dentro das variaveis temporarias que esta envolvida
     * em uma operacao de potenciacao e verifica caso o mesmo seja negativo, caso seja, adiciona o "-".
     *
     * @param source  Lista de codigos de tres endereços que foi gerado pelas etapaas iniciais do compilador.
     * @param tempVar Variavel provida pela quadrupla expandida que esta sendo processada no método {@link #power}.
     * @return Retorna o valor da quadrupla que foi acessada atraves do parametro tempVar, em caso de a quadrupla ser negativa (MINUS), retorna o valor concatenado com "-".
     */
    private String findInnerPower(List<ThreeAddressCode> source, String tempVar) {
        ExpandedQuadruple innerQuadruple = source.get(0).findQuadrupleByResult(tempVar);
        if (innerQuadruple.getArgument1() != null || innerQuadruple.getArgument2() != null) {
            if (innerQuadruple.getOperator().equals("MINUS")) {
                this.expandedQuadruples.remove(innerQuadruple);
                return "-" + innerQuadruple.getArgument1();
            }
            return innerQuadruple.getArgument1();
        }
        return null;
    }


    /**
     * @param expandedQuadruples Lista de quadruplas expandidadas geradas pelas etapas anteriores do compilador.
     * @return Retorna true caso exista termos a serem elevados a potencia, retorna false caso contrario.
     */
    private boolean isThereTermsToPowerTo(List<ExpandedQuadruple> expandedQuadruples) {
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.getOperator().equals("^")) {
                return true;
            }
        }
        return false;
    }
}