package br.ifmath.compiler.domain.expertsystem.polynomial;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.NumberUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PolynomialRuleGroupSimilarTerms implements IRule {
    private final List<ExpandedQuadruple> expandedQuadruples;
    private List<NumericValueVariable> termsAndValuesList;
    private boolean visitado = false;

    public PolynomialRuleGroupSimilarTerms() {
        expandedQuadruples = new ArrayList<>();
        termsAndValuesList = new ArrayList<>();
    }


    @Override
    public boolean match(List<ThreeAddressCode> sources) {
        return isThereEquivalentTermsToJoin(sources.get(0).getOperationsFromRight());
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        expandedQuadruples.clear();

        List<Step> steps = new ArrayList<>();

        double rightIndependentTermValue = sumTerms(sources.get(0), sources.get(0).getRight(), false, false);
        sumTerms(sources.get(0), sources.get(0).getRight(), false, true);

        String right = generateParameter(rightIndependentTermValue, sources, 1);

        ThreeAddressCode step = new ThreeAddressCode("x", sources.get(0).getComparison(), right, expandedQuadruples);
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation(), step.toMathNotation(), "Agrupando termos semelhantes."));

        return steps;

    }


    private String findVariable(ThreeAddressCode threeAddressCode, String param) {
        String variable = null;
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(param);

            variable = findVariable(threeAddressCode, expandedQuadruple.getArgument1());

            if (StringUtil.isEmpty(variable)) {
                variable = findVariable(threeAddressCode, expandedQuadruple.getArgument2());
            }
        } else if (isVariable(param)) {
            variable = StringUtil.removeNumericChars(param);
        }

        return variable;
    }


    private boolean isVariable(String param) {
        return StringUtil.matchAny(param, RegexPattern.VARIABLE.toString(), RegexPattern.VARIABLE_WITH_COEFICIENT.toString())
                && !StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString());
    }


    private String generateParameter(double valueTermIndependent, List<ThreeAddressCode> source, int position) {

        //FIXME PLEASE  verificacao com MINUS
        String parameter = "";
        List<NumericValueVariable> list = new ArrayList<>(termsAndValuesList);
        parameter = this.searchOperaThor(list,source);

        return parameter;
    }

    private String searchOperaThor(List<NumericValueVariable> variableList, List<ThreeAddressCode> sources) {
        ExpandedQuadruple expandedQuadruple = sources.get(0).findQuadrupleByArgument2(variableList.get(0).getLabel());
        if (expandedQuadruple != null) {
            return expandedQuadruple.getOperator();
        } else {
            expandedQuadruple = sources.get(0).findQuadrupleByArgument1(variableList.get(0).getLabel());
            if (expandedQuadruple != null) {
                variableList.remove(0);
                variableList.add(0,new NumericValueVariable(expandedQuadruple.getResult()));
                this.searchOperaThor(variableList, sources);
            }
        }
        variableList.remove(0);
        if (!variableList.isEmpty()) {
            return this.searchOperaThor(variableList, sources);
        }

        return "";
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
            if (isVariable(param)) {
                if (!variable)
                    return 0;

                String aux = StringUtil.removeNonNumericChars(param);
                int index = 0;
                int cont = 0;
                if (termsAndValuesList.isEmpty()) {
                    this.termsAndValuesList.add(new NumericValueVariable(param, 0));
                } else {
                    for (int i = 0; i < termsAndValuesList.size(); i++) {
                        if (!termsAndValuesList.get(i).getLabel().equals(param)) {
                            cont++;
                            if (cont == termsAndValuesList.size()) {
                                this.termsAndValuesList.add(new NumericValueVariable(param, 0));
                            }
                        } else {
                            index = i;
                        }
                    }
                }
                if (StringUtil.isEmpty(aux)) {
                    if (lastOperationIsMinus)
                        this.termsAndValuesList.get(index).addValue(-1);
                    else
                        this.termsAndValuesList.get(index).addValue(1);
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


    private boolean isThereEquivalentTermsToJoin(List<ExpandedQuadruple> expandedQuadruples) {
        int countVariable = 0;
        List<String> listaVariaveis = new ArrayList<>();
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (!expandedQuadruple.isPlusOrMinus() || expandedQuadruple.getLevel() != 0) {
                return false;
            }
            if (isVariable(expandedQuadruple.getArgument1())) {
                listaVariaveis.add(expandedQuadruple.getArgument1());
            }
            if (isVariable(expandedQuadruple.getArgument2())) {
                listaVariaveis.add(expandedQuadruple.getArgument2());
            }
        }

        if (listaVariaveis.size() > 1) {
            for (String variavel : listaVariaveis) {
                if (listaVariaveis.contains(variavel)) {
                    countVariable++;
                }
            }
        }
        return (countVariable > 1);
    }
}
