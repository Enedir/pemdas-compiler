/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.expertsystem.linearequation;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.NumberUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alex_
 */
public class LinearEquationRuleSumTerms implements IRule {

    private List<ExpandedQuadruple> expandedQuadruples;

    public LinearEquationRuleSumTerms() {
        this.expandedQuadruples = new ArrayList<>();
    }

    @Override
    public boolean match(List<ThreeAddressCode> sources) {
        return isThereTermsToSum(sources.get(0).getOperationsFromLeft())
                || isThereTermsToSum(sources.get(0).getOperationsFromRight());
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        expandedQuadruples.clear();

        List<Step> steps = new ArrayList<>();

        double leftValue = sumTerms(sources.get(0), sources.get(0).getLeft(), false);
        double rightValue = sumTerms(sources.get(0), sources.get(0).getRight(), false);

        String leftVariable = findVariable(sources.get(0), sources.get(0).getLeft());
        String rightVariable = findVariable(sources.get(0), sources.get(0).getRight());

        String left = generateParameter(leftValue, leftVariable, 0);
        String right = generateParameter(rightValue, rightVariable, 1);

        ThreeAddressCode step = new ThreeAddressCode(left, sources.get(0).getComparison(), right, expandedQuadruples);
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation(), step.toMathNotation(), "Soma dos termos semelhantes."));

        return steps;
    }

    private String generateParameter(double value, String variable, int position) {
        String parameter = "";

        if ((value != 1 && value != -1) || StringUtil.isEmpty(variable)) {
            if (NumberUtil.isInteger(value)) {
                parameter = String.valueOf(NumberUtil.parseInt(value));
            } else {
                parameter = String.valueOf(value).replace(".", ",");
            }
        }

        if (StringUtil.isNotEmpty(variable)) {
            parameter += variable;
        }

        if (value < 0) {
            parameter = parameter.replace("-", "");
            ExpandedQuadruple expandedQuadruple = new ExpandedQuadruple("MINUS", parameter, "T" + (expandedQuadruples.size() + 1), position, 0);
            expandedQuadruples.add(expandedQuadruple);
            parameter = expandedQuadruple.getResult();
        }

        return parameter;
    }

    private double sumTerms(ThreeAddressCode threeAddressCode, String param, boolean lastOperationIsMinus) {
        double sum = 0;

        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(param);

            if (expandedQuadruple.isNegative()) {
                sum -= sumTerms(threeAddressCode, expandedQuadruple.getArgument1(), false);
            } else {
                sum += sumTerms(threeAddressCode, expandedQuadruple.getArgument1(), lastOperationIsMinus);
                sum += sumTerms(threeAddressCode, expandedQuadruple.getArgument2(), expandedQuadruple.isMinus());
            }
        } else if (StringUtil.isVariable(param)) {
            String aux = StringUtil.removeNonNumericChars(param);
            if (StringUtil.isEmpty(aux)) {
                if (lastOperationIsMinus)
                    sum -= 1;
                else
                    sum += 1;
            } else {
                if (lastOperationIsMinus)
                    sum -= Double.parseDouble(aux.replace(",", "."));
                else
                    sum += Double.parseDouble(aux.replace(",", "."));
            }
        } else {
            if (lastOperationIsMinus)
                sum -= Double.parseDouble(param.replace(",", "."));
            else
                sum += Double.parseDouble(param.replace(",", "."));
        }

        return sum;
    }


    private String findVariable(ThreeAddressCode threeAddressCode, String param) {
        String variable = null;
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(param);

            variable = findVariable(threeAddressCode, expandedQuadruple.getArgument1());

            if (StringUtil.isEmpty(variable)) {
                variable = findVariable(threeAddressCode, expandedQuadruple.getArgument2());
            }
        } else if (StringUtil.isVariable(param)) {
            variable = StringUtil.removeNumericChars(param);
        }

        return variable;
    }


    private boolean isThereTermsToSum(List<ExpandedQuadruple> expandedQuadruples) {
        int variableAmount = 0;
        int numberAmount = 0;

        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (!expandedQuadruple.isPlusOrMinus() || expandedQuadruple.getLevel() != 0)
                return false;

            if (StringUtil.matchAny(expandedQuadruple.getArgument1(), RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString()))
                numberAmount++;

            if (StringUtil.matchAny(expandedQuadruple.getArgument2(), RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString()))
                numberAmount++;

            if (StringUtil.isVariable(expandedQuadruple.getArgument1()))
                variableAmount++;

            if (StringUtil.isVariable(expandedQuadruple.getArgument2()))
                variableAmount++;
        }

        return (variableAmount > 1 && numberAmount == 0) || (variableAmount == 0 && numberAmount > 1);
    }

}
