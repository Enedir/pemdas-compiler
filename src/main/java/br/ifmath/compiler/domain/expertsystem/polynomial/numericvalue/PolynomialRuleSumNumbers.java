package br.ifmath.compiler.domain.expertsystem.polynomial.numericvalue;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.NumberUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PolynomialRuleSumNumbers implements IRule {

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return isThereNumbersToSum(source.get(0).getExpandedQuadruples());
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) {
        List<Step> steps = new ArrayList<>();

        double result = sumTerms(sources.get(0), sources.get(0).getLeft(), false);

        String left;
        /*
         * Verifica se o numero é um inteiro ou um double, e realiza o cast do resultado para o formato adequado.
         */
        if (result % 1 == 0)
            left = String.valueOf(NumberUtil.parseInt(result));
        else
            left = String.valueOf(result);

        List<ExpandedQuadruple> expandedQuadruples = sources.get(0).getExpandedQuadruples();

        ThreeAddressCode step = new ThreeAddressCode(left, expandedQuadruples);
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Somando os valores."));

        return steps;
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

        /*
          verifica se entre a operacao ha uma variavel temporaria, e abre a mesma, para poder obter os numeros contidos.
          caso seja a soma entre numeros, entrara no else.
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
            /*
              entra no if caso seja uma subtracao, e no else caso seja uma soma
             */
            if (lastOperationIsMinus)
                sum -= Double.parseDouble(param.replace(",", "."));
            else
                sum += Double.parseDouble(param.replace(",", "."));
        }

        return sum;
    }


    /**
     * Verifica se ha operacoes de soma e subtracao a serem realizadas.
     *
     * @param expandedQuadruples {@link List} de {@link ExpandedQuadruple}s a serem analisados.
     * @return true se ha numeros a serem somados ou subtrados, e falso caso contrario.
     */
    private boolean isThereNumbersToSum(List<ExpandedQuadruple> expandedQuadruples) {
        int numberAmount = 0;
        int variableAmount = 0;

        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (!expandedQuadruple.isPlusOrMinus() || expandedQuadruple.getLevel() != 0)
                return false;

            if (StringUtil.matchAny(expandedQuadruple.getArgument1(), RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString()))
                numberAmount++;

            if (StringUtil.matchAny(expandedQuadruple.getArgument2(), RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString()))
                numberAmount++;

            if (StringUtil.isVariable(expandedQuadruple.getArgument1()))
                variableAmount++;

            if (StringUtil.isVariable(expandedQuadruple.getArgument2()))
                variableAmount++;

        }

        return (numberAmount > 1 && variableAmount == 0);
    }
}
