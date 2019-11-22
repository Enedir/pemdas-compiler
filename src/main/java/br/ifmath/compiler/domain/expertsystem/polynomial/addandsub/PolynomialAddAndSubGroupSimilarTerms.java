package br.ifmath.compiler.domain.expertsystem.polynomial.addandsub;

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
        expandedQuadruples.clear();

        List<Step> steps = new ArrayList<>();

        double leftIndependentTermValue = sumTerms(sources.get(0), sources.get(0).getLeft(), false, false);
        double leftVariableValue = sumTerms(sources.get(0), sources.get(0).getLeft(), false, true);

        //double rightIndependentTermValue = sumTerms(sources.get(0), sources.get(0).getRight(), false, false);
        //double rightVariableValue = sumTerms(sources.get(0), sources.get(0).getRight(), false, true);

        String leftVariable = findVariable(sources.get(0), sources.get(0).getLeft());
        //String rightVariable = findVariable(sources.get(0), sources.get(0).getRight());

        String left = generateParameter(leftVariableValue, leftIndependentTermValue, leftVariable, 0);
        //String right = generateParameter(rightVariableValue, rightIndependentTermValue, rightVariable, 1);

        ThreeAddressCode step = new ThreeAddressCode(left, expandedQuadruples);
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Soma dos termos semelhantes."));

        return steps;
    }

    private String generateParameter(double valueVariable, double valueTermIndependent, String variable, int position) {
        String parameter = "";

        if (valueVariable != 0) {
            if (valueVariable != 1 && valueVariable != -1) {
                if (NumberUtil.isInteger(valueVariable)) {
                    parameter = String.valueOf(NumberUtil.parseInt(valueVariable));
                } else {
                    parameter = String.valueOf(valueVariable).replace(".", ",");
                }
            }

            if (StringUtil.isNotEmpty(variable)) {
                parameter += variable;
            }

            if (valueVariable < 0) {
                parameter = parameter.replace("-", "");
                ExpandedQuadruple negativeVariable = new ExpandedQuadruple("MINUS", parameter, "T" + (expandedQuadruples.size() + 1), position, 0);
                expandedQuadruples.add(negativeVariable);
                parameter = negativeVariable.getResult();
            }
        }

        if (valueTermIndependent != 0) {
            ExpandedQuadruple terms = null;
            if (StringUtil.isNotEmpty(parameter)) {
                terms = new ExpandedQuadruple((valueTermIndependent < 0 ? "-" : "+"), parameter, "T" + (expandedQuadruples.size() + 1), position, 0);
            }

            if (valueTermIndependent != 1 && valueTermIndependent != -1) {
                if (NumberUtil.isInteger(valueTermIndependent)) {
                    parameter = String.valueOf(NumberUtil.parseInt(valueTermIndependent));
                } else {
                    parameter = String.valueOf(valueTermIndependent).replace(".", ",");
                }
            }

            parameter = parameter.replace("-", "");
            if (terms != null) {
                terms.setArgument2(parameter);
                expandedQuadruples.add(terms);
                parameter = terms.getResult();
            } else if (valueVariable < 0) {
                ExpandedQuadruple negativeNumber = new ExpandedQuadruple("MINUS", parameter, "T" + (expandedQuadruples.size() + 1), position, 0);
                expandedQuadruples.add(negativeNumber);
                parameter = negativeNumber.getResult();
            }
        }

        if (StringUtil.isEmpty(parameter))
            return "0";

        return parameter;
    }

    private double sumTerms(ThreeAddressCode threeAddressCode, String param, boolean lastOperationIsMinus, boolean variable) {
        double sum = 0;

        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(param);

            if (expandedQuadruple.isNegative()) {
                sum -= sumTerms(threeAddressCode, expandedQuadruple.getArgument1(), false, variable);
            } else {
                sum += sumTerms(threeAddressCode, expandedQuadruple.getArgument1(), lastOperationIsMinus, variable);
                sum += sumTerms(threeAddressCode, expandedQuadruple.getArgument2(), expandedQuadruple.isMinus(), variable);
            }
        } else {
            if (StringUtil.isVariable(param)) {
                if (!variable)
                    return 0;

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
                if (variable)
                    return 0;

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


    private void sumVariables(List<ThreeAddressCode> sources, String variable) {
        List<ExpandedQuadruple> expandedQuadruple = sources.get(0).getExpandedQuadruples();
        for (int i = 0; i < expandedQuadruple.size()-1; i++) {
            String arg;
            arg = expandedQuadruple.get(i).getArgument1();
            if (!arg.equals(variable)) {
                arg = expandedQuadruple.get(i).getArgument2();
                if (arg.equals(variable)){
                    expandedQuadruple.get(i).setArgument2("");
                }
            }else{
                expandedQuadruple.get(i).setArgument1("");
            }
        }
        String aux;
    //TODO percorrer novamente as quadruplas para realocar as variaveis que estao sozinhas

    }
}
