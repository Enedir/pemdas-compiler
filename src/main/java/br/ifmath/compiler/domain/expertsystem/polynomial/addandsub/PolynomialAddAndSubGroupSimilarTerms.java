package br.ifmath.compiler.domain.expertsystem.polynomial.addandsub;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PolynomialAddAndSubGroupSimilarTerms implements IRule {

    private final List<ExpandedQuadruple> expandedQuadruples;

    public PolynomialAddAndSubGroupSimilarTerms() {
        this.expandedQuadruples = new ArrayList<>();
    }


    @Override
    public boolean match(List<ThreeAddressCode> sources) {
        return isThereEquivalentTermsToJoin(sources.get(0).getOperationsFromLeft())
                || isThereEquivalentTermsToJoin(sources.get(0).getOperationsFromRight());
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        List<NumericValueVariable> termsAndValuesList = new ArrayList<>();
        expandedQuadruples.clear();
        expandedQuadruples.addAll(sources.get(0).getExpandedQuadruples());

        List<Step> steps = new ArrayList<>();

        int numbersSum = sumTerms(sources.get(0), sources.get(0).getLeft(), false, termsAndValuesList);

        String nonVariableResult = "T1";
        ThreeAddressCode step;
        if (termsAndValuesList.isEmpty()) {
            ExpandedQuadruple newQuadruple = new ExpandedQuadruple("", String.valueOf(numbersSum), "", nonVariableResult, 0, 0);
            step = new ThreeAddressCode(nonVariableResult, Arrays.asList(newQuadruple));
        } else {
            replaceExpandedQuadruples(sources.get(0), termsAndValuesList, numbersSum);
            clearUnusedQuadruple(sources.get(0));
            step = new ThreeAddressCode(sources.get(0).getLeft(), sources.get(0).getExpandedQuadruples());
        }

        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Soma dos termos semelhantes."));

        return steps;
    }

    private void clearUnusedQuadruple(ThreeAddressCode source) {
        int size = expandedQuadruples.size() - 1;
        for (int i = 1; i <= size; i++) {
            source.getExpandedQuadruples().remove(1);
        }
    }

    private void replaceExpandedQuadruples(ThreeAddressCode source, List<NumericValueVariable> termsAndValuesList, int numbersSum) {
        ExpandedQuadruple iterationQuadruple = null;
        int i = 0;
        while (!termsAndValuesList.isEmpty()) {
            iterationQuadruple = (i == 0 || i == 1) ? expandedQuadruples.get(0) : source.findQuadrupleByResult(iterationQuadruple.getArgument2());
            NumericValueVariable iterationNVV = termsAndValuesList.get(0);
            if (iterationNVV.getValue() != 0) {
                String nvvValue = String.valueOf(Math.abs(iterationNVV.getValue()));
                if (nvvValue.equals("1"))
                    nvvValue = "";
                if (iterationNVV.getValue() < 0) {
                    if (i % 2 == 0 && i == 0) {
                        ExpandedQuadruple newQuadruple = new ExpandedQuadruple("MINUS", nvvValue + iterationNVV.getLabel(), "T" + (source.getExpandedQuadruples().size() + 1), 0, 0);
                        source.getExpandedQuadruples().add(newQuadruple);
                        iterationQuadruple.setArgument1(newQuadruple.getResult());
                    } else {
                        if (numbersSum == 0) {
                            iterationQuadruple.setOperator("-");
                            iterationQuadruple.setArgument2(nvvValue + iterationNVV.getLabel());
                        } else {
                            iterationQuadruple.setOperator("-");
                            ExpandedQuadruple newQuadruple = new ExpandedQuadruple("+", nvvValue + iterationNVV.getLabel(), "", "T" + (source.getExpandedQuadruples().size() + 1), 0, 0);
                            source.getExpandedQuadruples().add(newQuadruple);
                            iterationQuadruple.setArgument2(newQuadruple.getResult());
                        }
                    }
                } else {
                    if (i % 2 == 0 && i == 0) {
                        iterationQuadruple.setArgument1(nvvValue + iterationNVV.getLabel());
                    } else {
                        if (numbersSum == 0) {
                            if (termsAndValuesList.size() > 1) {
                                iterationQuadruple.setOperator("+");
                                ExpandedQuadruple newQuadruple = new ExpandedQuadruple("+", nvvValue + iterationNVV.getLabel(), "", "T" + (source.getExpandedQuadruples().size() + 1), 0, 0);
                                source.getExpandedQuadruples().add(newQuadruple);
                                iterationQuadruple.setArgument2(newQuadruple.getResult());
                            } else {
                                iterationQuadruple.setOperator("+");
                                iterationQuadruple.setArgument2(nvvValue + iterationNVV.getLabel());
                            }
                        } else {
                            iterationQuadruple.setOperator("+");
                            ExpandedQuadruple newQuadruple = new ExpandedQuadruple("+", nvvValue + iterationNVV.getLabel(), "", "T" + (source.getExpandedQuadruples().size() + 1), 0, 0);
                            source.getExpandedQuadruples().add(newQuadruple);
                            iterationQuadruple.setArgument2(newQuadruple.getResult());
                        }
                    }
                }
            }

            termsAndValuesList.remove(0);
            i++;
        }

        if (numbersSum != 0) {
            ExpandedQuadruple quadruple;

            if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                quadruple = source.findQuadrupleByResult(iterationQuadruple.getArgument2());
            } else {
                quadruple = iterationQuadruple;
            }

            if (numbersSum < 0)
                quadruple.setOperator("-");
            else
                quadruple.setOperator("+");

            quadruple.setArgument2(String.valueOf(Math.abs(numbersSum)));

        }

    }

    private int sumTerms(ThreeAddressCode threeAddressCode, String param, boolean lastOperationIsMinus, List<
            NumericValueVariable> termsAndValuesList) {
        int sum = 0;
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(param);

            if (expandedQuadruple.isNegative()) {
                sum -= sumTerms(threeAddressCode, expandedQuadruple.getArgument1(), false, termsAndValuesList);
            } else {
                sum += sumTerms(threeAddressCode, expandedQuadruple.getArgument1(), lastOperationIsMinus, termsAndValuesList);
                sum += sumTerms(threeAddressCode, expandedQuadruple.getArgument2(), expandedQuadruple.isMinus(), termsAndValuesList);
            }
        } else {
            if (StringUtil.isVariable(param)) {
                String paramValue = StringUtil.removeNonNumericChars(param);
                String paramVariable = StringUtil.removeNumericChars(param);
                int index = 0;
                int cont = 0;
                if (termsAndValuesList.isEmpty()) {
                    termsAndValuesList.add(new NumericValueVariable(paramVariable, 0));
                } else {
                    for (int i = 0; i < termsAndValuesList.size(); i++) {
                        if (!termsAndValuesList.get(i).getLabel().equals(paramVariable)) {
                            cont++;
                            if (cont == termsAndValuesList.size()) {
                                termsAndValuesList.add(new NumericValueVariable(paramVariable, 0));
                            }
                        } else {
                            index = i;
                        }
                    }
                }
                int newValue = (StringUtil.isEmpty(paramValue)) ? 1 : Integer.parseInt(paramValue);
                if (lastOperationIsMinus)
                    termsAndValuesList.get(index).setValue(termsAndValuesList.get(index).getValue() - newValue);
                else
                    termsAndValuesList.get(index).setValue(termsAndValuesList.get(index).getValue() + newValue);
            } else {
                if (lastOperationIsMinus)
                    sum -= Double.parseDouble(param.replace(",", "."));
                else
                    sum += Double.parseDouble(param.replace(",", "."));
            }
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


    private boolean isThereEquivalentTermsToJoin(List<ExpandedQuadruple> expandedQuadruples) {
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

        return (variableAmount > 1 || numberAmount > 1);
    }


}
