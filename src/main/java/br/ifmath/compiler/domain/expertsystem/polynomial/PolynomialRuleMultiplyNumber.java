package br.ifmath.compiler.domain.expertsystem.polynomial;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.MathOperatorUtil;
import br.ifmath.compiler.infrastructure.util.NumberUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;


import java.util.ArrayList;
import java.util.List;


public class PolynomialRuleMultiplyNumber implements IRule {
    private List<ExpandedQuadruple> expandedQuadruples;
    private int operationIndex;


    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return isThereTermsToMultiply(source.get(0).getExpandedQuadruples());
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();
        expandedQuadruples = new ArrayList<>();


        expandedQuadruples.addAll(sources.get(0).getExpandedQuadruples());
        operationIndex = expandedQuadruples.size() + 1;
        List<ExpandedQuadruple> rightTimes = checkTimesOperation(sources.get(0).getExpandedQuadruples(), 1);

        multiply(sources, rightTimes);

        String result = generateParameter(expandedQuadruples, rightTimes);

        ThreeAddressCode step = new ThreeAddressCode("x", sources.get(0).getComparison(), "result", expandedQuadruples);
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation(), step.toMathNotation(), "Multiplicando os valores."));

        return steps;
    }


    private List<ExpandedQuadruple> checkTimesOperation(List<ExpandedQuadruple> expandedQuadruples, int position) {
        List<ExpandedQuadruple> expandedQuadruples1 = new ArrayList<>();
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.getOperator().equals("*")) {
                expandedQuadruples1.add(expandedQuadruple);
            }
        }
        return expandedQuadruples1;
    }


    private void multiply(List<ThreeAddressCode> source, List<ExpandedQuadruple> times) {
        String a = "", b = "";
        boolean changeSignal = false;

        for (ExpandedQuadruple eq : times) {
            if (StringUtil.match(eq.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple innerOperation = source.get(0).findQuadrupleByResult(eq.getArgument1());
                if (innerOperation.isNegative() && !StringUtil.match(innerOperation.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    a = "-" + innerOperation.getArgument1();
                    changeSignal = !changeSignal;
                }
            } else {
                a = eq.getArgument1();
            }

            if (StringUtil.match(eq.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple innerOperation = source.get(0).findQuadrupleByResult(eq.getArgument2());
                if (innerOperation.isNegative() && !StringUtil.match(innerOperation.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    b = "-" + innerOperation.getArgument1();
                    changeSignal = !changeSignal;
                }
            } else {
                b = eq.getArgument2();
            }

            if (!StringUtil.isEmpty(a) || !StringUtil.isEmpty(b)) {
                expandedQuadruples.remove(eq);

                eq.setArgument1(String.valueOf(MathOperatorUtil.times(a, b)));
                eq.setOperator("");
                eq.setArgument2("");
            }
        }

    }

    private String createNegativeNumberQuadruple(String parameter, int position, int level) {
        ExpandedQuadruple negativeQuadruple = new ExpandedQuadruple("MINUS", parameter, "T" + (this.operationIndex++), position, level);
        this.expandedQuadruples.add(negativeQuadruple);

        return negativeQuadruple.getResult();
    }

    private String generateParameter(List<ExpandedQuadruple> expandedQuadruples, List<ExpandedQuadruple> times) {
        String result = "";


        return result;
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
