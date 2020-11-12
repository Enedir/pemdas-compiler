package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.perfectpolynomial;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.FatorationRuleIdentification;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.Monomial;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FatorationRulePerfectPolynomialExpandedFormulaConversion implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return FatorationRuleIdentification.isPerfectSquareTrinomial(source.get(0)) ||
                FatorationRuleIdentification.isPerfectCube(source.get(0));
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);
        boolean isPerfectSquare = FatorationRuleIdentification.isPerfectSquareTrinomial(source.get(0));
        String sign = this.adjustToExpandedFormula(isPerfectSquare);
        String reason = (isPerfectSquare) ?
                "Escrevemos a expressão no formato &ascr;&sup2; " + sign + " 2 &middot; &ascr; &middot; &bscr; &plus;" +
                        " &bscr;&sup2;, identificando os elementos que estão elevados ao quadrado e os respectivos produtos." :

                "Escrevemos a expressão no formato &ascr;&sup3; " + sign + " 3 &middot; &ascr;&sup2; &middot; &bscr; " +
                        "&plus; 3 &middot; &ascr; &middot; &bscr;&sup2; " + sign + " &bscr;&sup3;," +
                        " identificando os elementos que estão elevados ao cubo, ao quadrado e os respectivos produtos.";

        this.source.clearNonUsedQuadruples();

        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), reason));
        return steps;
    }

    /**
     * Ajusta as quádruplas para a fórmula expandida, que podem ser:
     * <ul>
     *     <li>Trinômio Quadrado Perfeito: a^2 +- 2 * a * b + b^2;</li>
     *     <li>Cubo Perfeito: a^3 +- 3 * a^2 * b + 3 * a * b^2 +- b^3.</li>
     * </ul>
     *
     * @param isPerfectSquare booleano que indica se é um trinomio quadrado perfeito (true), ou cubo perfeito (false).
     * @return operação que irá ser aplicada, podendo ser de soma ou subtração.
     */
    private String adjustToExpandedFormula(boolean isPerfectSquare) {
        /* Para melhorar a explicação serão utilizados esses modelos:
         *      Para trinomio quadrado perfeito: #^2 +- # * # * # + #^2
         *      Para cubo perfeito: #^3 +- # * #^2 * # + # * # * #^2 +- #^3
         *
         * No qual os valores "#" serãosubstituídos a medida que no código os valores serão criados.
         * Por exemplo, em uma determinada linha do código,o valor que representa o "b^2" é feito, então a fórmula
         *  vai passar de  #^2 +- # * # * # + #^2, para #^2 +- # * # * # + b^2.
         * */


        ExpandedQuadruple root = this.source.getRootQuadruple();
        ExpandedQuadruple middleQuadruple = this.source.getRootQuadruple();
        ExpandedQuadruple lastQuadruple = this.source.getLastQuadruple(
                this.source.findQuadrupleByArgument(root.getArgument2()));

        //caso tiver uma quadrupla com "MINUS"
        if (StringUtil.match(root.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            root = this.source.findQuadrupleByResult(root.getArgument1());

        if (isPerfectSquare)
            this.convertSquareMiddleTerm(root, lastQuadruple);
        else
            this.convertCubeMiddleTerm(root, lastQuadruple);

        //as operações abaixo são tanto para caso de trinomio quadrado perfeito quanto para cubo perfeito

        //constrói o argumento b^2 ou b^3:
        // #^2 +- 2 * a * b + b^2
        // #^3 +- 3 * a^2 * b + 3 * a * b^2 +- b^3
        lastQuadruple.setArgument2(this.convertToRaisedValue(lastQuadruple.getArgument2(), isPerfectSquare));

        //obtém o valor elevado (2 ou 3) para colocar no a, ou seja, o a^2 ou a^3
        String raisedArgument;

        //caso for cubo perfeito e quádrupla de MINUS
        if (root.isNegative() && !isPerfectSquare) {
            raisedArgument = this.convertMinusToRaisedArgument(root);

            //ajusta para ficar do tipo (-1)^3
            root = middleQuadruple;
        } else
            raisedArgument = this.convertToRaisedValue(this.source.getRootQuadruple().getArgument1(), isPerfectSquare);

        //constrói o argumento a^2 ou a^3:
        // a^2 +- 2 * a * b + b^2
        // a^3 +- 3 * a^2 * b + 3 * a * b^2 +- b^3
        root.setArgument1(raisedArgument);

        //retorna qual a operação
        return (root.getOperator().equals("+")) ? "&plus;" : "&minus;";
    }

    /**
     * Altera as quádruplas caso algum dos valores (a ou b) seja negativo, para ficar do tipo (-a) ou (-b).
     *
     * @param negativeQuadruple {@link ExpandedQuadruple} que contém a quádrupla do valor a ou b.
     * @return {@link String} que representa o result da quádrupla que foi criado o (-a) ou (-b).
     */
    private String convertMinusToRaisedArgument(ExpandedQuadruple negativeQuadruple) {
        //coloca entre parênteses
        negativeQuadruple.setLevel(1);
        //cria a quadrupla com o e coloca na lista
        ExpandedQuadruple exponentQuadruple = new ExpandedQuadruple("^", negativeQuadruple.getResult(), "3", source.retrieveNextTemporary(), 0, 0);
        source.getExpandedQuadruples().add(exponentQuadruple);

        //retorna o result da quadrupla do tipo (-a)^3 ou (-b)^3
        return exponentQuadruple.getResult();
    }


    /**
     * Converte o {@code argument} para o tipo a^2, a^3, b^2 ou b^3.
     *
     * @param argument        {@link String} que representa o a ou b.
     * @param isPerfectSquare booleano que indica se é um trinomio quadrado perfeito (true), ou cubo perfeito (false).
     * @return o argumento elevado ao seu grau.
     */
    private String convertToRaisedValue(String argument, boolean isPerfectSquare) {
        return changeToRaisedValue(argument, this.source, isPerfectSquare);
    }


    /**
     * Altera o argumento para adicionar o seu expoente respectivo, podendo ser expoente 2 ou 3.
     *
     * @param argument      {@link String} que representa o a ou b.
     * @param source        {@link ThreeAddressCode} que contém todas quádruplas.
     * @param isRaisedByTwo booleano que indica se é elevado a 2 (true) ou 3 (false).
     * @return o argumento elevado ao seu grau.
     */
    public static String changeToRaisedValue(String argument, ThreeAddressCode source, boolean isRaisedByTwo) {
        Monomial monomial = new Monomial(argument);
        int exponent = (isRaisedByTwo) ? 2 : 3;

        //faz a raiz do coeficiente (número) ao quadrado ou ao cubo
        if (monomial.getCoefficient() != 1)
            monomial.setCoefficient((isRaisedByTwo) ? (int) Math.sqrt(monomial.getCoefficient()) : (int) Math.cbrt(monomial.getCoefficient()));

        //se o expoente for 2, irá dividir por 2, como por exemplo a^6: divide 6 por 2. O mesmo se aplica ao expoente 3
        if (monomial.getLiteralDegree() > 1)
            monomial.setLiteralDegree(monomial.getLiteralDegree() / exponent);

        //cria uma quádrupla com parenteses para envolver o argumento a ou b
        ExpandedQuadruple parenthesesQuadruple = new ExpandedQuadruple("", monomial.toString(), "", source.retrieveNextTemporary(), 0, 1);
        source.getExpandedQuadruples().add(parenthesesQuadruple);


        //cria uma quádrupla com o sinal de exponenciação (^) para ficar do tipo a^2, a^3, b^2 ou b^3.
        ExpandedQuadruple exponentQuadruple = new ExpandedQuadruple("^", parenthesesQuadruple.getResult(), String.valueOf(exponent), source.retrieveNextTemporary(), 0, 0);
        source.getExpandedQuadruples().add(exponentQuadruple);

        //retorna o result da quádrupla elevada a potência
        return exponentQuadruple.getResult();
    }

    /**
     * Converte as quádruplas para o trinomio quadrado perfeito.
     *
     * @param quadrupleA {@link ExpandedQuadruple} que contém o valor de a.
     * @param quadrupleB {@link ExpandedQuadruple} que contém o valor de b.
     */
    private void convertSquareMiddleTerm(ExpandedQuadruple quadrupleA, ExpandedQuadruple quadrupleB) {
        //pega o argumento que representa o valor de b
        String sonArgument = this.source.findDirectSonArgument(quadrupleB.getArgument2(), true);

        //constrói o b, que antecede o b^2 : #^2 +- # * # * b + #^2
        quadrupleB.setArgument1(this.getReducedTerm(sonArgument, true));

        sonArgument = this.source.findDirectSonArgument(quadrupleA.getArgument1(), true);

        //constrói o b, entre o 2 e o b:  #^2 +- # * a * b + #^2
        this.source.addQuadrupleToList("*", this.getReducedTerm(sonArgument, true),
                quadrupleB.getResult(), quadrupleA, false);

        //constrói o 2, entre o a^2 e o b :  #^2 +- 2 * a * b + #^2
        this.source.addQuadrupleToList("*", "2", quadrupleA.getArgument2(), quadrupleA, false);
    }

    /**
     * Reduz o argumento de acordo com a regra. Em caso de trinomio quadrado perfeito, será reduzido por 2, e cubo
     * perfeito por 3. Os argumentos serão convertidos em {@link Monomial} que seus valores serão alterados da seguinte
     * forma:
     *
     * <ul>
     *     <li>Coeficiente: para trinomio quadrado será feita a raiz quadrada do número e em cubo será feita a
     *     raiz cúbica.</li>
     *     <li>Literal: para trinomio quadrado o expoente será dividido por 2 e em cubo dividido por 3.</li>
     * </ul>
     *
     * @param argument        {@link String} que é o argumento a ser reduzido.
     * @param isPerfectSquare booleano que indica se é um trinomio quadrado perfeito (true), ou cubo perfeito (false).
     * @return {@link String} do argumento reduzido.
     */
    private String getReducedTerm(String argument, boolean isPerfectSquare) {

        //Se for somente um número
        if (StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString()))
            return String.valueOf((isPerfectSquare) ?
                    (int) Math.sqrt(Integer.parseInt(argument)) :
                    (int) Math.cbrt(Integer.parseInt(argument)));

        Monomial monomial = new Monomial(argument);
        int exponent = (isPerfectSquare) ? 2 : 3;

        //ajusta o grau da parte literal
        monomial.setLiteralDegree(monomial.getLiteralDegree() / exponent);

        //ajusta o coeficiente
        if (monomial.getCoefficient() != 1)
            monomial.setCoefficient((isPerfectSquare) ? (int) Math.sqrt(monomial.getCoefficient()) : (int) Math.cbrt(monomial.getCoefficient()));

        return monomial.toString();
    }

    /**
     * Converte as quádruplas para o cubo perfeito.
     *
     * @param quadrupleA {@link ExpandedQuadruple} que contém o valor de a.
     * @param quadrupleB {@link ExpandedQuadruple} que contém o valor de b.
     */
    private void convertCubeMiddleTerm(ExpandedQuadruple quadrupleA, ExpandedQuadruple quadrupleB) {

        String sonArgument = this.source.findDirectSonArgument(quadrupleA.getArgument1(), true);
        String firstArgumentValue = this.getReducedTerm(sonArgument, false);

        //caso o valor de a seja negativo, terá um quadrupla "MINUS" que tem de ser tratada
        if (quadrupleA.isNegative()) {
            quadrupleA.setArgument1(this.getReducedTerm(quadrupleA.getArgument1(), false));
            firstArgumentValue = quadrupleA.getResult();
            quadrupleA = this.source.findQuadrupleByArgument(quadrupleA.getResult());
        }

        sonArgument = this.source.findDirectSonArgument(quadrupleB.getArgument2(), true);
        String secondArgumentValue = this.getReducedTerm(sonArgument, false);

        //constrói o b, que antecede o b^3 : #^3 +- # * #^2 * # + # * # * b^2 +- #^3
        quadrupleB.setArgument1("(" + secondArgumentValue + ")^2");

        //constrói o a, entre o 3 e o b^2 : #^3 +- # * #^2 * # + # * a * b^2 +- #^3
        this.source.addQuadrupleToList("*", firstArgumentValue, quadrupleB.getResult(), quadrupleA, false);

        //constrói o 3,entre b e o a : #^3 +- # * #^2 * # + 3 * a * b^2 +- #^3
        this.source.addQuadrupleToList("*", "3", quadrupleA.getArgument2(), quadrupleA, false);

        //constrói o b,entre a^2 e o 3 : #^3 +- # * #^2 * b + 3 * a * b^2 +- #^3
        this.source.addQuadrupleToList("+", secondArgumentValue, quadrupleA.getArgument2(), quadrupleA, false);


        //o if é para verificar se o a for negativo, para ajustar corretamente
        String argument1;
        if (StringUtil.match(firstArgumentValue, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple addedQuadruple = this.source.addQuadrupleToList("^", firstArgumentValue, "2", quadrupleA, true);
            argument1 = addedQuadruple.getResult();
        } else
            argument1 = "(" + firstArgumentValue + ")^2";

        //constrói o a,entre a^3 e o b : #^3 +- # * a^2 * b + 3 * a * b^2 +- #^3
        this.source.addQuadrupleToList("*", argument1, quadrupleA.getArgument2(), quadrupleA, false);

        //constrói o a,entre a^3 e o b : #^3 +- 3 * a^2 * b + 3 * a * b^2 +- #^3
        this.source.addQuadrupleToList("*", "3", quadrupleA.getArgument2(), quadrupleA, false);
    }
}
