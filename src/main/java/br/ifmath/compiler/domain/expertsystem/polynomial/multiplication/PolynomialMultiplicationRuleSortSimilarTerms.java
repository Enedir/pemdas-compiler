package br.ifmath.compiler.domain.expertsystem.polynomial.multiplication;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PolynomialMultiplicationRuleSortSimilarTerms implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return isThereTermsToSort(source.get(0), source.get(0).findQuadrupleByResult(source.get(0).getLeft()), -1);
    }


    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        this.source = source.get(0);

        /* Bubble sort, mas como nao se pode iterar pelo tamanho da lista, eh feito um laço verificando se
          ha necessidade de ordenar, a cada iteracao */
        boolean hasElementsToSort = true;
        while (hasElementsToSort) {
            if (isThereTermsToSort(source.get(0), source.get(0).findQuadrupleByResult(source.get(0).getLeft()), -1))
                sortQuadruples(this.source.findQuadrupleByResult(this.source.getLeft()), -1);
            else
                hasElementsToSort = false;
        }
        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        List<Step> steps = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Agrupando os termos semelhantes."));

        return steps;
    }

    /**
     * Realiza a ordenacao das quadruplas, a partir das potencias dos monomios
     *
     * @param iterationQuadruple {@link ExpandedQuadruple} que representa a raiz da arvore de quadruplas
     *                           a ser organizada
     * @param lastPower          {@link Integer} que representa a maior potencia da iteracao anterior. Caso for a
     *                           primeira iteracao, o valor sera -1.
     * @return se terminou a organizacao ou nao
     */
    private boolean sortQuadruples(ExpandedQuadruple iterationQuadruple, int lastPower) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            return sortQuadruples(this.source.findQuadrupleByResult(iterationQuadruple.getArgument1()), lastPower);

        //obtem o valor da potencia
        int power = StringUtil.getPowerValue(iterationQuadruple.getArgument1());

        ExpandedQuadruple sortQuadruple = iterationQuadruple;
        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());

        /*Essa variavel indica se ira analisar o argument2 da quadrupla atual (linha abaixo) ou
        uma variavel temporaria que ira ser iterada (if)*/
        String iterableValue = iterationQuadruple.getResult();
        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            iterableValue = iterationQuadruple.getArgument2();

        //obtem a maior potencia dos monomios possivel
        lastPower = swapArgumentWithHigherPower(sortQuadruple, power, iterableValue);

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            return sortQuadruples(this.source.findQuadrupleByResult(iterationQuadruple.getArgument2()), lastPower);

        return true;
    }

    /**
     * Verifica e itera as quadruplas para realizar a troca dos monomios.
     *
     * @param originalQuadruple {@link ExpandedQuadruple} que contem o {@code argument1} a ser comparado
     *                          as outras quadruplas para fazer a troca
     * @param lastPower         {@link Integer} que representa a maior potencia da iteracao anterior. Caso for a
     *                          primeira iteracao, o valor sera -1.
     * @param iterationValue    {@link String} que representa o argument2 da {@code originalQuadruple} ou
     *                          a quadrupla da iteracao
     * @return {@link Integer} que representa o maior valor da potencia atualmente
     */
    private int swapArgumentWithHigherPower(ExpandedQuadruple originalQuadruple, int lastPower, String iterationValue) {
        ExpandedQuadruple expandedQuadruple = this.source.findQuadrupleByResult(iterationValue);

        //se estiver analizando o argument1 e o argument2 de uma mesma quadrupla
        if (originalQuadruple.equals(expandedQuadruple) && StringUtil.isVariable(expandedQuadruple.getArgument2()))
            return swapLastArgument(originalQuadruple, lastPower, expandedQuadruple, true);


        int power = swapLastArgument(originalQuadruple, lastPower, expandedQuadruple, false);
        //caso houve uma mudança nos monomios
        if (power != lastPower)
            return power;

        //caso o valor da potencia da originalQuadruple for menor que a expandedQuadruple, continua iterando
        if (StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            return swapArgumentWithHigherPower(originalQuadruple, lastPower, expandedQuadruple.getArgument2());

        return swapLastArgument(originalQuadruple, lastPower, expandedQuadruple, true);
    }

    /**
     * Efetivamente realiza a troca dos sinais e dos proprios monimos, para ordena-los de acordo com a potencia
     * maior
     *
     * @param originalQuadruple  {@link ExpandedQuadruple} que contem o {@code argument1} a ser comparado
     *                           as outras quadruplas para fazer a troca
     * @param lastPower          {@link Integer} que representa a maior potencia da iteracao anterior. Caso for a
     *                           primeira iteracao, o valor sera -1.
     * @param iterationQuadruple {@link ExpandedQuadruple} que representa a quadrupla da iteracao
     * @param isLastQuadruple    {@link Boolean} que indica se esta verificando a ultima quadrupla ou nao
     * @return {@link Integer} com o valor da potencia apos a troca ser realizada ou nao
     */
    private int swapLastArgument(ExpandedQuadruple originalQuadruple, int lastPower, ExpandedQuadruple iterationQuadruple, boolean isLastQuadruple) {
        String param = (isLastQuadruple) ? iterationQuadruple.getArgument2() : iterationQuadruple.getArgument1();
        int power = StringUtil.getPowerValue(param);
        if (power > lastPower) {
            //faz a troca dos monomios
            String argument1 = originalQuadruple.getArgument1();
            originalQuadruple.setArgument1(param);
            if (isLastQuadruple)
                iterationQuadruple.setArgument2(argument1);
            else
                iterationQuadruple.setArgument1(argument1);

            //troca os sinais respectivos dos monomios

            //obtem o sinal da originalQuadruple
            String originalFatherOperator = "+";
            ExpandedQuadruple originalFather = this.source.findDirectFather(originalQuadruple.getResult());

            if (originalQuadruple.isNegative())
                originalFatherOperator = "-";
            else {
                if (originalFather != null) {
                    originalFatherOperator = originalFather.getOperator();
                }
            }

            //obtem o sinal da iterationQuadruple, que caso nao for a ultima quadrupla, obtera o sinal de seu pai
            iterationQuadruple = (!isLastQuadruple) ? this.source.findDirectFather(iterationQuadruple.getResult()) : iterationQuadruple;
            if (!originalFatherOperator.equals(iterationQuadruple.getOperator())) {
                if (originalQuadruple.isNegative())
                    this.source.replaceFatherArgumentForSons(originalQuadruple, 1);
                else {
                    if (originalFather == null)
                        this.source.addQuadrupleToList("MINUS", originalQuadruple.getArgument1(), "", originalQuadruple, true);
                    else
                        originalFather.setOperator(iterationQuadruple.getOperator());
                }
                iterationQuadruple.setOperator(originalFatherOperator);
            }
            return power;
        }
        return lastPower;
    }

    /**
     * Verifica se eh necessario organizar as quadruplas, de acordo com a potencia dos monomios
     *
     * @param source             {@link ThreeAddressCode} que contem a {@link List} de {@link ExpandedQuadruple}
     *                           a serem analisadas
     * @param iterationQuadruple {@link ExpandedQuadruple} que representa a raiz das quadruplas
     * @param lastPower          {@link Integer} que representa a maior potencia da iteracao anterior. Caso for a
     *                           primeira iteracao, o valor sera -1.
     * @return {@link Boolean} se as quadruplas estao organizadas ou nao
     */
    private boolean isThereTermsToSort(ThreeAddressCode source, ExpandedQuadruple iterationQuadruple, int lastPower) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            return isThereTermsToSort(source, source.findQuadrupleByResult(iterationQuadruple.getArgument1()), lastPower);

        int power = StringUtil.getPowerValue(iterationQuadruple.getArgument1());
        if (power <= lastPower || lastPower == -1)
            lastPower = power;
        else
            return true;

        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());

        //caso só tenha uma quadrupla, com um único valor e MINUS
        if (iterationQuadruple != null) {

            //caso só tenha uma quadrupla, com um único valor
            if (!iterationQuadruple.getArgument2().equals("") &&
                    StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString()))
                return isThereTermsToSort(source, source.findQuadrupleByResult(iterationQuadruple.getArgument2()), lastPower);

            return StringUtil.getPowerValue(iterationQuadruple.getArgument2()) > lastPower;
        }
        return false;
    }
}
