package br.ifmath.compiler.domain.expertsystem.polynomial.numericvalue;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.NumberUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class PolynomialRuleParenthesesOperations implements IRule {
    private List<ExpandedQuadruple> expandedQuadruples;
    private List<Step> steps = new ArrayList<>();


    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return isThereOperationsBetweenParentheses(source.get(0).getExpandedQuadruples());
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException {
        expandedQuadruples = new ArrayList<>();
        expandedQuadruples.addAll(source.get(0).getExpandedQuadruples());

        resolveInnerOperations(source);
        clearNonUsedQuadruples();

        ThreeAddressCode step = new ThreeAddressCode(source.get(0).getLeft(), expandedQuadruples);
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Realizando as operações dentro dos parênteses."));

        return steps;
    }

    private void clearNonUsedQuadruples() {
        Iterator<ExpandedQuadruple> i = expandedQuadruples.iterator();
        while (i.hasNext()) {
            ExpandedQuadruple expandedQuadruple = i.next();
            if (expandedQuadruple.getLevel() != 0)
                i.remove();
        }
    }

    private void resolveInnerOperations(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException {
        for (int i = 0; i < expandedQuadruples.size() - 1; i++) {
            ExpandedQuadruple expandedQuadruple = expandedQuadruples.get(i);
            if (expandedQuadruple.getLevel() != 0) {
                selectCorrectOperation(expandedQuadruple, source);
            }
        }
    }

    private void selectCorrectOperation(ExpandedQuadruple expandedQuadruple, List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
//        double newValue = 0;
        List<ThreeAddressCode> source = new ArrayList<>();
        expandedQuadruple.setLevel(0);
        source.add(new ThreeAddressCode("T1", Arrays.asList(expandedQuadruple)));

        if (expandedQuadruple.isPlusOrMinus()) {
            PolynomialRuleSumNumbers sum = new PolynomialRuleSumNumbers();
            List<Step> parenthesesSum = sum.handle(source);

            String result = parenthesesSum.get(0).getSource().get(0).getLeft();

            ExpandedQuadruple aux = sources.get(0).findQuadrupleByArgument1(expandedQuadruple.getResult());

            if (aux == null) {
                aux = sources.get(0).findQuadrupleByArgument2(expandedQuadruple.getResult());
                aux.setArgument2(result);
            } else {
                aux.setArgument1(result);
            }

            source.get(0).setExpandedQuadruples(Arrays.asList(aux));

            parenthesesSum.get(parenthesesSum.size() - 1).setSource(source);
            parenthesesSum.get(parenthesesSum.size() - 1).setLatexExpression(source.get(0).toLaTeXNotation().trim());
            parenthesesSum.get(parenthesesSum.size() - 1).setMathExpression(source.get(0).toMathNotation().trim());
            steps.add(parenthesesSum.get(parenthesesSum.size() - 1));
        } else if (expandedQuadruple.isTimes()) {


        } else if (expandedQuadruple.isPotentiation()) {


        }


//
//
//        if (nextQuadruple.getArgument1().equals(expandedQuadruple.getResult())) {
//            nextQuadruple.setArgument1(String.valueOf(newValue));
//        }
//
//        if (nextQuadruple.getArgument2().equals(expandedQuadruple.getResult())) {
//            nextQuadruple.setArgument2(String.valueOf(newValue));
//        }

    }


    /**
     * Realiza a soma entre todos os numeros, de maneira recursiva.
     *
     * @param threeAddressCode     {@link ThreeAddressCode} que representa o polinomio.
     * @param param                {@link String} que pode representar uma temporaria ou um numero em si.
     * @param lastOperationIsMinus {@link Boolean} que identifica se a operacao anterior era uma subtracao.
     * @return a soma total dos numeros, como um {@link Double}.
     */
    private double sumTerms(ThreeAddressCode threeAddressCode, String param, boolean lastOperationIsMinus) {
        double sum = 0;

        /**
         * verifica se entre a operacao ha uma variavel temporaria, e abre a mesma, para poder obter os numeros contidos.
         * caso seja a soma entre numeros, entrara no else.
         */

        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(param);

            if (expandedQuadruple.isNegative()) {
                sum -= sumTerms(threeAddressCode, expandedQuadruple.getArgument1(), false);
            } else {
                sum += sumTerms(threeAddressCode, expandedQuadruple.getArgument1(), lastOperationIsMinus);
                sum += sumTerms(threeAddressCode, expandedQuadruple.getArgument2(), expandedQuadruple.isMinus());
            }
        } else {
            /**
             * entra no if caso seja uma subtracao, e no else caso seja uma soma
             */
            if (lastOperationIsMinus)
                sum -= Double.parseDouble(param.replace(",", "."));
            else
                sum += Double.parseDouble(param.replace(",", "."));
        }

        return sum;
    }

    /**
     * Realiza efetivamente as multiplicacoes dos termos.
     *
     * @param source Lista de codigos de tres endereços que foi gerado pelas etapaas iniciais do compilador.
     * @param times  Lista de quadruplas expandidas que possuem a operacação de multiplicação.
     */
    private double multiply(List<ThreeAddressCode> source, ExpandedQuadruple times) {
        String a, b;
        a = findFactor(source, times.getArgument1());
        b = findFactor(source, times.getArgument2());

        if (!StringUtil.isEmpty(a) || !StringUtil.isEmpty(b)) {
            double result = Double.parseDouble(a) * Double.parseDouble(b);

            /**
             * Realiza a verificação se o numero é negativo, caso seja,transforma o mesmo em positivo e atribui o operador MINUS a quadrupla do mesmo.
             */
            if (result < 0) {
                result *= -1;
                times.setOperator("MINUS");
            } else
                times.setOperator("");

            /**
             * Verifica se o numero é um inteiro ou um double, e realiza o cast do resultado para o formato adequado.
             */
            if (result % 1 == 0)
                times.setArgument1(String.valueOf((int) result));
            else
                times.setArgument1(String.valueOf(result));

            times.setArgument2("");
        }

        return Double.parseDouble(times.getArgument1());
    }

    /**
     * Realiza a operaçao de potencia nas quadruplas que possuem a operaçao
     *
     * @param source Lista de codigos de tres endereços que foi gerado pelas etapaas iniciais do compilador.
     * @param powers Lista de quadruplas expandidas que possuem a operacação de exponenciacao.
     */
    private double power(List<ThreeAddressCode> source, ExpandedQuadruple powers) {
        String a, b;
        a = findFactor(source, powers.getArgument1());
        b = findFactor(source, powers.getArgument2());

        if (!StringUtil.isEmpty(a) || !StringUtil.isEmpty(b)) {

            /**
             * Mascara para limitar as casas decimais em caso de potencia negativa.
             */
            double result = Math.pow(Double.parseDouble(a), Double.parseDouble(b));
            result = NumberUtil.formatDouble(result);
            /**
             * Verifica se o numero é um inteiro ou um double, e realiza o cast do resultado para o formato adequado.
             */
            if (result % 1 == 0)
                powers.setArgument1(String.valueOf((int) result));
            else
                powers.setArgument1(String.valueOf(result));
            powers.setOperator("");
            powers.setArgument2("");
        }

        return Double.parseDouble(powers.getArgument1());
    }

    /**
     * Encontra os fatores a e b para a multiplicacao, conforme a expressao: a * b, para cada uma
     * das quadruplas que tem alguma operação de multiplicacao.
     *
     * @param source {@link ThreeAddressCode} que contem todas as quadruplas.
     * @param factor Fator a ser verificado e encontrado dentro das quadruplas(a ou b).
     * @return {@link String} que contem o valor numérico do fator.
     */
    private String findFactor(List<ThreeAddressCode> source, String factor) {
        if (StringUtil.match(factor, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return findInnerFactor(source, factor);
        }
        return factor;
    }

    /**
     * Encontra o valor  dentro das variaveis temporarias que esta envolvida
     * em uma operacao de multiplicaçao e verifica caso o mesmo seja negativo, caso seja, adiciona o "-".
     *
     * @param source  Lista de codigos de tres endereços que foi gerado pelas etapaas iniciais do compilador.
     * @param tempVar Variavel provida pela quadrupla expandida que esta sendo processada no método {@link #multiply}.
     * @return Retorna o valor da quadrupla que foi acessada atraves do parametro tempVar, em caso de a quadrupla ser negativa (MINUS), retorna o valor concatenado com "-".
     */
    private String findInnerFactor(List<ThreeAddressCode> source, String tempVar) {
        ExpandedQuadruple innerQuadruple = source.get(0).findQuadrupleByResult(tempVar);
        if (!innerQuadruple.getOperator().equals("") && innerQuadruple.isNegative()) {
            expandedQuadruples.remove(innerQuadruple);
            return "-" + innerQuadruple.getArgument1();
        }
        return innerQuadruple.getArgument1();
    }

    private boolean isThereOperationsBetweenParentheses(List<ExpandedQuadruple> expandedQuadruples) {
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.getLevel() != 0) {
                return true;
            }
        }
        return false;
    }
}
