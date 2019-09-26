package br.ifmath.compiler.domain.expertsystem.polynomial;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;


import java.util.ArrayList;
import java.util.List;


public class PolynomialRuleMultiplyNumbers implements IRule {
    private List<ExpandedQuadruple> expandedQuadruples;
    private int operationIndex;


    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return isThereTermsToMultiply(source.get(0).getExpandedQuadruples());
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) {
        List<Step> steps = new ArrayList<>();
        expandedQuadruples = new ArrayList<>();

        expandedQuadruples.addAll(sources.get(0).getExpandedQuadruples());
        operationIndex = expandedQuadruples.size() + 1;
        List<ExpandedQuadruple> rightTimes = checkTimesOperation(sources.get(0).getExpandedQuadruples());

        multiply(sources, rightTimes);

        generateParameter(expandedQuadruples, rightTimes);

        if (!expandedQuadruples.isEmpty())
            sources.get(0).setExpandedQuadruples(expandedQuadruples);
        else
            expandedQuadruples = rightTimes;
        ThreeAddressCode step = new ThreeAddressCode("x", sources.get(0).getComparison(), sources.get(0).getRight(), expandedQuadruples);
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation(), step.toMathNotation().trim(), "Multiplicando os valores."));

        return steps;
    }


    private List<ExpandedQuadruple> checkTimesOperation(List<ExpandedQuadruple> expandedQuadruples) {
        List<ExpandedQuadruple> expandedQuadruples1 = new ArrayList<>();
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.getOperator().equals("*")) {
                expandedQuadruples1.add(expandedQuadruple);
            }
        }
        return expandedQuadruples1;
    }


    private void multiply(List<ThreeAddressCode> source, List<ExpandedQuadruple> times) {
        String a, b;

        for (ExpandedQuadruple eq : times) {
            if (StringUtil.match(eq.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                a = findInnerProduct(source, eq.getArgument1());
            } else {
                a = eq.getArgument1();
            }

            if (StringUtil.match(eq.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                b = findInnerProduct(source, eq.getArgument2());
            } else {
                b = eq.getArgument2();
            }

            if (!StringUtil.isEmpty(a) || !StringUtil.isEmpty(b)) {
                if (!eq.getOperator().equals("MINUS"))
                    expandedQuadruples.remove(eq);
                double result = Double.parseDouble(a) * Double.parseDouble(b);

                if (result < 0) {
                    result *= -1;
                    eq.setOperator("MINUS");
                } else
                    eq.setOperator("");

                if (result % 1 == 0)
                    eq.setArgument1(String.valueOf((int) result));
                else
                    eq.setArgument1(String.valueOf(result));

                eq.setArgument2("");
            }
        }

    }

    private String createNegativeNumberQuadruple(String parameter, int position, int level) {
        ExpandedQuadruple negativeQuadruple = new ExpandedQuadruple("MINUS", parameter, "T" + (this.operationIndex++), position, level);
        this.expandedQuadruples.add(negativeQuadruple);

        return negativeQuadruple.getResult();
    }


    private void generateParameter(List<ExpandedQuadruple> expandedQuadruples, List<ExpandedQuadruple> times) {
        for (ExpandedQuadruple multipliedQuadruple : times) {
            String temporaryVarLabel = multipliedQuadruple.getResult();
            for (ExpandedQuadruple remainingQuadruple : expandedQuadruples) {
                if (remainingQuadruple.getArgument1() != null && remainingQuadruple.getArgument2() != null) {
                    if (remainingQuadruple.getArgument1().equals(temporaryVarLabel)) {
                        if (multipliedQuadruple.getOperator().equals(""))
                            remainingQuadruple.setArgument1(multipliedQuadruple.getArgument1().replace(".", ","));
                    }
                    if (remainingQuadruple.getArgument2().equals(temporaryVarLabel)) {
                        remainingQuadruple.setArgument2(multipliedQuadruple.getArgument1().replace(".", ","));
                    }
                }
            }

        }
    }


    private String findInnerProduct(List<ThreeAddressCode> source, String tempVar) {
        ExpandedQuadruple innerQuadruple = source.get(0).findQuadrupleByResult(tempVar);
        if (!innerQuadruple.getOperator().equals("")) {
            if (innerQuadruple.getOperator().equals("MINUS")) {
                expandedQuadruples.remove(innerQuadruple);
                return "-" + innerQuadruple.getArgument1();
            }
        }
        return innerQuadruple.getArgument1();
    }

    private boolean isThereTermsToMultiply(List<ExpandedQuadruple> expandedQuadruples) {
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.getOperator().equals("*")) {
                return true;
            }
        }
        return false;
    }
}
