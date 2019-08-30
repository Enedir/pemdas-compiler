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
