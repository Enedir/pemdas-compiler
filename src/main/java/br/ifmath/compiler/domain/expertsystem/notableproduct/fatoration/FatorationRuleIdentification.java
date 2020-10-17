package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.groupment.Couples;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.twobinomialproduct.FatorationRuleTwoBinomialProduct;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.twobinomialproduct.FatorationRuleTwoBinomialProductConvertToDivisionFormula;
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

        if (isTwoBinomialProduct(this.source)) {
            return "Trinômio do segundo grau. Note que a expressão é um trinômio no " +
                    "formato &ascr;&xscr;&sup2; &plus; &bscr;&xscr; &plus; &cscr;.";
        }

        if (isGroupment(this.source)) {
            return "Agrupamento. Note que nesse caso temos um elemento em comum nos dois primeiros termos e um " +
                    "elemento comum no terceiro e quarto termos.";
        }

        if (isCommonFactor(this.source.getRootQuadruple(), this.source)) {
            return "Fator comum em evidência.";
        }


        throw new InvalidAlgebraicExpressionException("Regra não identificada");
    }

    //<editor-fold desc="CommonFactor">

    public static boolean isCommonFactor(ExpandedQuadruple iterationQuadruple, ThreeAddressCode source) {
        String argument = (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
                ? source.findQuadrupleByResult(iterationQuadruple.getArgument1()).getArgument1() : iterationQuadruple.getArgument1();
        Monomial monomialPattern = new Monomial(argument);
        return isThereAEqualPattern(iterationQuadruple, monomialPattern.getLiteral(), source);
    }

    private static boolean isThereAEqualPattern(ExpandedQuadruple iterationQuadruple, String pattern, ThreeAddressCode source) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return isThereAEqualPattern(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), pattern, source);
        }

        Monomial iterationMonomialArgument = new Monomial(iterationQuadruple.getArgument1());
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

    //<editor-fold desc="Groupment">

    public static boolean isGroupment(ThreeAddressCode source) throws InvalidAlgebraicExpressionException {
        ExpandedQuadruple root = source.getRootQuadruple();
        if (isCommonFactor(root, source)) {
            int argumentsCount = argumentsCount(source.getRootQuadruple(), source);
            if (argumentsCount > 3 && argumentsCount % 2 == 0) {

                List<ThreeAddressCode> couplesSources = generateCouples(source, argumentsCount);

                if (!couplesSources.isEmpty()) {
                    ThreeAddressCode firstCouple = couplesSources.get(0);
                    ThreeAddressCode secondCouple = couplesSources.get(1);


                    String secondCoupleOperation = source.findQuadrupleByArgument(secondCouple.getLeft()).getOperator();

                    Couples couples = new Couples(firstCouple, secondCouple, secondCoupleOperation);
                    if (couples.areEmpty())
                        return false;
                    return !couples.getFirstCoupleFactor().equals(couples.getSecondCoupleFactor()) &&
                            couples.isFirstCoupleEqualsSecond();
                }
            }
        }

        return false;
    }


    public static List<ThreeAddressCode> generateCouples(ThreeAddressCode source, int argumentsCount) {

        List<ExpandedQuadruple> firstCoupleQuadruples = new ArrayList<>();
        List<ExpandedQuadruple> secondCoupleQuadruples = new ArrayList<>();
        fillQuadruples(source.getRootQuadruple(), source, firstCoupleQuadruples, secondCoupleQuadruples, 0,
                argumentsCount);

        ExpandedQuadruple lastQuadruple = firstCoupleQuadruples.get(firstCoupleQuadruples.size() - 1);
        if (StringUtil.match(lastQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            firstCoupleQuadruples.get(firstCoupleQuadruples.size() - 2).setArgument2(lastQuadruple.getArgument1());
            firstCoupleQuadruples.remove(firstCoupleQuadruples.size() - 1);
        }
        if (!firstCoupleQuadruples.isEmpty() && !secondCoupleQuadruples.isEmpty()) {
            ThreeAddressCode firstSource = new ThreeAddressCode();
            firstSource.setExpandedQuadruples(firstCoupleQuadruples);
            firstSource.setLeft(firstCoupleQuadruples.get(0).getResult());

            ThreeAddressCode secondSource = new ThreeAddressCode();
            secondSource.setExpandedQuadruples(secondCoupleQuadruples);
            secondSource.setLeft(secondCoupleQuadruples.get(0).getResult());

            List<ThreeAddressCode> newSourceList = new ArrayList<>();
            newSourceList.add(firstSource);
            newSourceList.add(secondSource);
            return newSourceList;
        }
        return new ArrayList<>();
    }

    private static void fillQuadruples(ExpandedQuadruple iterationQuadruple, ThreeAddressCode source,
                                       List<ExpandedQuadruple> firstQuadruples, List<ExpandedQuadruple> secondQuadruples,
                                       int analyzedArgumentsCount, int argumentsCount) {

        ExpandedQuadruple expandedQuadruple = new ExpandedQuadruple(iterationQuadruple.getOperator(), iterationQuadruple.getArgument1(),
                iterationQuadruple.getArgument2(), iterationQuadruple.getResult(), iterationQuadruple.getPosition(), iterationQuadruple.getLevel());

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
            if (!secondQuadruples.isEmpty())
                secondQuadruples.get(secondQuadruples.indexOf(expandedQuadruple)).setArgument2(iterationQuadruple.getArgument2());
        }
    }


    public static int argumentsCount(ExpandedQuadruple iterationQuadruple, ThreeAddressCode source) {
        int sum = 0;
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            sum += argumentsCount(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), source);
        }

        sum++;
        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            sum += argumentsCount(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), source);
        } else {
            sum++;
        }

        return sum;
    }

    //</editor-fold>

    //<editor-fold desc="PerfectSquareTrinomial">
    public static boolean isPerfectSquareTrinomial(ThreeAddressCode source) {
        ExpandedQuadruple root = source.getRootQuadruple();
        if (isSquareReducibleTerm(root.getArgument1())) {
            if (StringUtil.match(root.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple middleTermQuadruple = source.findQuadrupleByResult(root.getArgument2());
                if (StringUtil.matchAny(middleTermQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_EXPONENT.toString(),
                        RegexPattern.VARIABLE_WITH_COEFFICIENT.toString(), RegexPattern.NATURAL_NUMBER.toString())) {
                    Monomial middleTerm = new Monomial(middleTermQuadruple.getArgument1());
                    if (isSquareReducibleTerm(middleTermQuadruple.getArgument2())) {

                        Monomial firstTerm = new Monomial(root.getArgument1());
                        Monomial secondTerm = new Monomial(middleTermQuadruple.getArgument2());
                        //Variável com variável
                        if (StringUtil.matchAny(middleTermQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_EXPONENT.toString(),
                                RegexPattern.VARIABLE_WITH_COEFFICIENT.toString()) &&
                                (StringUtil.matchAny(middleTermQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_EXPONENT.toString(),
                                        RegexPattern.VARIABLE_WITH_COEFFICIENT.toString()))) {
                            if (firstTerm.getLiteralVariable().equals(secondTerm.getLiteralVariable())) {
                                if ((firstTerm.getLiteralDegree() + secondTerm.getLiteralDegree()) / 2 == middleTerm.getLiteralDegree()) {
                                    if (firstTerm.getCoefficient() == 1 && secondTerm.getCoefficient() == 1 && middleTerm.getCoefficient() == 2)
                                        return true;
                                    else if (firstTerm.getCoefficient() != 1 && secondTerm.getCoefficient() != 1) {
                                        return (((int) Math.sqrt(firstTerm.getCoefficient()) * (int) Math.sqrt(secondTerm.getCoefficient())) * 2) == middleTerm.getCoefficient();
                                    } else {
                                        int coefficient = (firstTerm.getCoefficient() != 1) ? (int) Math.sqrt(firstTerm.getCoefficient()) : (int) Math.sqrt(secondTerm.getCoefficient());
                                        return (coefficient * 2) == middleTerm.getCoefficient();
                                    }
                                }
                            }
                        }

                        //Número com número
                        if (StringUtil.match(root.getArgument1(), RegexPattern.NATURAL_NUMBER.toString()) &&
                                StringUtil.match(middleTermQuadruple.getArgument2(), RegexPattern.NATURAL_NUMBER.toString())) {
                            return ((Math.round(Math.sqrt(firstTerm.getCoefficient()) * Math.sqrt(secondTerm.getCoefficient()))) * 2) == middleTerm.getCoefficient();
                        }

                        //Variável com número
                        if (StringUtil.matchAny(middleTermQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_EXPONENT.toString(),
                                RegexPattern.VARIABLE_WITH_COEFFICIENT.toString()) &&
                                StringUtil.match(middleTermQuadruple.getArgument2(), RegexPattern.NATURAL_NUMBER.toString())) {
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

                        //Número com variável
                        if (StringUtil.match(root.getArgument1(), RegexPattern.NATURAL_NUMBER.toString()) &&
                                StringUtil.matchAny(middleTermQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_EXPONENT.toString(),
                                        RegexPattern.VARIABLE_WITH_COEFFICIENT.toString())) {
                            int variableCoefficient = (secondTerm.getCoefficient() == 1) ? 1 : (int) Math.sqrt(secondTerm.getCoefficient());
                            if (((variableCoefficient * (int) Math.sqrt(firstTerm.getCoefficient())) * 2) == middleTerm.getCoefficient()) {
                                return middleTerm.getLiteralDegree() == secondTerm.getLiteralDegree() / 2;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

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

    private static boolean isNotSecondDegree(ThreeAddressCode source) {
        List<ExpandedQuadruple> expandedQuadruples = new ArrayList<>();
        for (ExpandedQuadruple expandedQuadruple : source.getExpandedQuadruples()) {
            ExpandedQuadruple newQuadruple = new ExpandedQuadruple(expandedQuadruple.getOperator(),
                    expandedQuadruple.getArgument1(), expandedQuadruple.getArgument2(), expandedQuadruple.getResult(),
                    expandedQuadruple.getPosition(), expandedQuadruple.getLevel());
            expandedQuadruples.add(newQuadruple);
        }
        ThreeAddressCode trinomialSource = new ThreeAddressCode(source.getLeft(), expandedQuadruples);

        FatorationRuleTwoBinomialProductConvertToDivisionFormula perfectSqrFirstRule = new FatorationRuleTwoBinomialProductConvertToDivisionFormula();
        perfectSqrFirstRule.handle(Collections.singletonList(trinomialSource));

        FatorationRuleTwoBinomialProduct perfectSqrSecondRule = new FatorationRuleTwoBinomialProduct();
        return perfectSqrSecondRule.areRootsEqual(trinomialSource);
    }
    //</editor-fold>>

    //<editor-fold desc="DifferenceOfTwoSquares">
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

    //<editor-fold desc="PerfectCube">

    public static boolean isPerfectCube(ThreeAddressCode source) {
        ExpandedQuadruple root = source.getRootQuadruple();
        ExpandedQuadruple last = source.getLastQuadruple(root);
        ExpandedQuadruple middleQuadruple = source.findQuadrupleByResult(root.getArgument2());


        //Variável com variável
        if (isCubeReducibleVariableTerm(root.getArgument1()) && isCubeReducibleVariableTerm(last.getArgument2())) {
            Monomial firstTerm = new Monomial(root.getArgument1());
            Monomial lastTerm = new Monomial(last.getArgument2());
            if (isMiddleTermValid(firstTerm, lastTerm, middleQuadruple.getArgument1(), true)) {
                middleQuadruple = source.findQuadrupleByResult(middleQuadruple.getArgument2());
                return isMiddleTermValid(firstTerm, lastTerm, middleQuadruple.getArgument1(), false);
            }
        }

        //Número com número
        if (isCubeReducibleNumericTerm(root.getArgument1()) && isCubeReducibleNumericTerm(last.getArgument2())) {
            int firstCoefficient = (int) Math.cbrt(Integer.parseInt(root.getArgument1()));
            int lastCoefficient = (int) Math.cbrt(Integer.parseInt(last.getArgument2()));
            if (Integer.parseInt(middleQuadruple.getArgument1()) == 3 * (Math.pow(firstCoefficient, 2) * lastCoefficient)) {
                middleQuadruple = source.findQuadrupleByResult(middleQuadruple.getArgument2());
                return Integer.parseInt(middleQuadruple.getArgument1()) == 3 * (Math.pow(lastCoefficient, 2) * firstCoefficient);
            }
        }

        //Variável com número
        if (isCubeReducibleVariableTerm(root.getArgument1()) && isCubeReducibleNumericTerm(last.getArgument2())) {
            Monomial firstTerm = new Monomial(root.getArgument1());
            int lastCoefficient = (int) Math.cbrt(Integer.parseInt(last.getArgument2()));
            if (isVariableAndNumberValid(firstTerm, lastCoefficient, middleQuadruple, true, true)) {
                middleQuadruple = source.findQuadrupleByResult(middleQuadruple.getArgument2());
                return isVariableAndNumberValid(firstTerm, lastCoefficient, middleQuadruple, true, false);
            }
        }

        //Número com variável
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

    private static boolean isCubeReducibleNumericTerm(String argument) {
        return StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString()) &&
                (Math.cbrt(Integer.parseInt(argument)) % 1 == 0);

    }

    private static boolean isCubeReducibleVariableTerm(String argument) {
        return StringUtil.match(argument, RegexPattern.VARIABLE_WITH_EXPONENT.toString()) &&
                (new Monomial(argument).getLiteralDegree() % 3 == 0 &&
                        (Math.cbrt(new Monomial(argument).getCoefficient()) % 1 == 0));

    }


    private static boolean isMiddleTermValid(Monomial firstTerm, Monomial lastTerm, String middleCoefficient, boolean isMiddleTerm1) {
        if (!isMiddleTerm1) {
            Monomial aux = firstTerm;
            firstTerm = lastTerm;
            lastTerm = aux;
        }

        String firstTermLiteral = firstTerm.getLiteralVariable();
        String lastTermLiteral = lastTerm.getLiteralVariable();

        if (middleCoefficient.contains(firstTermLiteral) && middleCoefficient.contains(lastTermLiteral)) {
            if (middleCoefficient.charAt(middleCoefficient.indexOf(firstTermLiteral) + 1) == '^') {
                if (firstTerm.getLiteralDegree() % 3 == 0 && lastTerm.getLiteralDegree() % 3 == 0) {
                    int firstPower = firstTerm.getLiteralDegree() / 3;
                    int lastPower = lastTerm.getLiteralDegree() / 3;
                    if ((firstPower * 2) + lastPower == new Monomial(middleCoefficient).getLiteralDegree()) {
                        if (firstTerm.getCoefficient() == 1 && lastTerm.getCoefficient() == 1 && middleCoefficient.startsWith("3"))
                            return true;
                        else if (firstTerm.getCoefficient() != 1 && lastTerm.getCoefficient() != 1) {
                            int firstTermCbrt = (int) Math.cbrt(firstTerm.getCoefficient());
                            int lastTermCbrt = (int) Math.cbrt(lastTerm.getCoefficient());
                            return ((firstTermCbrt * lastTermCbrt) * 3) == middleCoefficient.charAt(0);
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

    private static boolean isVariableAndNumberValid(Monomial monomial, int numberCoefficient,
                                                    ExpandedQuadruple middleQuadruple, boolean isFirstTermALiteral,
                                                    boolean isMiddleTerm1) {
        int monomialCoefficient = (monomial.getCoefficient() == 1) ?
                monomial.getCoefficient() : (int) Math.cbrt(monomial.getCoefficient());

        if ((isMiddleTerm1 && !isFirstTermALiteral) || (!isMiddleTerm1 && isFirstTermALiteral))
            numberCoefficient = (int) Math.pow(numberCoefficient, 2);
        else
            monomialCoefficient = (int) Math.pow(monomialCoefficient, 2);

        Monomial monomialMiddleTerm = new Monomial(middleQuadruple.getArgument1());
        return monomialMiddleTerm.getCoefficient() == (3 * (monomialCoefficient * numberCoefficient));
    }


    private static boolean isCubeDifference(ThreeAddressCode source) {
        ExpandedQuadruple root = source.getRootQuadruple();
        if (root.isMinus()) {
            ExpandedQuadruple lastQuadruple = source.getLastQuadruple(root);
            return lastQuadruple.isMinus();
        }
        return false;
    }

    //</editor-fold>

    //<editor-fold desc="Two Binomial Product">
    public static boolean isTwoBinomialProduct(ThreeAddressCode source) {
        ExpandedQuadruple root = source.getRootQuadruple();

        String rootArgument1 = root.getArgument1();

        if (StringUtil.match(rootArgument1, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple innerQuadruple = source.findQuadrupleByResult(rootArgument1);
            rootArgument1 = innerQuadruple.getArgument1();
        }

        if (StringUtil.match(rootArgument1, RegexPattern.VARIABLE_WITH_EXPONENT.toString())) {

            Monomial argument = new Monomial(rootArgument1);
            if (argument.getCoefficient() != 0 && argument.getLiteralDegree() == 2) {

                if (StringUtil.match(root.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {

                    ExpandedQuadruple innerQuadruple = source.findQuadrupleByResult(root.getArgument2());
                    if (StringUtil.match(innerQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_COEFFICIENT.toString())) {
                        argument = new Monomial(innerQuadruple.getArgument1());
                        if (argument.getCoefficient() != 0 && argument.getLiteralDegree() == 1) {
                            if (StringUtil.match(innerQuadruple.getArgument2(), RegexPattern.NATURAL_NUMBER.toString())) {
                                return !innerQuadruple.getArgument2().equals("0");
                            }
                        }

                    }
                }
            }
        }

        return false;
    }
    //</editor-fold>


}
