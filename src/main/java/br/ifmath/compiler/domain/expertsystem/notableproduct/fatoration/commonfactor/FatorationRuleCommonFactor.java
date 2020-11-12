package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.commonfactor;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.FatorationRuleIdentification;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.Monomial;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.MathOperatorUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FatorationRuleCommonFactor implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return FatorationRuleIdentification.isCommonFactor(source.get(0).getRootQuadruple(), source.get(0));
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);
        String commonFactor = this.getCommonFactor();
        if (!commonFactor.isEmpty())
            this.groupCommonFactor(commonFactor);

        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Verificamos o elemento que temos em comum e colocamos em evidência."));
        return steps;
    }

    //<editor-fold desc="getCommonFactor">

    /**
     * Obtém o fator comum entre os argumentos.
     *
     * @return {@link String} que representa o fator (número, literal ou monômio) que é comum a todos os argumentos.
     */

    private String getCommonFactor() {
        String rootArgument1 = this.getFirstArgument(this.source.getRootQuadruple());

        //Monômio inicial no qual será comparado com os outros para obter o fator em comum
        Monomial initialMonomial = new Monomial(rootArgument1);

        int coefficient = this.getCommonCoefficient(this.source.getRootQuadruple(), initialMonomial.getCoefficient());
        String literal = this.getCommonLiteral(this.source.getRootQuadruple(), new Monomial(initialMonomial.getLiteral()));

        return new Monomial(literal, coefficient).toString();
    }

    /**
     * Obtém o primeiro argumento válido das quádruplas.
     *
     * @param iterationQuadruple {@link ExpandedQuadruple} que representa qual a quádrupla inicial a procurar
     *                           pelo argumento.
     * @return {@link String} que é o primeiro argumento válido das quádruplas, ou seja, um argumento que não seja
     * uma quádrupla temporária.
     */
    private String getFirstArgument(ExpandedQuadruple iterationQuadruple) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getFirstArgument(source.findQuadrupleByResult(iterationQuadruple.getArgument1()));
        }
        return iterationQuadruple.getArgument1();
    }

    /**
     * Obtém o coeficiente em comum entre os argumentos, se houver algum.
     *
     * @param iterationQuadruple {@link ExpandedQuadruple} que representa qual a quádrupla inicial a procurar
     *                           pelo coeficiente.
     * @param coefficient        valor inteiro que representa o coeficiente inicial e que será comparado a todos os outros
     *                           coeficientes.
     * @return valor inteiro com o coeficiente em comum. Caso não houver um coeficiente em comum, será considerado 1.
     */
    private int getCommonCoefficient(ExpandedQuadruple iterationQuadruple, int coefficient) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getCommonCoefficient(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), coefficient);
        }

        Monomial monomial = new Monomial(iterationQuadruple.getArgument1());

        //compara o coeficiente atual com os outros através do maior divisor comum (gcd)
        coefficient = (int) MathOperatorUtil.gcd(coefficient, monomial.getCoefficient());
        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getCommonCoefficient(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), coefficient);
        }

        monomial.setAttributesFromString(iterationQuadruple.getArgument2());

        coefficient = (int) MathOperatorUtil.gcd(coefficient, monomial.getCoefficient());
        return coefficient;
    }

    /**
     * Obtém a parte literal em comum entre os argumentos, se houver algum.
     *
     * @param iterationQuadruple {@link ExpandedQuadruple} que representa qual a quádrupla inicial a procurar
     *                           pelo coeficiente.
     * @param literal            {@link Monomial} que representa somente a parte literal inicial e que será comparado a
     *                           todos os outros literais.
     * @return {@link String} com a parte literal em comum. Caso não houver um coeficiente em comum, será considerado "".
     */
    private String getCommonLiteral(ExpandedQuadruple iterationQuadruple, Monomial literal) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getCommonLiteral(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), literal);
        }

        Monomial monomial = new Monomial(iterationQuadruple.getArgument1());

        literal = this.getLowestDegreeLiteral(monomial, literal);
        if (literal.toString().isEmpty())
            return literal.toString();

        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getCommonLiteral(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), literal);
        }

        monomial = new Monomial(iterationQuadruple.getArgument2());

        literal = this.getLowestDegreeLiteral(monomial, literal);

        return literal.toString();
    }

    /**
     * Obtém a parte literal com menor expoente não-zero dentre dois literais.
     *
     * @param firstLiteral  {@link Monomial} que representa o primeiro dos literais a ser comparado.
     * @param secondLiteral {@link Monomial} que representa o segundo dos literais a ser comparado.
     * @return {@link Monomial} com menor grau entre o {@code firstLiteral} e {@code secondLiteral}.
     */
    private Monomial getLowestDegreeLiteral(Monomial firstLiteral, Monomial secondLiteral) {

        //caso algum dos dois seja vazio ou uma parte literal seja diferente da outra (comparação de x com y por exemplo)
        if (firstLiteral.getLiteral().isEmpty() || secondLiteral.getLiteral().isEmpty() ||
                !firstLiteral.getLiteralVariable().equals(secondLiteral.getLiteralVariable()))
            return new Monomial();

        return (firstLiteral.getLiteralDegree() < secondLiteral.getLiteralDegree()) ?
                new Monomial(firstLiteral.getLiteral()) :
                new Monomial(secondLiteral.getLiteral());
    }
    //</editor-fold>>

    //<editor-fold desc="groupCommonFactor">

    /**
     * Agrupa os argumentos de acordo com o fator em comum.
     *
     * @param commonFactor {@link String} que é o fator em comum entre todos os argumentos das quádruplas.
     */
    private void groupCommonFactor(String commonFactor) {
        this.removeCommonFactor(this.source.getRootQuadruple(), commonFactor);
        this.surroundQuadruplesWithParentheses(this.source.getLeft());
        ExpandedQuadruple newRoot = new ExpandedQuadruple("*", commonFactor, this.source.getLeft(), this.source.retrieveNextTemporary(), 0, 0);
        this.source.getExpandedQuadruples().add(newRoot);
        this.source.setLeft(newRoot.getResult());
    }

    /**
     * Remove o {@code commonFactor} de todas as quádruplas
     *
     * @param iterationQuadruple {@link ExpandedQuadruple} que representa qual a quádrupla inicial a procurar
     *                           pelo argumento.
     * @param commonFactor       {@link String} que é o fator em comum entre todos os argumentos das quádruplas.
     */
    private void removeCommonFactor(ExpandedQuadruple iterationQuadruple, String commonFactor) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.removeCommonFactor(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), commonFactor);
            return;
        }

        this.adjustTermsByFactor(iterationQuadruple, commonFactor, true);

        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());


        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.removeCommonFactor(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), commonFactor);
            return;
        }

        this.adjustTermsByFactor(iterationQuadruple, commonFactor, false);
    }

    /**
     * Ajusta os argumentos para serem "reduzidos" pelo {@code commonFactor}, ou seja, para que sejam transformados
     * em valores equivalentes. Exs.: 3 * (6 + 9), sendo que 3 é o fator em comum, então transformará os valores
     * entre parênteses, resultando em: 3 * (2 + 3). O mesmo se aplica as partes literais como 2x * (4x^2 - 2x), que
     * será tranformado para: 2x * (2x - 1).
     *
     * @param argumentQuadruple {@link ExpandedQuadruple} que representa qual a quádrupla inicial a procurar
     *                          pelo argumento.
     * @param commonFactor      {@link String} que é o fator em comum entre todos os argumentos das quádruplas.
     * @param isArgument1       booleano que indica se o primeiro ou o segundo argumento da quádrupla é que será tranformado.
     */
    private void adjustTermsByFactor(ExpandedQuadruple argumentQuadruple, String commonFactor, boolean isArgument1) {

        //obtém o argumento correto
        String argument = (isArgument1) ? argumentQuadruple.getArgument1() : argumentQuadruple.getArgument2();

        //se for uma quádrupla temporária, então é obtém a quádrupla interior (trata cenários de quádruplas com MINUS)
        if (StringUtil.match(argument, RegexPattern.TEMPORARY_VARIABLE.toString()))
            argument = this.source.findQuadrupleByResult(argument).getArgument1();

        //variável que representa o novo argumento que será incluído
        String newArgument;

        //caso o fator em comum for um monômio com grau 1 (Ex.: x, 2x, 5x,etc.) que está presente no argumento sendo analisado
        if (StringUtil.match(commonFactor, RegexPattern.VARIABLE.toString()) && argument.contains(commonFactor)) {

            //se forem exatamente iguais, o novo valor será 1. Como na situação x * (x + x), que ficará: x * (1 + 1)
            if (argument.equals(commonFactor))
                newArgument = "1";

                //se o argumento sendo analisado for um monômio completo com coeficiente e parte literal (Ex.: 2x^2, 5y^5, etc.)
            else if (StringUtil.match(argument, RegexPattern.VARIABLE_WITH_COEFFICIENT.toString()))
                newArgument = argument.replace(commonFactor, "");

                //se o argumento é um monômio sem o coeficiente (Ex.: x, i, t^3, u^2,etc.)
            else {
                Monomial monomial = new Monomial(argument);
                monomial.setLiteralDegree(monomial.getLiteralDegree() - 1);
                newArgument = monomial.toString();
            }

            //caso o fator em comum for um coeficiente (Ex.: 2, 54, 12,etc.)
        } else if (StringUtil.match(commonFactor, RegexPattern.NATURAL_NUMBER.toString())) {
            int commonFactorValue = Integer.parseInt(commonFactor);

            Monomial monomial = new Monomial(argument);

            int iterationCoefficient = monomial.getCoefficient();

            //se o argumento e fator em comum forem divisíveis (sem resto e quociente sendo um valor inteiro)
            if (iterationCoefficient % commonFactorValue == 0)
                monomial.setCoefficient(iterationCoefficient / commonFactorValue);

            newArgument = monomial.toString();

            //caso o fator em comum for um monômio com grau diferente de 1 (Ex.: 3x^2, x^7, 4a^2,etc.)
        } else {
            Monomial argumentMonomial = new Monomial(argument);
            Monomial monomialFactor = new Monomial(commonFactor);

            //caso os literais do argumento e do fator em comum forem iguais, retira-o do argumento. Senão diminui
            // o grau do argumento pelo grau do fator em comum
            if (argumentMonomial.getLiteral().equals(monomialFactor.getLiteral()))
                argumentMonomial.setLiteral("");
            else
                argumentMonomial.setLiteralDegree(argumentMonomial.getLiteralDegree() - monomialFactor.getLiteralDegree());

            //divide o coeficiente do argumento pelo do fator em comum
            argumentMonomial.setCoefficient(argumentMonomial.getCoefficient() / monomialFactor.getCoefficient());

            //o toString irá representar o monômio como o vemos, com seu coeficiente seguido de sua parte literal
            newArgument = argumentMonomial.toString();
        }

        //ajusta o argumento correto
        if (isArgument1)
            argumentQuadruple.setArgument1(newArgument);
        else
            argumentQuadruple.setArgument2(newArgument);
    }

    /**
     * Adiciona quádruplas para representarem uma {@link String} entre parênteses (nível igual a 1) a todas as
     * quadruplas a partir do {@code argument}.
     *
     * @param argument {@link String} que representa qual o result da quádrupla inicial a ser envolta em
     *                 parênteses.
     */
    private void surroundQuadruplesWithParentheses(String argument) {
        ExpandedQuadruple iterationQuadruple = this.source.findQuadrupleByResult(argument);
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.surroundQuadruplesWithParentheses(source.findQuadrupleByResult(iterationQuadruple.getArgument1()).getResult());
        }

        iterationQuadruple.setLevel(1);

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.surroundQuadruplesWithParentheses(source.findQuadrupleByResult(iterationQuadruple.getArgument2()).getResult());
        }
    }
    //</editor-fold>>

}
