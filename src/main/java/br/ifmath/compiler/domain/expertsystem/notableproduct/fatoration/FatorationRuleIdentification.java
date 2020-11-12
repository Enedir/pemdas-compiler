package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.groupment.Couple;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.seconddegreetrinomial.FatorationRuleSecondDegreeTrinomialProduct;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.seconddegreetrinomial.FatorationRuleSecondDegreeTrinomialConvertToDivisionFormula;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.Monomial;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FatorationRuleIdentification implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return true;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);

        String correctExplanation = identify();

        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(),
                "Identificação do tipo de fatoração a partir da equação inicial: " + correctExplanation));
        return steps;
    }

    /**
     * Identifica qual a regra correta a partir da equação inicial.
     *
     * @return {@link String} que contém a explicação sobre a regra identificada.
     * @throws InvalidAlgebraicExpressionException Caso nenhuma regra aplicável seja encontrada.
     */
    private String identify() throws InvalidAlgebraicExpressionException {
        if (isPerfectSquareTrinomial(this.source)) {
            return "Trinômio quadrado perfeito. Note que a expressão é formada " +
                    "por três monômios em que o primeiro e o último termo são quadrados e o termo cental é o dobro do " +
                    "produto entre o primeiro termo e o segundo termo.";
        }

        if (isPerfectCube(this.source)) {
            return (isCubeDifference(this.source)) ? "Cubo perfeito (cubo da diferença)." : "Cubo perfeito (cubo da soma).";
        }

        if (isDifferenceOfTwoSquares(this.source)) {
            return "Diferença de dois quadrados.";
        }

        if (isSecondDegreeTrinomial(this.source)) {
            return "Trinômio do segundo grau. Note que a expressão é um trinômio no " +
                    "formato &ascr;&xscr;&sup2; &plus; &bscr;&xscr; &plus; &cscr;.";
        }

        if (isGroupment(this.source)) {
            return "Agrupamento. Note que nesse caso temos um elemento em comum nos primeiros termos e um elemento " +
                    "comum nos últimos termos.";
        }

        if (isCommonFactor(this.source.getRootQuadruple(), this.source)) {
            return "Fator comum em evidência.";
        }


        throw new InvalidAlgebraicExpressionException("Regra não identificada");
    }


    //<editor-fold desc="PerfectSquareTrinomial">

    /**
     * Verifica se a equação se refere a uma regra de trinômio quadrado perfeito. Sendo a sua fórmula: a^2 +- 2 * a * b + b^2.
     * É válido mencionar que os valores da fórmula já irão estar "resolvidos". Ex.: 16 + 8x + x^2, onde o valor de a
     * é 4 (4^2 = 16, no primeiro argumento), e o de b é x, além disso o argumento do meio já está resolvido (2 * 4 * x = 8x).
     *
     * @param source {@link ThreeAddressCode} que contém as quádruplas a serem analisadas.
     * @return true caso a equação seja de uma regra de trinômio quadrado perfeito e false caso contrário.
     */
    public static boolean isPerfectSquareTrinomial(ThreeAddressCode source) {
        ExpandedQuadruple root = source.getRootQuadruple();

        //verifica o valor de a^2 e se tem mais quádruplas além da primeira
        if (isSquareReducibleTerm(root.getArgument1()) &&
                StringUtil.match(root.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {

            ExpandedQuadruple middleTermQuadruple = source.findQuadrupleByResult(root.getArgument2());

            //verifica se o termo do meio é valido
            if (StringUtil.matchAny(middleTermQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_EXPONENT.toString(),
                    RegexPattern.VARIABLE_WITH_COEFFICIENT.toString(), RegexPattern.NATURAL_NUMBER.toString())) {

                Monomial middleTerm = new Monomial(middleTermQuadruple.getArgument1());

                //verifica o valor de b^2
                if (isSquareReducibleTerm(middleTermQuadruple.getArgument2())) {

                    //valor de a
                    Monomial firstTerm = new Monomial(root.getArgument1());

                    //valor de b
                    Monomial secondTerm = new Monomial(middleTermQuadruple.getArgument2());

                    //Caso o valor de a e b sejam ambos monômios
                    if (StringUtil.isMonomial(middleTermQuadruple.getArgument1()) &&
                            (StringUtil.isMonomial(middleTermQuadruple.getArgument2()))) {

                        //se as partes literais de a e b são iguais (x e x, por exemplo)
                        if (firstTerm.getLiteralVariable().equals(secondTerm.getLiteralVariable())) {

                            //verifica se o valor do expoente do argumento do meio corresponde aos dos valores a e b
                            if ((firstTerm.getLiteralDegree() + secondTerm.getLiteralDegree()) / 2 == middleTerm.getLiteralDegree()) {

                                /*caso o coeficiente de a e b seja 1 (os monômios seriam x^2 e x^4, por exemplo), então o valor do
                                 * coeficiente do argumento do meio só pode ser 2 */
                                if (firstTerm.getCoefficient() == 1 && secondTerm.getCoefficient() == 1 && middleTerm.getCoefficient() == 2)
                                    return true;

                                    /*caso o coeficiente de a e b seja diferente de 1 (os monômios seriam 9x^2 e 16x, por exemplo),
                                     * então o valor do coeficiente do argumento do meio tem de ser a raiz dos
                                     * coeficientes dividido por 2, (2 * raiz de 9 * raiz de 16, ou seja, pela formula 2 * a * b) */
                                else if (firstTerm.getCoefficient() != 1 && secondTerm.getCoefficient() != 1) {
                                    return (((int) Math.sqrt(firstTerm.getCoefficient()) * (int) Math.sqrt(secondTerm.getCoefficient())) * 2) == middleTerm.getCoefficient();
                                } else {
                                    /*caso o coeficiente de a ou de b seja 1, faz a raiz quadrada do que é diferente de 1 e
                                     * verifica se o coeficiente do argumento do meio é esse valor * 2 */
                                    int coefficient = (firstTerm.getCoefficient() != 1) ? (int) Math.sqrt(firstTerm.getCoefficient()) : (int) Math.sqrt(secondTerm.getCoefficient());
                                    return (coefficient * 2) == middleTerm.getCoefficient();
                                }
                            }
                        }
                    }

                    //Caso o valor de a e b sejam ambos números
                    if (StringUtil.isNaturalNumber(root.getArgument1()) &&
                            StringUtil.isNaturalNumber(middleTermQuadruple.getArgument2())) {

                        return ((Math.round(Math.sqrt(firstTerm.getCoefficient()) * Math.sqrt(secondTerm.getCoefficient()))) * 2) == middleTerm.getCoefficient();
                    }

                    //Caso o valor de a seja um monômio e b seja um número
                    if (StringUtil.isMonomial(middleTermQuadruple.getArgument1()) &&
                            StringUtil.isNaturalNumber(middleTermQuadruple.getArgument2())) {

                        int variableCoefficient = (firstTerm.getCoefficient() == 1) ? 1 : (int) Math.sqrt(firstTerm.getCoefficient());
                        if (((variableCoefficient * (int) Math.sqrt(secondTerm.getCoefficient())) * 2) == middleTerm.getCoefficient()) {
                            if (middleTerm.getLiteralDegree() == firstTerm.getLiteralDegree() / 2) {

                                /*o trinomio de segundo grau e o quadrado perfeito podem ter praticamente a mesma
                                 * estrutura, então para ver qual é o caso, é verificado se as duas raízes gerados pelo
                                 * trinomio de segundo grau são iguais, se forem iguais então na verdade é um
                                 * trinomio quadrado perfeito*/
                                return isNotSecondDegree(source);
                            }
                        }
                    }

                    //Caso o valor de a seja um número e b seja um monômio
                    if (StringUtil.isNaturalNumber(root.getArgument1()) &&
                            StringUtil.isMonomial(middleTermQuadruple.getArgument1())) {
                        int variableCoefficient = (secondTerm.getCoefficient() == 1) ? 1 : (int) Math.sqrt(secondTerm.getCoefficient());
                        if (((variableCoefficient * (int) Math.sqrt(firstTerm.getCoefficient())) * 2) == middleTerm.getCoefficient()) {
                            return middleTerm.getLiteralDegree() == secondTerm.getLiteralDegree() / 2;
                        }
                    }
                }
            }

        }
        return false;
    }

    /**
     * Indica se um argumento tem uma raiz quadrada exata (em caso de o argumento ser um número), ou se o expoente
     * é divisível por 2 (em caso de o argumento ser um monômio).
     *
     * @param argument {@link String} que contém o elemento a ser analisado.
     * @return true caso o valor seja divisível por 2 ou tem raiz quadrada exata, e false caso contrário.
     */
    private static boolean isSquareReducibleTerm(String argument) {
        if (StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString())) {
            //faz a raiz quadrada do número
            double result = Math.sqrt(Integer.parseInt(argument));

            //verifica se é raiz quadrada exata
            return result % 1 == 0;
        }

        if (StringUtil.match(argument, RegexPattern.VARIABLE_WITH_EXPONENT.toString())) {
            Monomial monomial = new Monomial(argument);

            //verifica se o número é divisivel por 2
            boolean isDivisibleByTwo = monomial.getLiteralDegree() % 2 == 0;

            //faz a raiz do coeficiente do monomio
            double sqrt = Math.sqrt(monomial.getCoefficient());

            //verifica se é divisivel por 2 e também é uma raiz quadrada exata
            return isDivisibleByTwo && sqrt % 1 == 0;
        }
        return false;
    }

    /**
     * Verifica se a expressão tem a estrutura de um trinômio quadrado perfeito, porém na verdade é um trinômio
     * do segundo grau.
     *
     * @param source {@link ThreeAddressCode} que contém as quádruplas a serem analisadas.
     * @return true caso a regra a ser aplicada é de trinômio quadrado perfeito, e false caso a regra a ser
     * aplicada é a de trinômio do segundo grau.
     */
    private static boolean isNotSecondDegree(ThreeAddressCode source) {
        List<ExpandedQuadruple> expandedQuadruples = new ArrayList<>();

        //cria um ThreeAddressCode novo para não alterar as quádruplas do source original
        for (ExpandedQuadruple expandedQuadruple : source.getExpandedQuadruples()) {

            ExpandedQuadruple newQuadruple = new ExpandedQuadruple(expandedQuadruple.getOperator(),
                    expandedQuadruple.getArgument1(), expandedQuadruple.getArgument2(), expandedQuadruple.getResult(),
                    expandedQuadruple.getPosition(), expandedQuadruple.getLevel());
            expandedQuadruples.add(newQuadruple);
        }
        ThreeAddressCode trinomialSource = new ThreeAddressCode(source.getLeft(), expandedQuadruples);

        //Faz a regra do trinomio do segundo grau para poder identificar qual a regra correta
        FatorationRuleSecondDegreeTrinomialConvertToDivisionFormula perfectSqrFirstRule = new FatorationRuleSecondDegreeTrinomialConvertToDivisionFormula();
        perfectSqrFirstRule.handle(Collections.singletonList(trinomialSource));

        //Aplica a regra que identifica as duas raizes (x' e x'')
        FatorationRuleSecondDegreeTrinomialProduct perfectSqrSecondRule = new FatorationRuleSecondDegreeTrinomialProduct();

        //se as raízes forem iguais, então a regra é de trinômio do segundo grau
        return perfectSqrSecondRule.areRootsEqual(trinomialSource);
    }
    //</editor-fold>>

    //<editor-fold desc="PerfectCube">

    /**
     * Verifica se a equação se refere a uma regra de cubo perfeito. Sendo a sua fórmula: a^3 +- 3 * a^2 * b + 3 * a * b^2 +- b^3.
     *
     * @param source {@link ThreeAddressCode} que contém as quádruplas a serem analisadas.
     * @return true caso a equação seja de uma regra de cubo perfeito e false caso contrário.
     */
    public static boolean isPerfectCube(ThreeAddressCode source) {
        ExpandedQuadruple root = source.getRootQuadruple();
        ExpandedQuadruple last = source.getLastQuadruple(root);
        ExpandedQuadruple middleQuadruple = source.findQuadrupleByResult(root.getArgument2());

        //caso tiver uma quádrupla negativa
        if (StringUtil.match(root.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            root = source.findQuadrupleByResult(root.getArgument1());
        }

        //Caso o valor de a e b sejam ambos monômios
        if (isCubeReducibleVariableTerm(root.getArgument1()) && isCubeReducibleVariableTerm(last.getArgument2())) {

            Monomial firstTerm = new Monomial(root.getArgument1());
            Monomial lastTerm = new Monomial(last.getArgument2());

            //verifica a parte da fórmula 3 * a^2 * b, e também da 3 * b * b^2
            if (isMiddleTermValid(firstTerm, lastTerm, middleQuadruple.getArgument1(), true)) {
                middleQuadruple = source.findQuadrupleByResult(middleQuadruple.getArgument2());
                return isMiddleTermValid(firstTerm, lastTerm, middleQuadruple.getArgument1(), false);
            }
        }

        //Caso o valor de a e b sejam ambos números
        if (isCubeReducibleNumericTerm(root.getArgument1()) && isCubeReducibleNumericTerm(last.getArgument2())) {

            int firstCoefficient = (int) Math.cbrt(Integer.parseInt(root.getArgument1()));
            int lastCoefficient = (int) Math.cbrt(Integer.parseInt(last.getArgument2()));

            if (Integer.parseInt(middleQuadruple.getArgument1()) == 3 * (Math.pow(firstCoefficient, 2) * lastCoefficient)) {
                middleQuadruple = source.findQuadrupleByResult(middleQuadruple.getArgument2());
                return Integer.parseInt(middleQuadruple.getArgument1()) == 3 * (Math.pow(lastCoefficient, 2) * firstCoefficient);
            }
        }

        //Caso o valor de a seja um monômio e b seja um número
        if (isCubeReducibleVariableTerm(root.getArgument1()) && isCubeReducibleNumericTerm(last.getArgument2())) {

            Monomial firstTerm = new Monomial(root.getArgument1());
            int lastCoefficient = (int) Math.cbrt(Integer.parseInt(last.getArgument2()));

            if (isVariableAndNumberValid(firstTerm, lastCoefficient, middleQuadruple, true, true)) {
                middleQuadruple = source.findQuadrupleByResult(middleQuadruple.getArgument2());
                return isVariableAndNumberValid(firstTerm, lastCoefficient, middleQuadruple, true, false);
            }
        }

        //Caso o valor de a seja um número e b seja um monômio
        if (isCubeReducibleNumericTerm(root.getArgument1()) && isCubeReducibleVariableTerm(last.getArgument2())) {

            Monomial lastTerm = new Monomial(last.getArgument2());
            int firstCoefficient = (int) Math.cbrt(Integer.parseInt(root.getArgument1()));

            if (isVariableAndNumberValid(lastTerm, firstCoefficient, middleQuadruple, false, true)) {
                middleQuadruple = source.findQuadrupleByResult(middleQuadruple.getArgument2());
                return isVariableAndNumberValid(lastTerm, firstCoefficient, middleQuadruple, false, false);
            }
        }
        return false;
    }

    /**
     * Verifica se o argumento é um valor numérico que tem uma raíz cúbica inteira.
     *
     * @param argument {@link String} que será analisado.
     * @return true caso o número seja reduzível ao cubo, e false caso contrário.
     */
    private static boolean isCubeReducibleNumericTerm(String argument) {
        return StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString()) &&
                (Math.cbrt(Integer.parseInt(argument)) % 1 == 0);

    }

    /**
     * Verifica se o argumento é um {@link Monomial} que tem como coeficiente um número que tem raíz cúbica inteira, e se
     * o seu expoente é um valor que é divisível por 3.
     *
     * @param argument {@link String} que será analisado.
     * @return true caso o {@link Monomial} seja reduzível ao cubo, e false caso contrário.
     */
    private static boolean isCubeReducibleVariableTerm(String argument) {
        return StringUtil.match(argument, RegexPattern.VARIABLE_WITH_EXPONENT.toString()) &&
                (new Monomial(argument).getLiteralDegree() % 3 == 0 &&
                        (Math.cbrt(new Monomial(argument).getCoefficient()) % 1 == 0));

    }

    /**
     * Verifica se o valor no meio é válido. Por exemplo, na expressão y^9 + 3y^7 + 3y^5 + y^3, os dois valores do meio
     * são 3y^7 e 3y^5.
     *
     * @param firstTerm         {@link Monomial} que representa o valor a^3.
     * @param lastTerm          {@link Monomial} que representa o valor b^3.
     * @param middleCoefficient {@link String} que contém o valor do meio a ser processado.
     * @param isMiddleTerm1     booleano que indica qual dos dois valores do meio está sendo processado.
     * @return true caso o valor do meio seja válido, e false caso contrário.
     */
    private static boolean isMiddleTermValid(Monomial firstTerm, Monomial lastTerm, String middleCoefficient, boolean isMiddleTerm1) {
        if (!isMiddleTerm1) {
            Monomial aux = firstTerm;
            firstTerm = lastTerm;
            lastTerm = aux;
        }

        String firstTermLiteral = firstTerm.getLiteralVariable();
        String lastTermLiteral = lastTerm.getLiteralVariable();

        //identifica se todos os monomios tem a mesma parte literal
        if (middleCoefficient.contains(firstTermLiteral) && middleCoefficient.contains(lastTermLiteral)) {

            //verifica se o coeficiente do meio é elevado a algum expoente maior que 1
            if (middleCoefficient.charAt(middleCoefficient.indexOf(firstTermLiteral) + 1) == '^') {

                //verifica se os valores de a^3 e b^3 são divíveis por 3
                if (firstTerm.getLiteralDegree() % 3 == 0 && lastTerm.getLiteralDegree() % 3 == 0) {

                    int firstPower = firstTerm.getLiteralDegree() / 3;
                    int lastPower = lastTerm.getLiteralDegree() / 3;

                    //verifica os expoentes dos valores de a e b com o valor no meio
                    if ((firstPower * 2) + lastPower == new Monomial(middleCoefficient).getLiteralDegree()) {

                        //verifica o coeficiente do valor do meio: se os valores de a e b forem 1
                        if (firstTerm.getCoefficient() == 1 && lastTerm.getCoefficient() == 1 && middleCoefficient.startsWith("3"))
                            return true;

                            //verifica o coeficiente do valor do meio: se os valores de a e b forem diferentes 1
                        else if (firstTerm.getCoefficient() != 1 && lastTerm.getCoefficient() != 1) {
                            int firstTermCbrt = (int) Math.cbrt(firstTerm.getCoefficient());
                            int lastTermCbrt = (int) Math.cbrt(lastTerm.getCoefficient());
                            return ((firstTermCbrt * lastTermCbrt) * 3) == middleCoefficient.charAt(0);

                            //verifica o coeficiente do valor do meio: se algum dos valores de a e b é 1
                        } else {
                            int coefficient = (firstTerm.getCoefficient() != 1) ?
                                    (int) Math.cbrt(firstTerm.getCoefficient()) :
                                    (int) Math.cbrt(lastTerm.getCoefficient());

                            return (coefficient * 3) == middleCoefficient.charAt(0);
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * No caso do valor de a ou de b ser um número e o outro ser um monômio, verifica se os valores estão
     * corretos.
     *
     * @param monomial            valor que indica o {@link Monomial}, independente se é o valor a ou b.
     * @param numberCoefficient   valor que indica o coeficiente, independente se é o valor a ou b.
     * @param middleQuadruple     {@link ExpandedQuadruple} que contém os valores do meio.
     * @param isFirstTermALiteral booleano que indica se o valor de a é o monômio ou não.
     * @param isMiddleTerm1       booleano que indica qual argumento está sendo processado da {@code middleQuadruple}.
     * @return true caso os valores de a e b estejam corretos e false caso contrário.
     */
    private static boolean isVariableAndNumberValid(Monomial monomial, int numberCoefficient,
                                                    ExpandedQuadruple middleQuadruple, boolean isFirstTermALiteral,
                                                    boolean isMiddleTerm1) {

        int monomialCoefficient = (monomial.getCoefficient() == 1) ?
                monomial.getCoefficient() : (int) Math.cbrt(monomial.getCoefficient());

        //identifica se o valor a ou b que é um monômio
        if ((isMiddleTerm1 && !isFirstTermALiteral) || (!isMiddleTerm1 && isFirstTermALiteral))
            numberCoefficient = (int) Math.pow(numberCoefficient, 2);
        else
            monomialCoefficient = (int) Math.pow(monomialCoefficient, 2);

        //verifica se os valores são válidso
        Monomial monomialMiddleTerm = new Monomial(middleQuadruple.getArgument1());
        return monomialMiddleTerm.getCoefficient() == (3 * (monomialCoefficient * numberCoefficient));
    }


    /**
     * Indica se a expressão é cubo da diferença ou não.
     *
     * @param source {@link ThreeAddressCode} que contém as quádruplas a serem analisadas.
     * @return true caso a expressão seja o cubo da diferença e false caso contrário.
     */
    private static boolean isCubeDifference(ThreeAddressCode source) {
        ExpandedQuadruple root = source.getRootQuadruple();
        if (root.isMinus()) {
            ExpandedQuadruple lastQuadruple = source.getLastQuadruple(root);
            return lastQuadruple.isMinus();
        }
        return false;
    }

    //</editor-fold>

    //<editor-fold desc="DifferenceOfTwoSquares">

    /**
     * Verifica se a equação se refere a uma regra de diferença de dois quadrados. Sendo a sua fórmula: a^2 - b^2.
     *
     * @param source {@link ThreeAddressCode} que contém as quádruplas a serem analisadas.
     * @return true caso a equação seja de uma regra de diferença de dois quadrados e false caso contrário.
     */
    public static boolean isDifferenceOfTwoSquares(ThreeAddressCode source) {
        if (source.getExpandedQuadruples().size() == 1) {
            ExpandedQuadruple root = source.getRootQuadruple();
            if (root.isMinus()) {
                return isSquareReducibleTerm(root.getArgument1()) && isSquareReducibleTerm(root.getArgument2());
            }
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="Two Binomial Product">

    /**
     * Verifica se a equação se refere a uma regra de trinômio do segundo grau. Sendo a sua fórmula: ax^2 + bx + c
     *
     * @param source {@link ThreeAddressCode} que contém as quádruplas a serem analisadas.
     * @return true caso a equação seja de uma regra de diferença de dois quadrados e false caso contrário.
     */
    public static boolean isSecondDegreeTrinomial(ThreeAddressCode source) {
        ExpandedQuadruple root = source.getRootQuadruple();

        //verifica se o número de argumentos é exatamente 3, ou seja, se tem somente os valores a, b e c
        if (source.argumentsCount(root) == 3) {
            return containsMonomialWithExponent(root, 2, source) &&
                    containsMonomialWithExponent(root, 1, source) &&
                    containsMonomialWithExponent(root, 0, source);
        }

        return false;
    }

    /**
     * Verifica se existe um monômio de acordo com seu expoente, e assim identificando qual é o valor a, b e c.
     *
     * @param iterationQuadruple {@link ExpandedQuadruple} que representa a quádrupla inicial para ser buscado o monômio.
     * @param exponent           inteiro que representa o expoente a ser buscado, no qual podem ser:
     *                           <ul>
     *                              <li>2: significa um monomio do tipo x^2, ou seja, o valor a.</li>
     *                              <li>1: significa um monomio do tipo x, ou seja, o valor b.</li>
     *                              <li>0: significa um monomio que não tem parte literal, ou seja, o valor c.</li>
     *                           </ul>
     * @return true caso haja o monômio com o expoente correspondente, e false caso contrário.
     */
    private static boolean containsMonomialWithExponent(ExpandedQuadruple iterationQuadruple, int exponent, ThreeAddressCode source) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return containsMonomialWithExponent(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), exponent, source);
        }


        if (exponent != 0) {

            //se for o valor de a ou b, ou sej,a se tiver um expoente
            Monomial iterationMonomialArgument = new Monomial(iterationQuadruple.getArgument1());
            if (iterationMonomialArgument.getCoefficient() != 0 && iterationMonomialArgument.getLiteralDegree() == exponent)
                return true;
        } else {

            //se for o valor de c
            if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.NATURAL_NUMBER.toString()) &&
                    !iterationQuadruple.getArgument1().equals("0")) {
                return true;
            }
        }

        //caso a quádrupla seja negativa
        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return containsMonomialWithExponent(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), exponent, source);
        }

        if (exponent != 0) {
            Monomial iterationMonomialArgument = new Monomial(iterationQuadruple.getArgument2());
            return iterationMonomialArgument.getCoefficient() != 0 && iterationMonomialArgument.getLiteralDegree() == exponent;
        }
        return StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.NATURAL_NUMBER.toString()) &&
                !iterationQuadruple.getArgument2().equals("0");
    }
    //</editor-fold>

    //<editor-fold desc="Groupment">

    /**
     * Verifica se a equação se refere a uma regra de agrupamento.
     *
     * @param source {@link ThreeAddressCode} que contém as quádruplas a serem analisadas.
     * @return true caso a equação seja de uma regra de agrupamento e false caso contrário.
     */
    public static boolean isGroupment(ThreeAddressCode source) throws InvalidAlgebraicExpressionException {
        ExpandedQuadruple root = source.getRootQuadruple();

        //um agrupamento é basicamente dois fatores em comum, então deve ser validada essa regra antes
        if (isCommonFactor(root, source)) {
            int argumentsCount = source.argumentsCount(source.getRootQuadruple());

            //deve ter pelo menos 4 argumentos para poder gerar dois conjuntos (conjuges), e deve ser par
            if (argumentsCount > 3 && argumentsCount % 2 == 0) {

                //obtem os ThreeAddressCodes que serão utilizados para gerar os conjuges
                List<ThreeAddressCode> spousesSources = generateCouple(source, argumentsCount);

                if (!spousesSources.isEmpty()) {
                    ThreeAddressCode firstSpouse = spousesSources.get(0);
                    ThreeAddressCode secondSpouse = spousesSources.get(1);

                    String secondCoupleOperation = source.findQuadrupleByArgument(secondSpouse.getLeft()).getOperator();

                    //cria o casal de conjuges
                    Couple couple = new Couple(firstSpouse, secondSpouse, secondCoupleOperation);
                    if (couple.areEmpty())
                        return false;

                    /*para ser um agrupamento, os fatores dos conjuges devem ser diferentes, e os multiplicadores devem
                     * ser iguais*/
                    return !couple.getFirstSpouseFactor().equals(couple.getSecondSpouseFactor()) &&
                            couple.isFirstSpouseMultiplierEqualsSecond();
                }
            }
        }

        return false;
    }


    /**
     * Gera {@link ThreeAddressCode}s serão utilizados para criar o casal.
     *
     * @param source         {@link ThreeAddressCode} de onde serão obtidos as informações para serem criados os novos {@link ThreeAddressCode}.
     * @param argumentsCount {@link ThreeAddressCode} número de argumento dentro do {@code source}.
     * @return {@link List} de {@link ThreeAddressCode}, onde cada item é referente a um conjuge do casal.
     */
    public static List<ThreeAddressCode> generateCouple(ThreeAddressCode source, int argumentsCount) {

        List<ExpandedQuadruple> firstSpouseQuadruples = new ArrayList<>();
        List<ExpandedQuadruple> secondSpouseQuadruples = new ArrayList<>();

        //preenche as quádruplas dos dois conjuges
        fillQuadruples(source.getRootQuadruple(), source, firstSpouseQuadruples, secondSpouseQuadruples, 0,
                argumentsCount);

        /*a última quadrupla do primeiro conjuge deve ser alterada, já que fica com uma quádrupla como argumento2,
         * remanescente das quádruplas originais*/
        ExpandedQuadruple lastQuadruple = firstSpouseQuadruples.get(firstSpouseQuadruples.size() - 1);
        if (StringUtil.match(lastQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            firstSpouseQuadruples.get(firstSpouseQuadruples.size() - 2).setArgument2(lastQuadruple.getArgument1());
            firstSpouseQuadruples.remove(firstSpouseQuadruples.size() - 1);
        }

        //se houver elementos nos conjuges
        if (!firstSpouseQuadruples.isEmpty() && !secondSpouseQuadruples.isEmpty()) {

            //cria o ThreeAddressCode do primeiro conjuge
            ThreeAddressCode firstSource = new ThreeAddressCode();
            firstSource.setExpandedQuadruples(firstSpouseQuadruples);

            ExpandedQuadruple firstQuadruple = firstSpouseQuadruples.get(0);
            String firstQuadrupleResult = firstQuadruple.getResult();

            //caso o primeiro argumento for negativo é preciso obtê-lo para ajustar o left
            if (firstQuadruple.isNegative())
                firstQuadrupleResult = firstSource.findQuadrupleByArgument1(firstQuadrupleResult).getResult();
            firstSource.setLeft(firstQuadrupleResult);

            //cria o ThreeAddressCode do segundo conjuge
            ThreeAddressCode secondSource = new ThreeAddressCode();
            secondSource.setExpandedQuadruples(secondSpouseQuadruples);
            secondSource.setLeft(secondSpouseQuadruples.get(0).getResult());

            //adiciona os ThreeAddressCode criados a lista
            List<ThreeAddressCode> newSourceList = new ArrayList<>();
            newSourceList.add(firstSource);
            newSourceList.add(secondSource);
            return newSourceList;
        }
        return new ArrayList<>();
    }

    /**
     * Preenche as listas de quádruplas ({@code firstQuadruples} e {@code secondQuadruples}) que serão utilizadas para
     * criar o casal.
     *
     * @param iterationQuadruple     {@link ExpandedQuadruple} inicial de onde será analisado os argumentos.
     * @param source                 {@link ThreeAddressCode} que contém todas as quádruplas a serem analisadas.
     * @param firstQuadruples        {@link List} de {@link ExpandedQuadruple} que conterá as quádruplas do primeiro conjuge.
     * @param secondQuadruples       {@link List} de {@link ExpandedQuadruple} que conterá as quádruplas do segundo conjuge.
     * @param analyzedArgumentsCount inteiro que representa o número de argumento já analisados.
     * @param argumentsCount         inteiro que representa o número total de argumentos presentes no {@code source}.
     */
    private static void fillQuadruples(ExpandedQuadruple iterationQuadruple, ThreeAddressCode source,
                                       List<ExpandedQuadruple> firstQuadruples, List<ExpandedQuadruple> secondQuadruples,
                                       int analyzedArgumentsCount, int argumentsCount) {

        //cria a nova quádrupla que será inserida a partir da que está no source
        ExpandedQuadruple expandedQuadruple = new ExpandedQuadruple(iterationQuadruple.getOperator(), iterationQuadruple.getArgument1(),
                iterationQuadruple.getArgument2(), iterationQuadruple.getResult(), iterationQuadruple.getPosition(), iterationQuadruple.getLevel());

        if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple innerQuadruple = source.findQuadrupleByResult(expandedQuadruple.getArgument1());
            firstQuadruples.add(new ExpandedQuadruple(innerQuadruple.getOperator(), innerQuadruple.getArgument1(),
                    innerQuadruple.getArgument2(), innerQuadruple.getResult(), innerQuadruple.getPosition(), innerQuadruple.getLevel()));
        }

        /*essa verificação que indica para qual conjuge vai a quádrupla. Se cair no if, quer dizer que está na primeira
         * metade de argumentos, e então vai para o primeiro conjuge. No else é a segunda metade, e vai para o segundo conjuge*/
        if (analyzedArgumentsCount < argumentsCount / 2) {
            firstQuadruples.add(expandedQuadruple);
        } else {
            secondQuadruples.add(expandedQuadruple);
        }

        analyzedArgumentsCount++;

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            fillQuadruples(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), source, firstQuadruples,
                    secondQuadruples, analyzedArgumentsCount, argumentsCount);
        } else {

            //quando for a última quádrupla do segundo conjuge, tem de ajustar o argument2
            if (!secondQuadruples.isEmpty())
                secondQuadruples.get(secondQuadruples.indexOf(expandedQuadruple)).setArgument2(iterationQuadruple.getArgument2());
        }
    }


    //</editor-fold>

    //<editor-fold desc="CommonFactor">

    /**
     * Verifica se a equação se refere a uma regra de fator em comum.
     *
     * @param source {@link ThreeAddressCode} que contém as quádruplas a serem analisadas.
     * @return true caso a equação seja de uma regra de fator em comum e false caso contrário.
     */
    public static boolean isCommonFactor(ExpandedQuadruple iterationQuadruple, ThreeAddressCode source) {
        String argument = (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
                ? source.findQuadrupleByResult(iterationQuadruple.getArgument1()).getArgument1() : iterationQuadruple.getArgument1();
        Monomial monomialPattern = new Monomial(argument);

        //se tiver algum padrão, seja um número em comum, um monômio, ou até mesmo um divisor em comum entre números
        return isThereAEqualPattern(iterationQuadruple, monomialPattern.getLiteral(), source);
    }

    /**
     * Indica se há algum tipo de padrão (fator em comum) entre as quádruplas.
     *
     * @param iterationQuadruple {@link ExpandedQuadruple} de onde se iniciará a análise.
     * @param pattern            {@link String} que identifica qual o padrão atual que está sendo verificado com os outros argumentos.
     * @param source             {@link ThreeAddressCode} que contém as quádruplas a serem analisadas.
     * @return true caso haja um fator em comum entre as quádruplas e false caso contrário.
     */
    private static boolean isThereAEqualPattern(ExpandedQuadruple iterationQuadruple, String pattern, ThreeAddressCode source) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return isThereAEqualPattern(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), pattern, source);
        }

        //compara-se o argumento da iteração com o padrão encontrado
        Monomial iterationMonomialArgument = new Monomial(iterationQuadruple.getArgument1());

        //se as partes literais tiverem variáveis iguais
        if (iterationMonomialArgument.getLiteral().contains(pattern))
            return true;

        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return isThereAEqualPattern(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), pattern, source);
        }

        iterationMonomialArgument = new Monomial(iterationQuadruple.getArgument2());
        return iterationMonomialArgument.getLiteral().contains(pattern);
    }
    //</editor-fold>>

}
