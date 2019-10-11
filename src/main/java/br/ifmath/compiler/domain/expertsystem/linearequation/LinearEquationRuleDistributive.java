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
import br.ifmath.compiler.infrastructure.util.MathOperatorUtil;
import br.ifmath.compiler.infrastructure.util.NumberUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alex_
 */
public class LinearEquationRuleDistributive implements IRule {

    private List<ExpandedQuadruple> expandedQuadruples;
    private int operationIndex;

    public LinearEquationRuleDistributive() {
        this.expandedQuadruples = new ArrayList<>();
        this.operationIndex = 0;
    }

    @Override
    public boolean match(List<ThreeAddressCode> sources) {
        for (ExpandedQuadruple expandedQuadruple : sources.get(0).getExpandedQuadruples()) {
            if (expandedQuadruple.isTimes()) {
                if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()) && !StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    ExpandedQuadruple innerOperation = sources.get(0).findQuadrupleByResult(expandedQuadruple.getArgument1());

                    if (isThereOnlyOneVariableInDistributive(sources.get(0), expandedQuadruple.getArgument2(), innerOperation)) {
                        return true;
                    }
                }

                if (!StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()) && StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    ExpandedQuadruple innerOperation = sources.get(0).findQuadrupleByResult(expandedQuadruple.getArgument2());

                    if (isThereOnlyOneVariableInDistributive(sources.get(0), expandedQuadruple.getArgument1(), innerOperation)) {
                        return true;
                    }
                }

