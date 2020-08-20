package br.ifmath.compiler.domain.compiler;

import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ThreeAddressCode {

    private String left;
    private String comparison;
    private String right;
    private List<ExpandedQuadruple> expandedQuadruples;

    public ThreeAddressCode() {
    }

    public ThreeAddressCode(String left, String comparison, String right, List<ExpandedQuadruple> expandedQuadruples) {
        this.left = left;
        this.comparison = comparison;
        this.right = right;
        this.expandedQuadruples = expandedQuadruples;
    }

    public ThreeAddressCode(String left, List<ExpandedQuadruple> expandedQuadruples) {
        this.left = left;
        this.comparison = "";
        this.right = "";
        this.expandedQuadruples = expandedQuadruples;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getComparison() {
        return comparison;
    }

    public void setComparison(String comparison) {
        this.comparison = comparison;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public List<ExpandedQuadruple> getExpandedQuadruples() {
        return expandedQuadruples;
    }

    public void setExpandedQuadruples(List<ExpandedQuadruple> expandedQuadruples) {
        this.expandedQuadruples = expandedQuadruples;
    }

    /**
     * Obtem a primeira quadrupla "raiz" da estrutura, nao necessariamente a primeira quadrupla da lista.
     *
     * @return {@link ExpandedQuadruple} raiz.
     */
    public ExpandedQuadruple getRootQuadruple() {
        return this.findQuadrupleByResult(left);
    }

    /**
     * Obtem a ultima quadrupla da lista de quadrupla
     *
     * @return {@link ExpandedQuadruple} na ultima posicao da {@link List}.
     */
    public ExpandedQuadruple getLastQuadruple() {
        return this.getExpandedQuadruples().get(getExpandedQuadruples().size() - 1);
    }

    /**
     * Remove todas as quadruplas que não estão sendo usadas, ou seja, remove as quadruplas que não
     * são utilizadas por outras quadruplas
     */
    public void clearNonUsedQuadruples() {
        //Valor que representa se todas as quadruplas não usadas já foram retiradas
        boolean hasRemovedAllQuadruples = false;

        //Indice para iterar na lista de quádruplas
        int i = 0;

        while (!hasRemovedAllQuadruples) {
            ExpandedQuadruple expandedQuadruple = this.getExpandedQuadruples().get(i);
            /*verifica caso não seja a quadrupla presente no atributo "left" e
             e se não há alguma quadrupla que aponte para esta*/
            if (!expandedQuadruple.getResult().equals(this.getLeft())
                    && this.findQuadrupleByArgument(expandedQuadruple.getResult()) == null) {
                this.getExpandedQuadruples().remove(i);

                //Volta o indice para zero, para iterar desde o começo a lista
                i = 0;
            } else {
                /*condição de parada, que é caso tenha iterado por toda a lista, e não
                removeu nenhum elemento*/
                if (i == this.getExpandedQuadruples().size() - 1) {
                    hasRemovedAllQuadruples = true;
                }
                i++;

            }
        }
    }

    public ExpandedQuadruple findQuadrupleByResult(String result) {
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.getResult().equals(result)) {
                return expandedQuadruple;
            }
        }

        return null;
    }

    public List<ExpandedQuadruple> findAllQuadruplesByResultOrArgument(String param) {
        List<ExpandedQuadruple> quadruples = new ArrayList<>();

        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.getResult().equals(param)
                    || expandedQuadruple.isArgument1(param)
                    || expandedQuadruple.isArgument2(param)) {
                quadruples.add(expandedQuadruple);
            }
        }

        return quadruples;
    }

    public ExpandedQuadruple findQuadrupleByArgument(String param) {
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.isArgument1(param)
                    || expandedQuadruple.isArgument2(param)) {
                return expandedQuadruple;
            }
        }

        return null;
    }

    public ExpandedQuadruple findQuadrupleByArgument1(String param) {
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.isArgument1(param)) {
                return expandedQuadruple;
            }
        }

        return null;
    }

    public ExpandedQuadruple findQuadrupleByArgument2(String param) {
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.isArgument2(param)) {
                return expandedQuadruple;
            }
        }

        return null;
    }

    public String findNonEmptyArgs(String result) {
        ExpandedQuadruple expandedQuadruple = this.findQuadrupleByResult(result);

        if (expandedQuadruple.getArgument1().isEmpty() && !(expandedQuadruple.getArgument2().isEmpty())) {
            return expandedQuadruple.getArgument2();
        }
        if (expandedQuadruple.getArgument2().isEmpty() && !(expandedQuadruple.getArgument1().isEmpty())) {
            return expandedQuadruple.getArgument1();
        }
        if (!(expandedQuadruple.getArgument1().isEmpty() && expandedQuadruple.getArgument2().isEmpty())) {
            return result;
        }
        return "";

    }

    public List<ExpandedQuadruple> getDerivatedOperationsFromParent(String param) {
        ExpandedQuadruple parent = findQuadrupleByResult(param);

        List<ExpandedQuadruple> expandedQuadruples = new ArrayList<>();

        if (parent != null) {
            generateDependencyThree(parent.getArgument1(), expandedQuadruples, parent.getLevel());
            generateDependencyThree(parent.getArgument2(), expandedQuadruples, parent.getLevel());
        }

        return expandedQuadruples;
    }

    private void generateDependencyThree(String param, List<ExpandedQuadruple> expandedQuadruples, int level) {
        ExpandedQuadruple expandedQuadruple = findQuadrupleByResult(param);

        if (expandedQuadruple != null && (level + 1) == expandedQuadruple.getLevel()) {
            expandedQuadruples.add(expandedQuadruple);
            generateDependencyThree(expandedQuadruple.getArgument1(), expandedQuadruples, level);
            generateDependencyThree(expandedQuadruple.getArgument2(), expandedQuadruples, level);
        }
    }

    public List<ExpandedQuadruple> getOperationsFromLeft() {
        List<ExpandedQuadruple> expandedQuadruples = new ArrayList<>();

        generateDependencyThree(left, expandedQuadruples);

        return expandedQuadruples;
    }

    public List<ExpandedQuadruple> getOperationsFromRight() {
        List<ExpandedQuadruple> expandedQuadruples = new ArrayList<>();

        generateDependencyThree(right, expandedQuadruples);

        return expandedQuadruples;
    }


    private void generateDependencyThree(String param, List<ExpandedQuadruple> expandedQuadruples) {
        ExpandedQuadruple expandedQuadruple = findQuadrupleByResult(param);

        if (expandedQuadruple != null) {
            expandedQuadruples.add(expandedQuadruple);
            generateDependencyThree(expandedQuadruple.getArgument1(), expandedQuadruples);
            generateDependencyThree(expandedQuadruple.getArgument2(), expandedQuadruples);
        }
    }

    public String toLaTeXNotation() {
        return String.format("%s %s %s",
                generateLaTeXNotation(left, 0, new StringBuilder()).toString(),
                comparison,
                generateLaTeXNotation(right, 0, new StringBuilder()).toString());
    }

    /**
     * Substitui o valor de um argumento ou operador de uma quadrupla, pelo valor de um dado argumento ou operador do seu filho. Ex.:
     * A = B + 1 e B = 2 + 3. Escolhendo B como son, 1 como o argument, entao obterá o valor do argument1 de B,
     * e substituirá onde B aparecer, resultando em: A = 2 + 1.
     *
     * @param son      {@link ExpandedQuadruple} de onde será obtido o valor a ser substituído.
     * @param argument int que pode ser: <ul> <li> 0, caso deseje obter o operator do parâmetro son;</li>
     *                 <li> 1, caso deseje obter o argument1 do parâmetro son;</li>
     *                 <li>2, caso deseje obter o argument2 do parâmetro son.</li></ul>
     */
    public void replaceFatherArgumentForSons(ExpandedQuadruple son, int argument) {
        String newArgument;
        if (argument == 1) {
            newArgument = son.getArgument1();
        } else if (argument == 2) {
            newArgument = son.getArgument2();
        } else {
            newArgument = son.getOperator();
        }

        ExpandedQuadruple father = this.findQuadrupleByArgument(son.getResult());
        if (argument == 0) {
            if (newArgument.equals("MINUS")) {
                father.setOperator("-");
            } else {
                father.setOperator(newArgument);
            }
        } else {
            if (father.getArgument1().equals(son.getResult())) {
                father.setArgument1(newArgument);
            } else {
                father.setArgument2(newArgument);
            }
        }
    }

    /**
     * Adiciona uma nova quadrupla ao final da lista de quadruplas, e a adiciona no argumento correto
     * da {@code quadruple}.
     *
     * @param operator          {@link String} que representa o operador da nova quadrupla
     * @param argument1         {@link String} que representa o argument1 da nova quadrupla
     * @param argument2         {@link String} que representa o argument2 da nova quadrupla
     * @param quadruple         {@link ExpandedQuadruple} que um dos argumentos se tornará a nova quadrupla
     * @param setNewOnArgument1 {@link Boolean} que indica qual argumento da {@code quadruple} será substituída
     * @return Nova {@link ExpandedQuadruple} que foi adicionada a lista
     */
    public ExpandedQuadruple addQuadrupleToList(String operator, String argument1, String argument2, ExpandedQuadruple quadruple,
                                                boolean setNewOnArgument1) {
        ExpandedQuadruple newQuadruple;
        if (operator.equals("MINUS"))
            newQuadruple = new ExpandedQuadruple("MINUS", argument1, this.retrieveNextTemporary(), 0, 0);
        else
            newQuadruple = new ExpandedQuadruple(operator, argument1, argument2, this.retrieveNextTemporary(), 0, 0);

        this.getExpandedQuadruples().add(newQuadruple);

        if (setNewOnArgument1)
            quadruple.setArgument1(newQuadruple.getResult());
        else
            quadruple.setArgument2(newQuadruple.getResult());

        return newQuadruple;
    }

    /**
     * Obtem a String que sera utilizada como {@code result} da nova quadrupla
     *
     * @return {@link String} que eh o numero da ultima temporaria da lista, mais um (+1).
     */
    public String retrieveNextTemporary() {
        String lastQuadrupleResult = this.getExpandedQuadruples().get(this.getExpandedQuadruples().size() - 1).getResult();
        int value = Integer.parseInt(lastQuadrupleResult.replace("T", ""));

        return "T" + (value + 1);
    }

    /**
     * Remove todos os parenteses na lista {@code expandedQuadruples}, ou seja, define o nível dessas {@link ExpandedQuadruple}
     * para zero (0).
     */
    public void removeQuadruplesParentheses() {
        for (ExpandedQuadruple expandedQuadruple : this.getExpandedQuadruples()) {
            expandedQuadruple.setLevel(0);
        }
    }

    /**
     * Remove os parenteses na lista {@code expandedQuadruples}, ou seja, define o nível dessas {@link ExpandedQuadruple}
     * para zero (0), podendo escolher se remove ou não as quádruplas com operador MINUS.
     *
     * @param keepMinusQuadruples {@link Boolean} que indica se as quádruplas com operador MINUS serão mantidas, no qual:
     *                            <ul>
     *                                 <li>true - As quadruplas com MINUS, serão mantidas.</li>
     *                              <li>false - As quadruplas com MINUS, serão retiradas.</li>
     *                                    </ul>
     */
    public void removeQuadruplesParentheses(boolean keepMinusQuadruples) {
        for (ExpandedQuadruple expandedQuadruple : this.getExpandedQuadruples()) {
            if (!keepMinusQuadruples)
                expandedQuadruple.setLevel(0);
            else if (!expandedQuadruple.isNegative())
                expandedQuadruple.setLevel(0);
        }
    }

    /**
     * Encontra o pai que contém o operador diretamente antes da {@code iterationQuadruple}.
     *
     * @param quadrupleResult {@link String} que eh o {@code result} da {@link ExpandedQuadruple}
     *                        de onde sera encontrado o pai
     * @return {@link ExpandedQuadruple} que contem o operador anterior relativo a {@code iterationQuadruple}
     */
    public ExpandedQuadruple findDirectFather(String quadrupleResult) {
        if (this.getLeft().equals(quadrupleResult))
            return null;
        ExpandedQuadruple father = this.findQuadrupleByArgument(quadrupleResult);
        if (father.getArgument1().equals(quadrupleResult)) {
            return this.findDirectFather(father.getResult());
        }
        return father;

    }

    /**
     * Encontra o próximo argumento válido do filho.
     * @param quadrupleResult {@link String} representando uma variável temporária que será analisada
     * @param isArgument1 {@link Boolean} que indica se o argument 1 ou 2 será analisado e obtido
     * @return {@link String} do argumento correspondente do filho
     */
    public String findDirectSonArgument(String quadrupleResult, boolean isArgument1) {
        if (this.getLeft().equals(quadrupleResult))
            return null;
        if(!StringUtil.match(quadrupleResult,RegexPattern.TEMPORARY_VARIABLE.toString()))
            return quadrupleResult;
        ExpandedQuadruple son = this.findQuadrupleByResult(quadrupleResult);
        String argument = (isArgument1) ? son.getArgument1() : son.getArgument2();
        if (StringUtil.match(argument,RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.findDirectSonArgument(argument,isArgument1);
        }
        return argument;
    }

    /**
     * Ajusta as quadruplas que tem algum grau e estao com o formato incorreto. Necessario para os casos que ha uma
     * quadrupla de MINUS com um grau de potenciacao (Ex.: -2x^2), essas ficam como temporarias com grau de potenciação
     * (Ex.: T4^2, sendo T4 = MINUS 2x), o que não é desejável. Assim, o expoente é retirado do lado da variavel temporaria e adicionado a
     * quadrupla de MINUS. No caso do exemplo, seria T4 = MINUS 2x^2.
     */
    public void handlePotentiation() {
        for (ExpandedQuadruple expandedQuadruple : this.getExpandedQuadruples()) {
            // Verifica se não é uma quadrupla de MINUS que contém um expoente, pois não tem o argument2 para obter depois
            if (!expandedQuadruple.isNegative() && this.isTemporaryVariableWithPotentiation(expandedQuadruple)) {
                String argument = "";

                //Variável responsável por identificar qual argumento está sendo tratado
                boolean isArgument1 = true;

                if (expandedQuadruple.getArgument1().contains("^"))
                    argument = expandedQuadruple.getArgument1();
                else if (expandedQuadruple.getArgument2().contains("^")) {
                    argument = expandedQuadruple.getArgument2();
                    isArgument1 = false;
                }

                /* Nesse caso não há como fazer comparação através do {@link StringUtil.match} pois uma
                 * variável temporaria não deveria ter um expoente, e então sempre daria um resultado false */
                if (argument.startsWith("T")) {
                    String potentiation = argument.substring(argument.indexOf("^"));
                    argument = argument.replace(potentiation, "");

                    //Retirado expoente da variável temporaria
                    if (isArgument1)
                        expandedQuadruple.setArgument1(argument);
                    else
                        expandedQuadruple.setArgument2(argument);

                    //Adicionado o expoente ao argument1 da quadrupla MINUS.
                    ExpandedQuadruple quadruple = this.findQuadrupleByResult(argument);
                    quadruple.setArgument1(quadruple.getArgument1() + potentiation);
                }
            }
        }
    }

    /**
     * Verifica se a {@code expandedQuadruple} é uma variavel temporaria com uma potenciação. Ex.: T5^2
     *
     * @param expandedQuadruple {@link ExpandedQuadruple} a ser verificada.
     * @return {@code true} caso ela seja uma temporaria com potenciacao, e {@code false} caso contrario.
     */
    private boolean isTemporaryVariableWithPotentiation(ExpandedQuadruple expandedQuadruple) {
        return (expandedQuadruple.getArgument1().contains("T") && expandedQuadruple.getArgument1().contains("^"))
                || (expandedQuadruple.getArgument2().contains("T") && expandedQuadruple.getArgument2().contains("^"));
    }

    /**
     * Ajusta os valores iniciais da quadrupla
     */
    public void setUp() {
        this.setComparison("");
        this.setRight("");
    }

    private StringBuilder generateLaTeXNotation(String param, int level, StringBuilder builder) {
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple operation = findQuadrupleByResult(param);

            if (operation.getLevel() != level)
                builder.append("(");

            if (operation.isNegative()) {
                builder.append("-");
                generateLaTeXNotation(operation.getArgument1(), operation.getLevel(), builder);
            } else if (operation.isFraction()) {
                builder.append("\\frac{");
                generateLaTeXNotation(operation.getArgument1(), operation.getLevel(), builder);
                builder.append("}{");
                generateLaTeXNotation(operation.getArgument2(), operation.getLevel(), builder);
                builder.append("}");
            } else {
                generateLaTeXNotation(operation.getArgument1(), operation.getLevel(), builder);
                builder.append(operation.getOperator());
                generateLaTeXNotation(operation.getArgument2(), operation.getLevel(), builder);
            }

            if (operation.getLevel() != level)
                builder.append(")");

            return builder;
        }

        builder.append(param);
        return builder;
    }

    public String toMathNotation() {
        return String.format("%s %s %s",
                generateMathNotation(left, 0, new StringBuilder()).toString(),
                comparison,
                generateMathNotation(right, 0, new StringBuilder()).toString());
    }


    private StringBuilder generateMathNotation(String param, int level, StringBuilder builder) {
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple operation = findQuadrupleByResult(param);

            if (operation.getLevel() != level)
                builder.append("(");

            if (operation.isNegative()) {
                if (builder.length() != 0 && (builder.charAt(builder.length() - 1) == '('))
                    builder.append("-");
                else
                    builder.append(" -");
                generateMathNotation(operation.getArgument1(), operation.getLevel(), builder);
            } else {
                generateMathNotation(operation.getArgument1(), operation.getLevel(), builder);
                builder.append(" ");
                builder.append(operation.getOperator());
                builder.append(" ");
                generateMathNotation(operation.getArgument2(), operation.getLevel(), builder);
            }

            if (operation.getLevel() != level)
                builder.append(")");

            return builder;
        }

        builder.append(param);
        return builder;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            builder.append(expandedQuadruple.toString());
            builder.append('\n');
        }

        return builder.toString();
    }
}
