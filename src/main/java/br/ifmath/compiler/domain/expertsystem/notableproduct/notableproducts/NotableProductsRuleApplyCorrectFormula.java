package br.ifmath.compiler.domain.expertsystem.notableproduct.notableproducts;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class NotableProductsRuleApplyCorrectFormula implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        //Sempre aplicará a fórmula de um dos tipos de produto notável
        return true;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);

        String explanation = this.applyFormula();

        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(),
                "Aplicando o produto notável fazendo o " + explanation));
        return steps;
    }

    /**
     * Aplica a fórmula respectiva a que foi identificada
     *
     * @return {@link String} que contém a explicação da fórmula aplicada
     */
    private String applyFormula() {
        //Variável que irá receber a explicação da regra aplicada
        String rule = "";
        ExpandedQuadruple root = this.source.getRootQuadruple();

        if (StringUtil.match(root.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            if (root.isPotentiation()) {

                ExpandedQuadruple innerQuadruple = this.source.findQuadrupleByResult(root.getArgument1());

                if (!StringUtil.match(innerQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {

                    //Potência de dois termos
                    rule = this.twoTermsPower(root, innerQuadruple);
                }

                //Verificação do produto da soma pela diferença, com a fórmula (a + b) * (a - b)
            } else if (root.isTimes() &&
                    StringUtil.match(root.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {

                ExpandedQuadruple leftInnerQuadruple = this.source.findQuadrupleByResult(root.getArgument1());
                ExpandedQuadruple rightInnerQuadruple = this.source.findQuadrupleByResult(root.getArgument2());

                String firstArgument = leftInnerQuadruple.getArgument1();
                String secondArgument = leftInnerQuadruple.getArgument2();

                //Verifica se os termos b são iguais
                if (secondArgument.equals(rightInnerQuadruple.getArgument2())) {
                    if (isThereAMonomial(leftInnerQuadruple, true)) {

                        //cria parênteses para o termo b
                        this.createParentheses(firstArgument, this.source.getListLastQuadruple(), true);
                        firstArgument = root.getArgument1();

                        if (isThereAMonomial(leftInnerQuadruple, false)) {

                            //cria quadrupla para o termo a
                            this.createParentheses(secondArgument, this.source.getListLastQuadruple(), false);

                            String lastQuadrupleResult = this.source.getListLastQuadruple().getResult();
                            //ajusta os results das quadruplas caso tenham ficado em desordem
                            if (!root.getArgument2().equals(lastQuadrupleResult)) {
                                ExpandedQuadruple incorrectQuadruple = this.source.findQuadrupleByArgument(lastQuadrupleResult);
                                incorrectQuadruple.setArgument2("");
                                root.setArgument2(lastQuadrupleResult);
                            }
                            secondArgument = root.getArgument2();
                        }
                    }

                    //Produto da soma pela diferença
                    rule = this.productOfSumAndDif(root, firstArgument, secondArgument);
                }
            }
        }
        return rule;
    }

    /**
     * Verifica se o argumento é um monômio.
     *
     * @param expandedQuadruple {@link ExpandedQuadruple} que será verificado o argumento.
     * @param isArgument1       {@link Boolean} que indica qual argumento será verificado.
     * @return {@code true} caso seja um monômio, e {@code false} caso contrário.
     */
    private boolean isThereAMonomial(ExpandedQuadruple expandedQuadruple, boolean isArgument1) {
        String argument = (isArgument1) ? expandedQuadruple.getArgument1() : expandedQuadruple.getArgument2();
        return StringUtil.match(argument, RegexPattern.VARIABLE_WITH_COEFFICIENT.toString());
    }


    /**
     * Aplicação da fórmula da soma pela diferença de dois termos, sendo ela: (a)^2 - (b)^2.
     *
     * @param rootQuadruple {@link ExpandedQuadruple} que representa a primeira quádrupla.
     * @param firstTerm     {@link String} que representa o result da quadruple contendo o termo a.
     * @param secondTerm    {@link String} que representa o result da quadruple contendo o termo b.
     * @return Explicação da aplicação da fórmula.
     */
    private String productOfSumAndDif(ExpandedQuadruple rootQuadruple, String firstTerm, String secondTerm) {
        this.setCorrectArgument(firstTerm, rootQuadruple, true);
        rootQuadruple.setOperator("-");
        this.setCorrectArgument(secondTerm, rootQuadruple, false);
        return "quadrado do primeiro termo, menos o quadrado do segundo termo.";
    }

    /**
     * Adiciona os termos a e b aos argumentos corretos da primeira quádrupla.
     *
     * @param argument      {@link String} termo que será adicionado.
     * @param rootQuadruple {@link ExpandedQuadruple} que representa a primeira quádrupla.
     * @param isArgument1   {@link Boolean} que indica qual o argumento a ser adicionado.
     */
    private void setCorrectArgument(String argument, ExpandedQuadruple rootQuadruple, boolean isArgument1) {
        //se o termo já estiver entre parênteses, somente cria a potência ao quadrado
        if (StringUtil.match(argument, RegexPattern.TEMPORARY_VARIABLE.toString()))
            this.source.addQuadrupleToList("^", argument, "2", rootQuadruple, isArgument1);
        else {
            //caso não tiver entre parênteses, os cria e eleva ao quadrado
            argument = this.createParentheses(argument, rootQuadruple, isArgument1).getResult();
            argument = this.source.addQuadrupleToList("^", argument, "2", rootQuadruple, isArgument1).getResult();
            if (isArgument1) {
                rootQuadruple.setArgument1(argument);
            } else {
                rootQuadruple.setArgument2(argument);
            }
        }
    }

    /**
     * Aplica a fórmula da potência de dois termos, podendo ser o quadrado, cubo, soma e subtração dos termos.
     *
     * @param root           {@link ExpandedQuadruple} que representa a primeira quádrupla.
     * @param innerQuadruple {@link ExpandedQuadruple} que contém os termos que serão elevados.
     * @return {@link String} explicando a fórmula aplicada respectiva.
     */
    private String twoTermsPower(ExpandedQuadruple root, ExpandedQuadruple innerQuadruple) {
        String sign = innerQuadruple.isPlus() ? "mais" : "menos";

        String explanation = "";
        String exponent = "";
        if (root.getArgument2().equals("2")) {

            exponent = "2";
            explanation = this.twoTermsSquare(root, innerQuadruple, sign);

        } else if (root.getArgument2().equals("3")) {

            exponent = "3";
            explanation = this.twoTermsCube(root, innerQuadruple, sign);

        }
        root.setOperator(innerQuadruple.isPlus() ? "+" : "-");

        this.source.addQuadrupleToList("^", innerQuadruple.getArgument1(), exponent, root, true);
        ExpandedQuadruple lastQuadruple = this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1);
        if (!StringUtil.match(lastQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            this.createParentheses(lastQuadruple.getArgument1(), lastQuadruple, true);

        return explanation;
    }

    /**
     * Aplicação da fórmula do quadrado de dois termos, sendo ela: (a)^2 +- 2 * a * b + (b)^2
     *
     * @param rootQuadruple  {@link ExpandedQuadruple} que representa a primeira quádrupla.
     * @param termsQuadruple {@link ExpandedQuadruple} que contém os termos que serão elevados ao quadrado.
     * @param sign           {@link String} que representa a soma ou diferença entre os termos.
     * @return Explicação da aplicação da fórmula.
     */
    private String twoTermsSquare(ExpandedQuadruple rootQuadruple, ExpandedQuadruple termsQuadruple, String sign) {
        //É feita atribuição de forma mais direta já que é a aplicação de uma fórmula, ou seja, tem uma estrutura rígida

        //cria a quadrupla b^2
        this.source.addQuadrupleToList("^", termsQuadruple.getArgument2(), "2", rootQuadruple, false);

        //cria um parênteses para o termo b, da quadrupla acima
        ExpandedQuadruple lastQuadruple = this.source.getListLastQuadruple();
        this.createParentheses(lastQuadruple.getArgument1(), lastQuadruple, true);

        //cria a quadrupla b + (b)^2
        this.source.addQuadrupleToList("+", termsQuadruple.getArgument2(), rootQuadruple.getArgument2(), rootQuadruple, false);

        //cria a quadrupla a * b
        this.source.addQuadrupleToList("*", termsQuadruple.getArgument1(), rootQuadruple.getArgument2(), rootQuadruple, false);

        //cria a quadrupla 2 * a
        this.source.addQuadrupleToList("*", "2", rootQuadruple.getArgument2(), rootQuadruple, false);
        return "quadrado do primeiro termo, " + sign + " o dobro do produto " +
                "do primeiro pelo segundo termo, mais o quadrado do segundo termo.";
    }

    /**
     * Cria parênteses em volta de um argumento.
     *
     * @param argument       Argumento que será envolto em um parênteses.
     * @param quadruple      {@link ExpandedQuadruple} que está o {@code argument}.
     * @param setOnArgument1 {@link Boolean} que indica em qual argumento da quádrupla será inserido os parênteses
     * @return {@link ExpandedQuadruple} com o argumento entre parênteses.
     */
    private ExpandedQuadruple createParentheses(String argument, ExpandedQuadruple quadruple, boolean setOnArgument1) {
        //adiciona uma quadrupla nova somente com o argumento a ser envolto
        ExpandedQuadruple lastQuadruple = this.source.addQuadrupleToList("", argument, "", quadruple, setOnArgument1);

        //adiciona os parênteses
        lastQuadruple.setLevel(1);
        return lastQuadruple;
    }

    /**
     * Aplicação da fórmula do cubo de dois termos, sendo ela: (a)^3 +- 3 * a^2 * b + 3 * a * b^2 +- (b)^3
     *
     * @param rootQuadruple  {@link ExpandedQuadruple} que representa a primeira quádrupla.
     * @param termsQuadruple {@link ExpandedQuadruple} que contém os termos a e b.
     * @param sign           {@link String} que representa a soma ou diferença.
     * @return Explicação da aplicação da fórmula.
     */
    private String twoTermsCube(ExpandedQuadruple rootQuadruple, ExpandedQuadruple termsQuadruple, String sign) {

        String lastOperator = (termsQuadruple.isPlus()) ? "+" : "-";

        //cria a quadrupla b^3 e coloca os parênteses em volta do b
        this.source.addQuadrupleToList("^", termsQuadruple.getArgument2(), "3", rootQuadruple, false);
        ExpandedQuadruple lastQuadruple = this.source.getListLastQuadruple();
        String parenthesesQuadrupleResult = this.createParentheses(lastQuadruple.getArgument1(), lastQuadruple, true).getResult();

        //cria a quadrupla b^2 + b^3
        String powerThreeQuadrupleResult = this.source.findQuadrupleByArgument(parenthesesQuadrupleResult).getResult();
        String powerTwoQuadrupleResult = this.source.addQuadrupleToList("^", parenthesesQuadrupleResult, "2", rootQuadruple, false).getResult();
        this.source.addQuadrupleToList(lastOperator, powerTwoQuadrupleResult, powerThreeQuadrupleResult, rootQuadruple, false);

        //cria a quadrupla a * b^2
        this.source.addQuadrupleToList("*", termsQuadruple.getArgument1(), rootQuadruple.getArgument2(), rootQuadruple, false);

        //cria a quadrupla 3 * a
        this.source.addQuadrupleToList("*", "3", rootQuadruple.getArgument2(), rootQuadruple, false);

        //cria a quadrupla b + 3
        this.source.addQuadrupleToList("+", termsQuadruple.getArgument2(), rootQuadruple.getArgument2(), rootQuadruple, false);

        //cria a quadrupla a^2 e salva os results das quadruplas
        ExpandedQuadruple powerQuadruple;
        String argument2QuadrupleResult = this.source.getListLastQuadruple().getResult();
        String argument1QuadrupleResult = this.source
                .addQuadrupleToList("^", termsQuadruple.getArgument1(), "2", rootQuadruple, false)
                .getResult();

        lastQuadruple = this.source.getListLastQuadruple();

        //adiciona o parênteses em volta do termo a caso necessário
        if (!StringUtil.match(lastQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            this.createParentheses(lastQuadruple.getArgument1(), lastQuadruple, true);

        //cria a quadrupla a^2 * b
        powerQuadruple = this.source.addQuadrupleToList("*", argument1QuadrupleResult, argument2QuadrupleResult, rootQuadruple, false);

        //caso o termo seja negativo, adiciona a potência ao quadrado
        if (StringUtil.match(termsQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            this.source.addQuadrupleToList("^", termsQuadruple.getArgument1(), "2", powerQuadruple, true);

        //cria a quadrupla 3 * a^2
        this.source.addQuadrupleToList("*", "3", rootQuadruple.getArgument2(), rootQuadruple, false);
        return "cubo do primeiro termo, " + sign + " o triplo do produto do " +
                "quadrado do primeiro termo pelo segundo termo, mais o triplo do produto do primeiro pelo " +
                "quadrado do segundo termo, " + sign + " o cubo do segundo termo.";
    }
}
