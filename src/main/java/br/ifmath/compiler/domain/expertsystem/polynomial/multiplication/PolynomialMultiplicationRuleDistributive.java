package br.ifmath.compiler.domain.expertsystem.polynomial.multiplication;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PolynomialMultiplicationRuleDistributive implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> sources) {
        return isThereADistributiveCase(sources);
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) {
        source = sources.get(0);
        ExpandedQuadruple distributiveQuadruple = this.source.findQuadrupleByResult(this.source.getLeft());

        String reason = this.applyDistributive(distributiveQuadruple);
        this.fixingQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        List<Step> steps = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), reason));

        return steps;
    }

    /**
     * A partir da quadrupla que representa a junção dos dois polinomios dados pelo usuario, identifica caso
     * seja uma distributiva de um valor para um polinomio (Ex.: 4x * (2x^2 + 2) ), ou entre dois polinomios
     * (Ex.: (2x^3 - 3x) * (x^2 + 5x) )
     *
     * @param distributiveQuadruple {@link ExpandedQuadruple} que contém a junção dos dois polinomios
     * @return o {@code reason} dessa regra, ou seja, a explicação adequada do que foi realizado
     */
    private String applyDistributive(ExpandedQuadruple distributiveQuadruple) {
        if (StringUtil.match(distributiveQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()) &&
                StringUtil.match(distributiveQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {

            ExpandedQuadruple leftQuadruple = this.source.findQuadrupleByResult(distributiveQuadruple.getArgument1());
            ExpandedQuadruple rightQuadruple = this.source.findQuadrupleByResult(distributiveQuadruple.getArgument2());
            /*caso sejam duas variaveis temporarias nos argumentos 1 e 2, sera uma distribuição entre polinomios,
            com excecao aos dois casos nos ifs abaixo
             */

            if (leftQuadruple.isNegative()) {
                return this.applyMonomialDistributive(leftQuadruple.getResult(), rightQuadruple);
            }

            if (rightQuadruple.isNegative()) {
                return this.applyMonomialDistributive(rightQuadruple.getResult(), leftQuadruple);
            }

            ThreeAddressCode newSource = this.createNewSource(this.getQuadruplesWithMinus());
            return polynomialDistributive(leftQuadruple, rightQuadruple, newSource);

        }

        /* caso não sejam duas variaveis temporarias, entao significa uma distributiva entre
        um polinomio e um valor, que pode ser o argument 1 ou 2
         */
        if (!StringUtil.match(distributiveQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            return this.applyMonomialDistributive(distributiveQuadruple.getArgument1(), this.source.findQuadrupleByResult(distributiveQuadruple.getArgument2()));

        return this.applyMonomialDistributive(distributiveQuadruple.getArgument2(), this.source.findQuadrupleByResult(distributiveQuadruple.getArgument1()));

    }

    /**
     * cria um novo {@link ThreeAddressCode} contendo uma lista de quadruplas
     *
     * @param oldList {@link ArrayList} de {@link ExpandedQuadruple} que serão inseridos no novo
     *                {@link ThreeAddressCode}
     * @return novo {@link ThreeAddressCode} gerado
     */
    private ThreeAddressCode createNewSource(List<ExpandedQuadruple> oldList) {
        List<ExpandedQuadruple> newQuadrupleList = new ArrayList<>();
        //quadrupla inicial
        newQuadrupleList.add(new ExpandedQuadruple("", "", "", "T1", 0, 0));
        newQuadrupleList.addAll(oldList);
        return new ThreeAddressCode("T1", newQuadrupleList);
    }


    /**
     * Obtem todas as {@link ExpandedQuadruple} que sejam negativas da lista de quadruplas, ou seja,
     * todas as quadruplas com operador "MINUS" e "-"
     *
     * @return {@link List} de {@link ExpandedQuadruple} com os valores negativos
     */
    private List<ExpandedQuadruple> getQuadruplesWithMinus() {
        List<ExpandedQuadruple> minusList = new ArrayList<>();
        //essa variavel representa o numero da variavel temporaria (2 presente em T2, 3 em T3, etc.)
        int tvIndex = 2;
        for (ExpandedQuadruple expandedQuadruple : this.source.getExpandedQuadruples()) {
            if (expandedQuadruple.isMinusOrNegative()) {
                String argument1;
                if (expandedQuadruple.isMinus()) {

                    if (StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                        ExpandedQuadruple negativeQuadruple = this.source.findQuadrupleByResult(expandedQuadruple.getArgument2());
                        argument1 = negativeQuadruple.getArgument1();
                    } else
                        argument1 = expandedQuadruple.getArgument2();

                } else
                    argument1 = expandedQuadruple.getArgument1();

                ExpandedQuadruple newQuadruple = new ExpandedQuadruple("MINUS", argument1, "T" + tvIndex, expandedQuadruple.getPosition(), expandedQuadruple.getLevel());
                minusList.add(newQuadruple);
                tvIndex++;
            }
        }
        return minusList;
    }


    /**
     * Ajusta a quadrupla inicial para fazer a distributiva entre um monomio e um polinomio
     *
     * @param multiplier            valor que representa o monomio
     * @param distributiveQuadruple {@link ExpandedQuadruple} que representa o polinomio
     * @return {@link String} que representa a {@code reason}, ou seja, a explicação dessa regra
     */
    private String applyMonomialDistributive(String multiplier, ExpandedQuadruple distributiveQuadruple) {
        this.source.setLeft(distributiveQuadruple.getResult());
        return monomialDistributive(multiplier, distributiveQuadruple);
    }

    /**
     * Faz a distributiva entre um monomio ({@code multiplier}) e um polinomio ({@code distributiveQuadruple})
     *
     * @param multiplier            valor que representa o monomio
     * @param distributiveQuadruple {@link ExpandedQuadruple} que representa o polinomio
     * @return {@link String} que representa a {@code reason}, ou seja, a explicação dessa regra
     */
    private String monomialDistributive(String multiplier, ExpandedQuadruple distributiveQuadruple) {

        if (!distributiveQuadruple.isNegative()) {
            if (StringUtil.match(distributiveQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
                return this.monomialDistributive(multiplier, this.source.findQuadrupleByResult(distributiveQuadruple.getArgument1()));

            this.source.addQuadrupleToList("*", multiplier, distributiveQuadruple.getArgument1(), distributiveQuadruple, true);
        } else {
            ExpandedQuadruple father = this.source.findQuadrupleByArgument(distributiveQuadruple.getResult());
            this.source.addQuadrupleToList("*", multiplier, distributiveQuadruple.getResult(), father, true);

            //para continuar a iteracao, e evitar analisar o argument2 de uma quadrupla negativa
            distributiveQuadruple = father;
        }

        if (StringUtil.match(distributiveQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple nextQuadruple = this.source.findQuadrupleByResult(distributiveQuadruple.getArgument2());

            //caso for uma quadrupla com operador "-", transforma em uma nova quadrupla com operador "MINUS"
            if (distributiveQuadruple.isMinus())
                this.source.addQuadrupleToList("MINUS", nextQuadruple.getArgument1(), "", nextQuadruple, true);

            return this.monomialDistributive(multiplier, this.source.findQuadrupleByResult(distributiveQuadruple.getArgument2()));
        }

        if (distributiveQuadruple.isMinus())
            this.source.addQuadrupleToList("MINUS", distributiveQuadruple.getArgument2(), "", distributiveQuadruple, false);

        this.source.addQuadrupleToList("*", multiplier, distributiveQuadruple.getArgument2(), distributiveQuadruple, false);
        this.adjustMonomialQuadruples();

        return "Aplicando a propriedade distributiva, onde cada elemento dentro dos parênteses é multiplicado " +
                "pelo elemento do outro termo.";
    }

    /**
     * Ajusta os niveis e operadores das quadruplas negativas
     */
    private void adjustMonomialQuadruples() {
        for (ExpandedQuadruple expandedQuadruple : this.source.getExpandedQuadruples()) {
            if (expandedQuadruple.isNegative())
                expandedQuadruple.setLevel(1);
            if (expandedQuadruple.isMinus())
                expandedQuadruple.setOperator("+");
        }
    }

    /**
     * Realiza a iteracao do polinomio a esquerda da multiplicacao
     *
     * @param leftDistQuadruple  {@link ExpandedQuadruple} que representa o polinomio a esquerda da multiplicacao
     * @param rightDistQuadruple {@link ExpandedQuadruple} que representa o polinomio a direita da multiplicacao
     * @param newSource          {@link ThreeAddressCode} que será inserido as quadruplas com o resultado da distributiva
     * @return {@link String} que representa a {@code reason} dessa regra, ou seja, a explicação adequada do que foi realizado
     */
    private String polynomialDistributive(ExpandedQuadruple leftDistQuadruple, ExpandedQuadruple rightDistQuadruple, ThreeAddressCode newSource) {
        if (StringUtil.match(leftDistQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            return this.polynomialDistributive(this.source.findQuadrupleByResult(leftDistQuadruple.getArgument1()), rightDistQuadruple, newSource);

        leftDistQuadruple = handleMinusOnArgument1(leftDistQuadruple, rightDistQuadruple, newSource);

        if (StringUtil.match(leftDistQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple nextQuadruple = this.getNextQuadruple(leftDistQuadruple, newSource);
            return this.polynomialDistributive(nextQuadruple, rightDistQuadruple, newSource);
        }
        String leftQuadrupleMultiplier = handleNegativeOnQuadruple(leftDistQuadruple, newSource);

        /*pelo modo como as quadruplas sao inseriadas no newSource, é preciso ajustar a última quadrupla
        para não ficar um valor vazio
         */
        this.adjustLastQuadruple(this.polynomialRightTermDistributive(leftQuadrupleMultiplier, rightDistQuadruple, newSource));
        this.source = newSource;

        return "Aplicando a propriedade distributiva, onde cada elemento do primeiro termo é multiplicado " +
                "por cada um dos elementos do segundo termo.";
    }

    /**
     * Realiza a efetiva distributiva de cada elemento do polinomio a esquerda, com o polinomio a direita
     *
     * @param leftQuadrupleMultiplier {@link String} que representa um valor no polinomio a esquerda
     * @param rightQuadruple          {@link ExpandedQuadruple} que representa o polinomio a direita da multiplicacao
     * @param newSource               {@link ThreeAddressCode} que será inserido as quadruplas com o resultado da distributiva
     * @return {@link ThreeAddressCode} que contem as {@link ExpandedQuadruple} apos a realizacao da distributiva
     */
    private ThreeAddressCode polynomialRightTermDistributive(String leftQuadrupleMultiplier, ExpandedQuadruple rightQuadruple, ThreeAddressCode newSource) {
        if (StringUtil.match(rightQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            return this.polynomialRightTermDistributive(leftQuadrupleMultiplier, this.source.findQuadrupleByResult(rightQuadruple.getArgument1()), newSource);

        rightQuadruple = this.handleMinusOnArgument1(leftQuadrupleMultiplier, rightQuadruple, newSource);

        if (StringUtil.match(rightQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {

            ExpandedQuadruple nextQuadruple = this.getNextQuadruple(rightQuadruple, newSource);
            return this.polynomialRightTermDistributive(leftQuadrupleMultiplier, nextQuadruple, newSource);
        }
        String rightQuadrupleArgument = handleNegativeOnQuadruple(rightQuadruple, newSource);
        this.addNewQuadrupleToSource(leftQuadrupleMultiplier, rightQuadrupleArgument, newSource);
        return newSource;
    }

    /**
     * A partir de um {@code argument}, procura na lista de quadruplas ( {@code expandedQuadruples} por um valor igual.
     *
     * @param argument           {@link String} que é o valor a ser procurado na lista
     * @param expandedQuadruples {@link List} de {@link ExpandedQuadruple} que contem a quadrupla a ser buscada
     * @param isTopToBottom      define se eh uma busca ascendente ou descendente na lista
     * @return {@link ExpandedQuadruple} com o mesmo valor do {@code argument}
     */
    private ExpandedQuadruple getMinusQuadruple(String argument, List<ExpandedQuadruple> expandedQuadruples, boolean isTopToBottom) {
        //descendente
        if (isTopToBottom) {
            for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
                if (expandedQuadruple.isNegative() && expandedQuadruple.getArgument1().equals(argument))
                    return expandedQuadruple;
            }
        }

        //ascendente
        for (int i = expandedQuadruples.size() - 1; i > 0; i--) {
            ExpandedQuadruple expandedQuadruple = expandedQuadruples.get(i);
            if (expandedQuadruple.isNegative() && expandedQuadruple.getArgument1().equals(argument))
                return expandedQuadruple;
        }
        return expandedQuadruples.get(0);
    }

    /**
     * Cria uma quadrupla a partir de dois valores, e insere na lista de quadruplas de um codigo de tres enderecos
     *
     * @param leftQuadrupleMultiplier {@link String} que ira se tornar o {@code argument1} da nova {@link ExpandedQuadruple}
     * @param rightQuadrupleArgument  {@link String} que ira se tornar o {@code argument2} da nova {@link ExpandedQuadruple}
     * @param newSource               {@link ThreeAddressCode} que sera inserido a nova quadrupla em sua lista de quadruplas
     */
    private void addNewQuadrupleToSource(String leftQuadrupleMultiplier, String rightQuadrupleArgument, ThreeAddressCode newSource) {
        ExpandedQuadruple iterationQuadruple = this.findIterationQuadruple(newSource);
        newSource.addQuadrupleToList("*", leftQuadrupleMultiplier, rightQuadrupleArgument, iterationQuadruple, true);
        newSource.addQuadrupleToList("", "", "", iterationQuadruple, false);
        iterationQuadruple.setOperator("+");
    }

    /**
     * Encontra a quadrupla onde serao inseridos os novos valores
     *
     * @param newSource {@link ThreeAddressCode} que contem a {@link List} de {@link ExpandedQuadruple}
     *                  de qual sera feita a busca
     * @return {@link ExpandedQuadruple} que sera alterada e adicionado novos valores
     */
    private ExpandedQuadruple findIterationQuadruple(ThreeAddressCode newSource) {
        for (ExpandedQuadruple expandedQuadruple : newSource.getExpandedQuadruples()) {

            if (expandedQuadruple.getArgument1().equals("") &&
                    expandedQuadruple.getArgument2().equals("") && !expandedQuadruple.isNegative())
                return expandedQuadruple;
        }
        return newSource.getExpandedQuadruples().get(0);
    }

    /**
     * Faz a verificacao se o {@code argument1} de uma quadrupla tem operador "MINUS" ou nao, e realiza a operacao correta. Nesse caso, ira
     * analisar a {@code leftQuadruple} e ira realizar a iteracao para inserir a lista do {@code source} de acordo com a verificacao.
     *
     * @param leftQuadruple  {@link ExpandedQuadruple} que sera analisada e que ira definir qual sera o multiplicador
     *                       para a insercao na lista de quadruplas
     * @param rightQuadruple {@link ExpandedQuadruple} que representa a quadrupla inicial, de que pode se obter
     *                       o polinomio a direita da multiplicacao
     * @param source         {@link ThreeAddressCode} de onde sera obtida a {@link List} de {@link ExpandedQuadruple}
     *                       a serem iteradas
     * @return o {@code leftQuadruple} com o valor correto para continuar a iteracao
     */
    private ExpandedQuadruple handleMinusOnArgument1(ExpandedQuadruple leftQuadruple, ExpandedQuadruple rightQuadruple, ThreeAddressCode source) {
        if (leftQuadruple.isNegative()) {
            ExpandedQuadruple minusQuadruple = this.getMinusQuadruple(leftQuadruple.getArgument1(), source.getExpandedQuadruples(), true);
            this.polynomialRightTermDistributive(minusQuadruple.getResult(), rightQuadruple, source);
            return this.source.findQuadrupleByArgument(leftQuadruple.getResult());
        }
        this.polynomialRightTermDistributive(leftQuadruple.getArgument1(), rightQuadruple, source);
        return leftQuadruple;
    }

    /**
     * Faz a verificacao se o {@code argument1} de uma quadrupla tem operador "MINUS" ou nao, e realiza a operacao correta. Nesse caso, ira
     * analisar a {@code rightQuadruple} e inserir uma quadrupla a lista do {@code source} de acordo com a verificacao.
     *
     * @param leftQuadrupleMultiplier {@link String} que representa o multiplicador atual, ou seja, o valor
     *                                que ira se tornar o {@code argument1} da {@link ExpandedQuadruple}
     * @param rightQuadruple          {@link ExpandedQuadruple} que sera verificada e associada a {@code leftQuadrupleMultiplier}
     *                                para ser inserida na lista de quadruplas
     * @param source                  {@link ThreeAddressCode} que contem a {@link List} de {@link ExpandedQuadruple}
     *                                de qual sera inserida a nova quadrupla
     * @return o {@code rightQuadruple} com o valor correto para continuar a iteracao
     */
    private ExpandedQuadruple handleMinusOnArgument1(String leftQuadrupleMultiplier, ExpandedQuadruple rightQuadruple, ThreeAddressCode source) {
        if (rightQuadruple.isNegative()) {
            //insere a quadrupla com MINUS na lista, e depois pega seu pai, para continuar a iteracao com seu argument2
            ExpandedQuadruple minusQuadruple = this.getMinusQuadruple(rightQuadruple.getArgument1(), source.getExpandedQuadruples(), false);
            this.addNewQuadrupleToSource(leftQuadrupleMultiplier, minusQuadruple.getResult(), source);
            return this.source.findQuadrupleByArgument(rightQuadruple.getResult());
        }
        this.addNewQuadrupleToSource(leftQuadrupleMultiplier, rightQuadruple.getArgument1(), source);
        return rightQuadruple;
    }


    /**
     * Faz a verificacao se uma quadrupla tem operador "-" ou nao, e retorna o valor correto que o representa
     *
     * @param expandedQuadruple {@link ExpandedQuadruple} a ser analisada e obtida o valor negativo correto
     * @param source            {@link ThreeAddressCode} que contem a {@link List} de {@link ExpandedQuadruple} a auxiliar
     *                          para obter o valor correto
     * @return {@link String} que representa o valor numerico ou a quadrupla associada ao {@code argument2} da quadrupla
     */
    private String handleNegativeOnQuadruple(ExpandedQuadruple expandedQuadruple, ThreeAddressCode source) {
        if (expandedQuadruple.isMinus()) {
            ExpandedQuadruple minusQuadruple = this.getMinusQuadruple(expandedQuadruple.getArgument2(), source.getExpandedQuadruples(), true);
            return minusQuadruple.getResult();
        }
        return expandedQuadruple.getArgument2();
    }

    /**
     * Obtem a quadrupla correta, a partir do {@code argument2} que representa uma variavel temporaria
     *
     * @param expandedQuadruple {@link ExpandedQuadruple} de onde sera obtido e verificado o {@code argument2}
     * @param source            @link ThreeAddressCode} que contem a {@link List} de {@link ExpandedQuadruple} a auxiliar
     *                          para obter o valor correto
     * @return proxima {@link ExpandedQuadruple} a ser iterada ou alterada
     */
    private ExpandedQuadruple getNextQuadruple(ExpandedQuadruple expandedQuadruple, ThreeAddressCode source) {
        ExpandedQuadruple nextQuadruple = this.source.findQuadrupleByResult(expandedQuadruple.getArgument2());

        if (expandedQuadruple.isMinus() && !StringUtil.match(nextQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple minusQuadruple = this.getMinusQuadruple(nextQuadruple.getArgument1(), source.getExpandedQuadruples(), false);
            this.source.addQuadrupleToList(minusQuadruple.getOperator(), minusQuadruple.getArgument1(), minusQuadruple.getArgument2(), nextQuadruple, true);
        }
        return nextQuadruple;
    }

    /**
     * Ajusta a ultima quadrupla, para que nao apresente um valor vazio ou ate mesmo uma quadrupla vazia
     *
     * @param newSource {@link ThreeAddressCode} que contem a {@link List} de {@link ExpandedQuadruple} a ser analisada
     *                  e ajustada
     */
    private void adjustLastQuadruple(ThreeAddressCode newSource) {
        ExpandedQuadruple lastQuadruple = newSource.getExpandedQuadruples().get(newSource.getExpandedQuadruples().size() - 1);
        if (lastQuadruple.getOperator().equals("")) {
            ExpandedQuadruple father = newSource.findQuadrupleByArgument2(lastQuadruple.getResult());
            newSource.replaceFatherArgumentForSons(father, 1);
        }
    }


    /**
     * Limpa as quadruplas nao utilizadas e remove os parenteses das quadruplas
     */
    private void fixingQuadruples() {
        this.source.clearNonUsedQuadruples();
        this.source.removeQuadruplesParentheses(true);
    }

    /**
     * Verifica se eh necessario realizar uma distributiva em dado {@code source}
     *
     * @param source {@link ThreeAddressCode} que contem a {@link List} de {@link ExpandedQuadruple}
     *               a serem analisadas
     * @return {@code true} caso seja necessario executar uma distributiva, e {@code false} caso contrario.
     */
    private boolean isThereADistributiveCase(List<ThreeAddressCode> source) {
        ExpandedQuadruple expandedQuadruple = source.get(0).findQuadrupleByResult(source.get(0).getLeft());
        //Se o argument1 for uma temporaria, e o argument2 nao
        if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()) && !StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple innerOperation = source.get(0).findQuadrupleByResult(expandedQuadruple.getArgument1());
            if (!innerOperation.isNegative())
                return true;
        }

        //Se o argument1 nao for uma temporaria, e o argument2 sim
        if (!StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()) && StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple innerOperation = source.get(0).findQuadrupleByResult(expandedQuadruple.getArgument2());

            if (!innerOperation.isNegative())
                return true;
        }

        //Se ambos os arguments sao temporarias
        if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()) && StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple innerOperation1 = source.get(0).findQuadrupleByResult(expandedQuadruple.getArgument1());
            ExpandedQuadruple innerOperation2 = source.get(0).findQuadrupleByResult(expandedQuadruple.getArgument2());

            return !innerOperation1.isNegative() || !innerOperation2.isNegative();
        }
        return false;
    }
}
