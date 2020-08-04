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
public class LinearEquationRuleIsolateAndSumTerms implements IRule {

    private final List<ExpandedQuadruple> expandedQuadruples;
    private ExpandedQuadruple quadrupleVariable;
    private ExpandedQuadruple quadrupleNumber;
    private String left;
    private String right;
    private int count;

    public LinearEquationRuleIsolateAndSumTerms() {
        this.expandedQuadruples = new ArrayList<>();
    }

    @Override
    public boolean match(List<ThreeAddressCode> sources) {
        return isThereOnlyOperationInLevel0(sources.get(0).getExpandedQuadruples())
                && (isThereTermsToIsolate(sources.get(0).getOperationsFromLeft()) || isThereTermsToIsolate(sources.get(0).getOperationsFromRight()))
                && isThereTermsToSum(sources.get(0).getExpandedQuadruples());
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        expandedQuadruples.clear();

        List<Step> steps = new ArrayList<>();

        isolateTerms(sources.get(0), sources.get(0).getLeft(), null, null, true);
        isolateTerms(sources.get(0), sources.get(0).getRight(), null, null, false);

        if (StringUtil.isEmpty(quadrupleVariable.getArgument2()) && !quadrupleVariable.isNegative()) {
            left = quadrupleVariable.getArgument1();
        } else {
            expandedQuadruples.add(quadrupleVariable);
        }

        if (StringUtil.isEmpty(quadrupleNumber.getArgument2()) && !quadrupleNumber.isNegative()) {
            right = quadrupleNumber.getArgument1();
        } else {
            expandedQuadruples.add(quadrupleNumber);
        }

        ThreeAddressCode isolatedTerms = new ThreeAddressCode(left, sources.get(0).getComparison(), right, expandedQuadruples);

        double leftValue = sumTerms(isolatedTerms, isolatedTerms.getLeft(), false);
        double rightValue = sumTerms(isolatedTerms, isolatedTerms.getRight(), false);

        String leftVariable = findVariable(isolatedTerms, isolatedTerms.getLeft());
        String rightVariable = findVariable(isolatedTerms, isolatedTerms.getRight());

        String left = generateParameter(leftValue, leftVariable, 0);
        String right = generateParameter(rightValue, rightVariable, 1);

        ThreeAddressCode step = new ThreeAddressCode(left, sources.get(0).getComparison(), right, expandedQuadruples);
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation(), step.toMathNotation(), "Os termos semelhantes foram isolados. "
                + "As variáveis foram movidas para a esquerda da igualdade e as constantes foram movidas para a direita. "
                + "Ao trocar a posição de um termo perante a igualdade, é necessário mudar o seu sinal, aplicando a operação inversa. "
                + "Neste caso, quando o termo mudou de posição, + passou a ser – e – passou a ser +. Após isolados, os termos semelhantes são somados."));

        return steps;
    }


    private boolean isThereTermsToIsolate(List<ExpandedQuadruple> expandedQuadruples) {
        int variableAmount = 0;
        int numberAmount = 0;

        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (!expandedQuadruple.isPlusOrMinus())
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

        return variableAmount > 0 && numberAmount > 0;
    }

    private boolean isThereOnlyOperationInLevel0(List<ExpandedQuadruple> expandedQuadruples) {
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.getLevel() != 0)
                return false;
        }

        return true;
    }

    private void isolateTerms(ThreeAddressCode source, String param, ExpandedQuadruple parent, ExpandedQuadruple grandparent, boolean left) {
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple current = source.findQuadrupleByResult(param);

            isolateTerms(source, current.getArgument1(), current, parent, left);
            isolateTerms(source, current.getArgument2(), current, parent, left);
        } else if (StringUtil.matchAny(param, RegexPattern.VARIABLE.toString(), RegexPattern.VARIABLE_WITH_COEFFICIENT.toString())) {
            isolateVariable(param, parent, grandparent, left);
        } else if (StringUtil.matchAny(param, RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString())) {
            isolateNumber(param, parent, grandparent, left);
        }
    }

    private void isolateVariable(String param, ExpandedQuadruple parent, ExpandedQuadruple grandparent, boolean left) {
        if (parent == null || (parent.getArgument1().equals(param) && grandparent == null)) {
            quadrupleVariable = addTermToQuadruple(quadrupleVariable, param, !left, 0, false);
        } else if (parent.getArgument1().equals(param) && grandparent != null && grandparent.getArgument2().equals(parent.getResult())) {
            quadrupleVariable = addTermToQuadruple(quadrupleVariable, param, !(grandparent.isMinusOrNegative() ^ left), 0, false);
        } else {
            quadrupleVariable = addTermToQuadruple(quadrupleVariable, param, !(parent.isMinusOrNegative() ^ left), 0, false);
        }
    }

    private void isolateNumber(String param, ExpandedQuadruple parent, ExpandedQuadruple grandparent, boolean left) {
        if (parent == null || (parent.getArgument1().equals(param) && grandparent == null)) {
            quadrupleNumber = addTermToQuadruple(quadrupleNumber, param, left, 0, true);
        } else if (parent.getArgument1().equals(param) && grandparent != null && grandparent.getArgument2().equals(parent.getResult())) {
            quadrupleNumber = addTermToQuadruple(quadrupleNumber, param, grandparent.isMinusOrNegative() ^ left, 0, true);
        } else {
            quadrupleNumber = addTermToQuadruple(quadrupleNumber, param, parent.isMinusOrNegative() ^ left, 0, true);
        }
    }

    private ExpandedQuadruple addTermToQuadruple(ExpandedQuadruple quadruple, String param, boolean minus, int position, boolean number) {
        if (quadruple == null) {
            if (minus) {
                quadruple = new ExpandedQuadruple("MINUS", param, "T" + (++count), position, 0);
                expandedQuadruples.add(quadruple);
                quadruple = new ExpandedQuadruple(quadruple.getResult(), "T" + (++count), position, 0);
            } else {
                quadruple = new ExpandedQuadruple(param, "T" + (++count), position, 0);
            }

            if (number) {
                this.right = quadruple.getResult();
            } else {
                this.left = quadruple.getResult();
            }
        } else {
            if (StringUtil.isNotEmpty(quadruple.getArgument2())) {
                ExpandedQuadruple aux = new ExpandedQuadruple((minus ? "-" : "+"), quadruple.getArgument2(), param, "T" + (++count), position, 0);
                quadruple.setArgument2(aux.getResult());
                expandedQuadruples.add(quadruple);
                quadruple = aux;
            } else {
                quadruple.setArgument2(param);
                quadruple.setOperator(minus ? "-" : "+");
            }
        }

        return quadruple;
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

    private String generateParameter(double value, String variable, int position) {
        String parameter = "";

        if (value != 1 && value != -1) {
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

}
