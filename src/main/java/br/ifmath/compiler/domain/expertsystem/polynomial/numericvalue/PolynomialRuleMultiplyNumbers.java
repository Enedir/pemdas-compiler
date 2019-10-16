package br.ifmath.compiler.domain.expertsystem.polynomial.numericvalue;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;


import java.util.ArrayList;
import java.util.List;


public class PolynomialRuleMultiplyNumbers implements IRule {
    private List<ExpandedQuadruple> expandedQuadruples;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return isThereTermsToMultiply(source.get(0).getExpandedQuadruples());
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) {
        List<Step> steps = new ArrayList<>();
        expandedQuadruples = new ArrayList<>();

        expandedQuadruples.addAll(sources.get(0).getExpandedQuadruples());
        List<ExpandedQuadruple> rightTimes = checkTimesOperation(sources.get(0).getExpandedQuadruples());

        multiply(sources, rightTimes);

        generateParameter(expandedQuadruples, rightTimes);

        if (!expandedQuadruples.isEmpty())
            sources.get(0).setExpandedQuadruples(expandedQuadruples);
        else
            expandedQuadruples = rightTimes;
        ThreeAddressCode step = new ThreeAddressCode(sources.get(0).getLeft(), expandedQuadruples);
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Multiplicando os valores."));

        return steps;
    }


    /**
     * Verifica quais quadruplas possuem a operaçao de multiplicacao.
     *
     * @param expandedQuadruples lista de quadruplas expandidas que será avaliada.
     * @return expandedQuadruples1 retorna apenas as quadruplas que possuem o operador de multiplicação.
     */
    private List<ExpandedQuadruple> checkTimesOperation(List<ExpandedQuadruple> expandedQuadruples) {
        List<ExpandedQuadruple> expandedQuadruples1 = new ArrayList<>();
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.isTimes()) {
                expandedQuadruples1.add(expandedQuadruple);
            }
        }
        return expandedQuadruples1;
    }


    /**
     * Realiza efetivamente as multiplicacoes dos termos.
     *
     * @param source Lista de codigos de tres endereços que foi gerado pelas etapaas iniciais do compilador.
     * @param times  Lista de quadruplas expandidas que possuem a operacação de multiplicação gerada pelo {@link #checkTimesOperation}.
     */
    private void multiply(List<ThreeAddressCode> source, List<ExpandedQuadruple> times) {
        String a, b;

        for (ExpandedQuadruple eq : times) {
            a = findProductFactor(source, eq.getArgument1());
            b = findProductFactor(source, eq.getArgument2());

            if (!StringUtil.isEmpty(a) || !StringUtil.isEmpty(b)) {
                if (!eq.isNegative())
                    expandedQuadruples.remove(eq);
                double result = Double.parseDouble(a) * Double.parseDouble(b);

                /**
                 * Realiza a verificação se o numero é negativo, caso seja,transforma o mesmo em positivo e atribui o operador MINUS a quadrupla do mesmo.
                 */
                if (result < 0) {
                    result *= -1;
                    eq.setOperator("MINUS");
                } else
                    eq.setOperator("");

                /**
                 * Verifica se o numero é um inteiro ou um double, e realiza o cast do resultado para o formato adequado.
                 */
                if (result % 1 == 0)
                    eq.setArgument1(String.valueOf((int) result));
                else
                    eq.setArgument1(String.valueOf(result));

                eq.setArgument2("");
            }
        }

    }


    /**
     * Encontra os fatores a e b para a multiplicacao, conforme a expressao: a * b, para cada uma
     * das quadruplas que tem alguma operação de multiplicacao.
     * @param source {@link ThreeAddressCode} que contem todas as quadruplas.
     * @param factor Fator a ser verificado e encontrado dentro das quadruplas(a ou b).
     * @return {@link String} que contem o valor numérico do fator.
     */
    private String findProductFactor(List<ThreeAddressCode> source, String factor) {
        if (StringUtil.match(factor,RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return findInnerProduct(source,factor);
        }
        return factor;
    }

    /**
     * @param expandedQuadruples Lista de quadruplas expandidadas geradas pelas etapas anteriores do compilador.
     * @param times              Lista de quadruplas expandidas que possuem a operacação de multiplicação gerada pelo {@link #checkTimesOperation}.
     */
    private void generateParameter(List<ExpandedQuadruple> expandedQuadruples, List<ExpandedQuadruple> times) {
        for (ExpandedQuadruple multipliedQuadruple : times) {
            String temporaryVarLabel = multipliedQuadruple.getResult();

            for (ExpandedQuadruple remainingQuadruple : expandedQuadruples) {

                if (remainingQuadruple.getArgument1() != null && remainingQuadruple.getArgument2() != null) {
                    /**
                     * Verifica se algum dos argumentos da quadrupla que esta sendo processada é igual a variavel temporaria que possui o resultado.
                     * Em caso positivo atribui o valor da temporaria ao argumento da quadrupla que esta sendo analisada.
                     */
                    if (remainingQuadruple.getArgument1().equals(temporaryVarLabel) && (multipliedQuadruple.getOperator().equals(""))) {
                        remainingQuadruple.setArgument1(multipliedQuadruple.getArgument1().replace(".", ","));
                    }

                    if (remainingQuadruple.getArgument2().equals(temporaryVarLabel)) {
                        remainingQuadruple.setArgument2(multipliedQuadruple.getArgument1().replace(".", ","));
                    }
                }
            }

        }
    }


    /**
     * Encontra o valor  dentro das variaveis temporarias que esta envolvida
     * em uma operacao de multiplicaçao e verifica caso o mesmo seja negativo, caso seja, adiciona o "-".
     *
     * @param source  Lista de codigos de tres endereços que foi gerado pelas etapaas iniciais do compilador.
     * @param tempVar Variavel provida pela quadrupla expandida que esta sendo processada no método {@link #multiply}.
     * @return Retorna o valor da quadrupla que foi acessada atraves do parametro tempVar, em caso de a quadrupla ser negativa (MINUS), retorna o valor concatenado com "-".
     */
    private String findInnerProduct(List<ThreeAddressCode> source, String tempVar) {
        ExpandedQuadruple innerQuadruple = source.get(0).findQuadrupleByResult(tempVar);
        if (!innerQuadruple.getOperator().equals("") && innerQuadruple.isNegative()) {
            expandedQuadruples.remove(innerQuadruple);
            return "-" + innerQuadruple.getArgument1();
        }
        return innerQuadruple.getArgument1();
    }

    /**
     * @param expandedQuadruples Lista de quadruplas expandidadas geradas pelas etapas anteriores do compilador.
     * @return Retorna true caso exista termos a serem multiplicados, retorna false caso contrario.
     */
    private boolean isThereTermsToMultiply(List<ExpandedQuadruple> expandedQuadruples) {
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.isTimes()) {
                return true;
            }
        }
        return false;
    }
}
