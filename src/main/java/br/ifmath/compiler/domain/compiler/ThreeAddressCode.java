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


    public void clearNonUsedQuadruples() {
        List<ExpandedQuadruple> quadruplesToRemove = new ArrayList<>();

        for (ExpandedQuadruple expandedQuadruple : this.getExpandedQuadruples()) {
            if (expandedQuadruple.getResult() != this.getLeft()
                    && this.findQuadrupleByArgument(expandedQuadruple.getResult()) == null) {
                quadruplesToRemove.add(expandedQuadruple);
            }
        }
        this.expandedQuadruples.removeAll(quadruplesToRemove);
    }

    public String retrieveNextTemporary() {
        String lastQuadrupleResult = this.getExpandedQuadruples().get(this.getExpandedQuadruples().size() - 1).getResult();
        int value = Integer.parseInt(lastQuadrupleResult.replace("T", ""));

        return "T" + (value + 1);
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

    public void addQuadrupleToList(String operator, String argument1, String argument2, ExpandedQuadruple quadruple, boolean setNewOnArgument1) {
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
