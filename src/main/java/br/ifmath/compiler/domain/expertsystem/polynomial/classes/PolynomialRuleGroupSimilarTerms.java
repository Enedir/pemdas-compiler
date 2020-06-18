package br.ifmath.compiler.domain.expertsystem.polynomial.classes;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PolynomialRuleGroupSimilarTerms implements IRule {

    private final List<ExpandedQuadruple> expandedQuadruples;

    public PolynomialRuleGroupSimilarTerms() {
        this.expandedQuadruples = new ArrayList<>();
    }


    @Override
    public boolean match(List<ThreeAddressCode> sources) {
        return isThereEquivalentTermsToJoin(sources.get(0));
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) {
        List<NumericValueVariable> termsAndValuesList = new ArrayList<>();
        expandedQuadruples.clear();
        expandedQuadruples.addAll(sources.get(0).getExpandedQuadruples());

        int numbersSum = sumTerms(sources.get(0), sources.get(0).getLeft(), false, termsAndValuesList);
        sortNVVList(termsAndValuesList);
        ThreeAddressCode step;
        if (termsAndValuesList.isEmpty()) {
            ExpandedQuadruple newQuadruple = new ExpandedQuadruple("", String.valueOf(numbersSum), "", "T1", 0, 0);
            step = new ThreeAddressCode("T1", Collections.singletonList(newQuadruple));
        } else {
            replaceExpandedQuadruples(sources.get(0), termsAndValuesList, numbersSum);
            clearUnusedQuadruple(sources.get(0));
            sources.get(0).setLeft(expandedQuadruples.get(0).getResult());
            step = new ThreeAddressCode(sources.get(0).getLeft(), sources.get(0).getExpandedQuadruples());
        }

        List<ThreeAddressCode> codes = new ArrayList<>();
        List<Step> steps = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Soma dos termos semelhantes."));

        return steps;
    }

    /**
     * organiza e remove as variaveis desnecessarias da {@code termsAndValuesList} levando em consideração os expoentes
     * das variáveis, em ordem decrescente.
     *
     * @param termsAndValuesList {@link List} de {@link NumericValueVariable} a ser organizada
     */
    private void sortNVVList(List<NumericValueVariable> termsAndValuesList) {

        //Removendo as variáveis com valor zero
        List<NumericValueVariable> iterableNVVList = new ArrayList<>(termsAndValuesList);
        for (NumericValueVariable numericValue : iterableNVVList) {
            if (numericValue.getValue() == 0)
                termsAndValuesList.remove(numericValue);
        }

        //Bubble sort
        for (int i = 0; i < termsAndValuesList.size(); i++) {
            for (int j = 0; j < termsAndValuesList.size() - 1; j++) {
                String currentLabel = termsAndValuesList.get(j).getLabel();
                int currentValue = 1;
                // há o caso de haver uma variável sem expoente, somente com o x por exemplo
                if (currentLabel.contains("^"))
                    currentValue = Integer.parseInt(currentLabel.substring(currentLabel.indexOf("^") + 1));
                String nextLabel = termsAndValuesList.get(j + 1).getLabel();
                int nextValue = 1;
                if (nextLabel.contains("^"))
                    nextValue = Integer.parseInt(nextLabel.substring(nextLabel.indexOf("^") + 1));
                if (nextValue > currentValue) {
                    NumericValueVariable aux = termsAndValuesList.remove(j);
                    termsAndValuesList.add(j + 1, aux);
                }
            }
        }
    }

    /**
     * Retira as quadruplas que não estão sendo mais utilizadas
     *
     * @param source {@link ThreeAddressCode} que contém as quadruplas a serem retiradas
     */
    private void clearUnusedQuadruple(ThreeAddressCode source) {
        int size = expandedQuadruples.size() - 1;
        if (size >= 1) {
            source.getExpandedQuadruples().subList(1, size + 1).clear();
        }
    }

    /**
     * Adiciona os termos semelhantes agrupados na lista de quadruplas expandidas presente no {@code source}.
     *
     * @param source             {@link ThreeAddressCode} que será modificado para apresentar os termos agrupados
     * @param termsAndValuesList {@link List} de {@link NumericValueVariable} de onde serão obtidos os termos já
     *                           agrupados
     * @param numbersSum         soma total dos números que não contém variáveis
     */
    private void replaceExpandedQuadruples(ThreeAddressCode source, List<NumericValueVariable> termsAndValuesList, int numbersSum) {
        boolean hasOnlyOneItemOnList = false;
        if (termsAndValuesList.size() == 1)
            hasOnlyOneItemOnList = true;

        ExpandedQuadruple iterationQuadruple = null;
        //A variavel i faz o controle dos arguments da quadrupla, no qual i par é o argument1, e impar é argument 2
        int i = 0;
        //toda vez que uma variável é analisada, ela é retirada da lista
        while (!termsAndValuesList.isEmpty()) {
            iterationQuadruple = (i == 0 || i == 1) ? expandedQuadruples.get(0) : source.findQuadrupleByResult(iterationQuadruple.getArgument2());
            NumericValueVariable iterationNVV = termsAndValuesList.get(0);

            if (iterationNVV.getValue() != 0) {
                //verificação para caso esteja no formato 1x, transformando para x
                String nvvValue = String.valueOf(Math.abs(iterationNVV.getValue()));
                if (nvvValue.equals("1"))
                    nvvValue = "";
                iterationNVV.setLabel(nvvValue + iterationNVV.getLabel());
                //Se o valor for negativo, e for a analise do argument1 (i==0), criará uma quadrupla MINUS
                if (iterationNVV.getValue() < 0) {
                    if (i == 0)
                        source.addQuadrupleToList("MINUS", iterationNVV.getLabel(), "", iterationQuadruple, true);
                        /*Se for a analise do argument2, e a soma dos números sem variável for 0, o valor simplesmente é
                         * substituído no argument2*/
                    else {
                        iterationQuadruple.setOperator("-");
                        if (numbersSum == 0 && termsAndValuesList.size() == 1)
                            iterationQuadruple.setArgument2(iterationNVV.getLabel());
                            /* Já se a soma dos números for diferente de 0, será criada uma nova quadrupla para ser
                             *  adicionado o iterationNVV no argument1, e a soma dos números no argument2*/
                        else
                            source.addQuadrupleToList("+", iterationNVV.getLabel(), "", iterationQuadruple, false);

                    }
                    //Se o valor for positivo
                } else {
                    if (i == 0)
                        iterationQuadruple.setArgument1(iterationNVV.getLabel());
                    else {
                        iterationQuadruple.setOperator("+");

                        if (numbersSum == 0) {
                            /*Caso tiver dois ou mais itens na lista de NVV, criará uma quadrupla nova, caso contrário
                             * somente substituirá no argument2*/
                            if (termsAndValuesList.size() > 1)
                                source.addQuadrupleToList("+", iterationNVV.getLabel(), "", iterationQuadruple, false);
                            else
                                iterationQuadruple.setArgument2(iterationNVV.getLabel());

                        } else
                            source.addQuadrupleToList("+", iterationNVV.getLabel(), "", iterationQuadruple, false);
                    }
                }
            }

            termsAndValuesList.remove(0);
            i++;
        }

        //Quando existe uma soma de números sem variável
        if (numbersSum != 0) {
            ExpandedQuadruple quadruple;

            /*Caso haja uma variável temporária no argument2, abrirá essa temporária, e adicionará
             * a soma desses números no argument2 dessa temporária. Caso contrário somente substituirá
             * no argument2 da quadrupla atual*/
            if (iterationQuadruple != null) {
                if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())
                        && !hasOnlyOneItemOnList)
                    quadruple = source.findQuadrupleByResult(iterationQuadruple.getArgument2());
                else
                    quadruple = iterationQuadruple;

                if (numbersSum < 0)
                    quadruple.setOperator("-");
                else
                    quadruple.setOperator("+");

                quadruple.setArgument2(String.valueOf(Math.abs(numbersSum)));
            }
        } else {
            /*caso só haja um item na lista, e sem uma soma de número sem variável, haverá uma quadrupla
             * com somente o argument1*/
            if (hasOnlyOneItemOnList) {
                iterationQuadruple.setOperator("");
                iterationQuadruple.setArgument2("");
            }
        }

    }

    /**
     * Realiza a soma de todos os termos semelhantes, incluindo números sem e com variáveis.
     *
     * @param threeAddressCode     {@link ThreeAddressCode} que contém as quadruplas e termos a serem analisadas e
     *                             agrupadas
     * @param param                {@link String} que representa o resultado da quadrupla a ser analisada atualmente
     * @param lastOperationIsMinus valor booleano que indidica se a operação anterior tinha um operador negativo
     * @param termsAndValuesList   lista que contém os termos com números em conjunto com variáveis
     * @return A soma dos números que não tem variáveis
     */
    private int sumTerms(ThreeAddressCode threeAddressCode, String param, boolean lastOperationIsMinus, List<
            NumericValueVariable> termsAndValuesList) {
        int sum = 0;
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(param);

            if (expandedQuadruple.isNegative()) {
                sum -= sumTerms(threeAddressCode, expandedQuadruple.getArgument1(), true, termsAndValuesList);
            } else {
                sum += sumTerms(threeAddressCode, expandedQuadruple.getArgument1(), lastOperationIsMinus, termsAndValuesList);
                sum += sumTerms(threeAddressCode, expandedQuadruple.getArgument2(), expandedQuadruple.isMinus(), termsAndValuesList);
            }
        } else {

            //Dentro dessa cadeia de ifs é feita a soma dos numeros com variaveis
            if (StringUtil.isVariable(param)) {
                String paramValue, paramVariable;

                if (StringUtil.match(param, RegexPattern.VARIABLE_AND_COEFICIENT.toString())) {
                    paramValue = param.substring(0, param.indexOf("^") - 1);
                    paramVariable = param.substring(param.indexOf("^") - 1);
                } else {
                    paramValue = StringUtil.removeNonNumericChars(param);
                    paramVariable = StringUtil.removeNumericChars(param);
                }

                /*a variável index é utilizada para guardar o indice da lista que contém uma
                variável que vai ser agrupada */
                int index = 0;

                /*cont é utilizado para nao sobrescrever alguma variável, o qual só irá inserir um novo
                 * elemento, quando estiver no final da lista*/
                int cont = 0;

                /*caso não haja uma determinada variável, com ou sem expoente, dentro da lista termsAndValuesList
                 * é criado um novo item*/
                if (termsAndValuesList.isEmpty()) {
                    termsAndValuesList.add(new NumericValueVariable(paramVariable, 0));
                } else {
                    for (int i = 0; i < termsAndValuesList.size(); i++) {
                        if (!termsAndValuesList.get(i).getLabel().equals(paramVariable)) {
                            cont++;
                            if (cont == termsAndValuesList.size()) {
                                termsAndValuesList.add(new NumericValueVariable(paramVariable, 0));
                            }
                        } else {
                            index = i;
                        }
                    }
                }
                //caso o valor seja, por exemplo, x, então tratará como 1x
                int newValue = (StringUtil.isEmpty(paramValue)) ? 1 : Integer.parseInt(paramValue);
                if (lastOperationIsMinus)
                    termsAndValuesList.get(index).setValue(termsAndValuesList.get(index).getValue() - newValue);
                else
                    termsAndValuesList.get(index).setValue(termsAndValuesList.get(index).getValue() + newValue);
            } else {
                //efetiva soma dos números sem variável
                if (lastOperationIsMinus)
                    sum -= Double.parseDouble(param.replace(",", "."));
                else
                    sum += Double.parseDouble(param.replace(",", "."));
            }
        }
        return sum;
    }

    /**
     * Verifica se há dois ou mais numeros ou numeros com variáveis em uma lista, para serem agrupados.
     *
     * @param source {@link ThreeAddressCode} que contémas {@link ExpandedQuadruple} a serem verificadas
     * @return <ul>
     * <li>true - caso haja valores a serem agrupados</li>
     * <li>false - caso não haja valores a serem agrupados</li>
     * </ul>
     */
    private boolean isThereEquivalentTermsToJoin(ThreeAddressCode source) {
        int index = 1;
        //caso haja somente uma quadrupla com os dois argumentos
        if (source.getExpandedQuadruples().size() == 1)
            index--;

        /**/
        for (int i = 0; i < source.getExpandedQuadruples().size() - index; i++) {
            ExpandedQuadruple expandedQuadruple = source.getExpandedQuadruples().get(i);

            if (!StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                if (isThereAEqualLabel(source, expandedQuadruple, true)) {
                    return true;
                }
            }
            if (!StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                if (!expandedQuadruple.isNegative() && isThereAEqualLabel(source, expandedQuadruple, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * A partir de uma {@code expandedQuadruple} vai obter o label (Ex.: x^2) do argument1 ou 2, e verificar
     * se ha em alguma outra quadrupla da lista, outro label igual.
     *
     * @param source            {@link ThreeAddressCode} que contem todas as quadruplas
     * @param expandedQuadruple {@link ExpandedQuadruple} atual que sera comparada com o resto da lista
     * @param isArgument1       {@link Boolean} que indentifica se esta sendo analisado o argument1 ou 2
     *                          da {@code expandedQuadruple}
     * @return {@code true} caso haja dois labels iguais na lista, ou {@code false} caso contrario
     */
    private boolean isThereAEqualLabel(ThreeAddressCode source, ExpandedQuadruple expandedQuadruple, boolean isArgument1) {
        NumericValueVariable nvvExQuadruple = new NumericValueVariable();
        if (isArgument1)
            nvvExQuadruple.setAttributesFromString(expandedQuadruple.getArgument1());
        else
            nvvExQuadruple.setAttributesFromString(expandedQuadruple.getArgument2());

        for (ExpandedQuadruple argumentQuadruple : source.getExpandedQuadruples()) {
            NumericValueVariable nvvArgQuadruple = new NumericValueVariable();

            /*caso seja o argument1 a ser analisado, nao podera obter a si mesmo para ser analisado. Mesmo
             * caso da verificacao mais a baixo*/
            if (!expandedQuadruple.equals(argumentQuadruple) || !isArgument1) {
                if (!StringUtil.match(argumentQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
                    nvvArgQuadruple.setAttributesFromString(argumentQuadruple.getArgument1());
            }

            //caso haja labels iguais para o argument1
            if (nvvArgQuadruple.getLabel() != null && nvvArgQuadruple.getLabel().equals(nvvExQuadruple.getLabel()))
                return true;

            if (!expandedQuadruple.equals(argumentQuadruple) || isArgument1) {
                if (!argumentQuadruple.isNegative() && !StringUtil.match(argumentQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString()))
                    nvvArgQuadruple.setAttributesFromString(argumentQuadruple.getArgument2());
            }

            //caso haja labels iguais para o argument2
            if (nvvArgQuadruple.getLabel() != null && nvvArgQuadruple.getLabel().equals(nvvExQuadruple.getLabel()))
                return true;

        }

        return false;
    }

}