                if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()) && StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    ExpandedQuadruple innerOperation1 = sources.get(0).findQuadrupleByResult(expandedQuadruple.getArgument1());
                    ExpandedQuadruple innerOperation2 = sources.get(0).findQuadrupleByResult(expandedQuadruple.getArgument2());

                    if (innerOperation1.isNegative() && isThereOnlyOneVariableInDistributive(sources.get(0), innerOperation1.getArgument1(), innerOperation2)) {
                        return true;
                    }

                    if (innerOperation2.isNegative() && isThereOnlyOneVariableInDistributive(sources.get(0), innerOperation2.getArgument1(), innerOperation1)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private int maxIndex(List<ExpandedQuadruple> quadruples) {
        int maxIndex = 1;
        for (ExpandedQuadruple quadruple : quadruples) {
            int index = Integer.parseInt(StringUtil.removeNonNumericChars(quadruple.getResult()));
            if (index > maxIndex)
                maxIndex = index;
        }

        return maxIndex;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        expandedQuadruples = new ArrayList<>();

        operationIndex = maxIndex(sources.get(0).getExpandedQuadruples()) + 1;

        List<Step> steps = new ArrayList<>();

        for (ExpandedQuadruple expandedQuadruple : sources.get(0).getExpandedQuadruples()) {
            if (expandedQuadruple.isTimes()) {
                ExpandedQuadruple parent = sources.get(0).findQuadrupleByArgument(expandedQuadruple.getResult());

                ExpandedQuadruple grandparent = null;
                if (parent != null)
                    grandparent = sources.get(0).findQuadrupleByArgument(parent.getResult());

                boolean lastOperationIsMinus = false, root = false, needChangeInGrandparent = false,
                        updateParantFirstParameter = false, removeParent = false;
                if (parent != null) {
                    if (parent.isArgument1(expandedQuadruple.getResult())) {
                        if (parent.isNegative()) {
                            lastOperationIsMinus = true;

                            if (grandparent == null) {
                                root = true;
                            } else {
                                if (grandparent.isArgument2(parent.getResult()) && grandparent.isMinus()) {
                                    needChangeInGrandparent = true;
                                    lastOperationIsMinus = false;
                                } else {
                                    removeParent = true;
                                }
                            }
                        } else {
                            updateParantFirstParameter = true;
                        }
                    } else {
                        lastOperationIsMinus = parent.isMinus();
                    }
                } else {
                    root = true;
                }

                if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()) && !StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    solveDistributive(sources.get(0), lastOperationIsMinus, root, needChangeInGrandparent, updateParantFirstParameter, removeParent,
                            expandedQuadruple.getResult(), expandedQuadruple.getArgument2(), expandedQuadruple.getArgument1(),
                            expandedQuadruple.getPosition(), expandedQuadruple.getLevel(),
                            parent, grandparent);
                }

                if (!StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()) && StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    solveDistributive(sources.get(0), lastOperationIsMinus, root, needChangeInGrandparent, updateParantFirstParameter, removeParent,
                            expandedQuadruple.getResult(), expandedQuadruple.getArgument1(), expandedQuadruple.getArgument2(),
                            expandedQuadruple.getPosition(), expandedQuadruple.getLevel(),
                            parent, grandparent);
                }

                if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()) && StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    ExpandedQuadruple innerOperation1 = sources.get(0).findQuadrupleByResult(expandedQuadruple.getArgument1());
                    ExpandedQuadruple innerOperation2 = sources.get(0).findQuadrupleByResult(expandedQuadruple.getArgument2());

                    if (innerOperation1.isNegative() && isThereOnlyOneVariableInDistributive(sources.get(0), innerOperation1.getArgument1(), innerOperation2)) {
                        solveDistributive(sources.get(0), true, root, needChangeInGrandparent, updateParantFirstParameter, removeParent,
                                expandedQuadruple.getResult(), innerOperation1.getArgument1(), expandedQuadruple.getArgument2(),
                                expandedQuadruple.getPosition(), expandedQuadruple.getLevel(),
                                parent, grandparent);
                    }

                    if (innerOperation2.isNegative() && isThereOnlyOneVariableInDistributive(sources.get(0), innerOperation2.getArgument1(), innerOperation1)) {
                        solveDistributive(sources.get(0), true, root, needChangeInGrandparent, updateParantFirstParameter, removeParent,
                                expandedQuadruple.getResult(), innerOperation2.getArgument1(), expandedQuadruple.getArgument1(),
                                expandedQuadruple.getPosition(), expandedQuadruple.getLevel(),
                                parent, grandparent);
                    }
                }
            } else {
                if (!expandedQuadruples.contains(expandedQuadruple))
                    expandedQuadruples.add(expandedQuadruple);
            }
        }

        ThreeAddressCode handledCode = new ThreeAddressCode(sources.get(0).getLeft(), sources.get(0).getComparison(), sources.get(0).getRight(), expandedQuadruples);

        eliminateNonUsedQuadruples(handledCode);

        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(handledCode);

        steps.add(new Step(codes, handledCode.toLaTeXNotation(), handledCode.toMathNotation(),
                "Utilizando a propriedade distributiva, o elemento externo multiplicou cada elemento da operação interna"));

        return steps;
    }

    private void solveDistributive(ThreeAddressCode source, boolean lastOperationIsMinus, boolean root,
                                   boolean needChangeInGrandparent, boolean updateParantFirstParameter, boolean removeParent,
                                   String operationResult, String operationArgument1, String operationArgument2, int position, int level,
                                   ExpandedQuadruple parent, ExpandedQuadruple grandparent) {
        String result;
        String possibleNegative = getPossibleNegative(source, operationArgument2);

        if (needChangeInGrandparent) {
            if (possibleNegative == null)
                result = calculateDistributive(source, (lastOperationIsMinus ? "-" : "") + operationArgument1,
                        operationArgument2, position, level, grandparent, false, false, operationResult);
            else
                result = calculateDistributive(source, (lastOperationIsMinus ? "-" : "") + operationArgument1,
                        possibleNegative, position, level, grandparent, false, true, operationResult);

            grandparent.setArgument2(result);
        } else if (root) {
            if (possibleNegative == null)
                result = calculateDistributive(source, (lastOperationIsMinus ? "-" : "") + operationArgument1,
                        operationArgument2, position, level, parent, false, false, operationResult);
            else
                result = calculateDistributive(source, (lastOperationIsMinus ? "-" : "") + operationArgument1,
                        possibleNegative, position, level, parent, false, true, operationResult);

            if (parent == null) {
                if (source.getRight().equals(operationResult))
                    source.setRight(result);
                else
                    source.setLeft(result);
            } else {
                if (source.getRight().equals(parent.getResult()))
                    source.setRight(result);
                else
                    source.setLeft(result);
            }
        } else {
            if (possibleNegative == null)
                result = calculateDistributive(source, (lastOperationIsMinus ? "-" : "") + operationArgument1,
                        operationArgument2, position, level, parent, false, false, operationResult);
            else
                result = calculateDistributive(source, (lastOperationIsMinus ? "-" : "") + operationArgument1,
                        possibleNegative, position, level, parent, false, true, operationResult);

            if (updateParantFirstParameter)
                parent.setArgument1(result);
            else
                parent.setArgument2(result);
        }

        if (removeParent && grandparent != null) {
            grandparent.setArgument1(result);
            expandedQuadruples.remove(parent);
        }
    }

    private String getPossibleNegative(ThreeAddressCode source, String param) {
        ExpandedQuadruple quadruple = source.findQuadrupleByResult(param);
        if (quadruple != null && quadruple.isNegative()) {
            return quadruple.getArgument1();
        }

        return null;
    }

    private void eliminateNonUsedQuadruples(ThreeAddressCode source) {
        List<ExpandedQuadruple> toRemove = new ArrayList<>();

        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            boolean used = false;

            if (expandedQuadruple != null) {
                for (int i = 0; i < expandedQuadruples.size(); i++) {
                    ExpandedQuadruple quadruple = expandedQuadruples.get(i);
                    if (quadruple != null && (expandedQuadruple.getResult().equals(quadruple.getArgument1())
                            || expandedQuadruple.getResult().equals(quadruple.getArgument2()))) {
                        used = true;
                        break;
                    }
                }

                used = used || expandedQuadruple.getResult().equals(source.getLeft())
                        || expandedQuadruple.getResult().equals(source.getRight());
            }

            if (!used) {
                toRemove.add(expandedQuadruple);
            }
        }

        expandedQuadruples.removeAll(toRemove);
    }

    private String calculateDistributive(ThreeAddressCode source, String multiplier, String innerOperationString, int position, int level, ExpandedQuadruple parent, boolean lastOperationIsNegative, boolean changeSignal, String currentResult) {
        ExpandedQuadruple operation = source.findQuadrupleByResult(innerOperationString);

        expandedQuadruples.removeAll(source.findAllQuadruplesByResultOrArgument(innerOperationString));

        if (!operation.isPlusOrMinus()) {
            return null;
        }

        String a = "", b = "";
        String operator = "+";

        boolean skipA = false;
        if (StringUtil.match(operation.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple possibleNegative = source.findQuadrupleByResult(operation.getArgument1());

            if (possibleNegative.isNegative()) {
                a = "-" + possibleNegative.getArgument1();
            } else {
                skipA = true;
                a = createDistributive(multiplier, possibleNegative.getResult(), position, level);
            }
        } else {
            a = (lastOperationIsNegative ? "-" : "") + operation.getArgument1();
        }

        if (parent != null)
            parent.setOperator("+");

        if (!skipA) {
            double resultA = MathOperatorUtil.times(multiplier, a);

            if (changeSignal) {
                resultA *= (-1);
            }

            a = "" + generateParameter(resultA) + StringUtil.getVariable(multiplier, a);
            if (resultA < 0) {
                if (parent == null || parent.getArgument1().equals(currentResult))
                    a = createNegativeNumberQuadruple(a, position, level);
                else
                    parent.setOperator("-");
            }
        } else {
            if (changeSignal) {
                a = createNegativeNumberQuadruple(a, position, level);
            }
        }

        ExpandedQuadruple finalOperation = new ExpandedQuadruple(operator, a, "T" + (this.operationIndex++), position, level);

        if (StringUtil.match(operation.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple innerOperation = source.findQuadrupleByResult(operation.getArgument2());

            if (innerOperation.getLevel() == operation.getLevel()) {
                b = calculateDistributive(source, multiplier, operation.getArgument2(), position, level, finalOperation, operation.isMinus(), changeSignal, currentResult);
            } else {
                finalOperation.setOperator(operation.getOperator());
                b = createDistributive(multiplier, innerOperation.getResult(), position, level);
            }
        } else {
            b = operation.getArgument2();
            if (operation.isMinus()) {
                b = "-" + operation.getArgument2();
            }

            double resultB = MathOperatorUtil.times(multiplier, b);
            b = "" + generateParameter(resultB) + StringUtil.getVariable(multiplier, b);
            if (resultB < 0) {
                finalOperation.setOperator("-");
            }
        }

        if (changeSignal) {
            if (finalOperation.getOperator().equals("+"))
                finalOperation.setOperator("-");
            else
                finalOperation.setOperator("+");
        }

        finalOperation.setArgument2(b);
        this.expandedQuadruples.add(finalOperation);

        return finalOperation.getResult();
    }


    private String createDistributive(String multiplier, String innerOperation, int position, int level) {
        if (multiplier.contains("-")) {
            multiplier = createNegativeNumberQuadruple(multiplier, position, level);
        }

        ExpandedQuadruple distributive = new ExpandedQuadruple("*", multiplier, innerOperation, "T" + (this.operationIndex++), position, level);
        this.expandedQuadruples.add(distributive);

        return distributive.getResult();
    }

    private String createNegativeNumberQuadruple(String parameter, int position, int level) {
        ExpandedQuadruple negativeQuadruple = new ExpandedQuadruple("MINUS", parameter, "T" + (this.operationIndex++), position, level);
        this.expandedQuadruples.add(negativeQuadruple);

        return negativeQuadruple.getResult();
    }

    private String generateParameter(double value) {
        if (NumberUtil.isInteger(value))
            return String.valueOf(NumberUtil.parseInt(value)).replace("-", "");
        else
            return String.valueOf(value).replace(".", ",").replace("-", "");
    }

    private boolean isThereOnlyOneVariableInDistributive(ThreeAddressCode threeAddressCode, String multiplier, ExpandedQuadruple expandedQuadruple) {
        int countVariableInnerOperation = 0;

        do {
            if (!expandedQuadruple.isPlusOrMinus()) {
                return false;
            }

            if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                if (StringUtil.matchAny(expandedQuadruple.getArgument2(), RegexPattern.VARIABLE.toString(), RegexPattern.VARIABLE_WITH_COEFICIENT.toString()))
                    countVariableInnerOperation++;

                expandedQuadruple = threeAddressCode.findQuadrupleByResult(expandedQuadruple.getArgument1());
            } else if (StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                if (StringUtil.matchAny(expandedQuadruple.getArgument1(), RegexPattern.VARIABLE.toString(), RegexPattern.VARIABLE_WITH_COEFICIENT.toString()))
                    countVariableInnerOperation++;

                expandedQuadruple = threeAddressCode.findQuadrupleByResult(expandedQuadruple.getArgument2());
            } else {
                if (StringUtil.matchAny(expandedQuadruple.getArgument1(), RegexPattern.VARIABLE.toString(), RegexPattern.VARIABLE_WITH_COEFICIENT.toString()))
                    countVariableInnerOperation++;

                if (StringUtil.matchAny(expandedQuadruple.getArgument2(), RegexPattern.VARIABLE.toString(), RegexPattern.VARIABLE_WITH_COEFICIENT.toString()))
                    countVariableInnerOperation++;

                expandedQuadruple = null;
            }

        } while (expandedQuadruple != null);

        return !(StringUtil.matchAny(multiplier, RegexPattern.VARIABLE.toString(), RegexPattern.VARIABLE_WITH_COEFICIENT.toString()) && countVariableInnerOperation > 0);
    }

}
