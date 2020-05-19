package br.ifmath.compiler.domain.expertsystem.polynomial.multiplication;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PolynomialMultiplicationRuleSortSimilarTerms implements IRule {

    private ThreeAddressCode source;
    private boolean hasElementsToSort = true;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return isThereTermsToSort(source.get(0), source.get(0).findQuadrupleByResult(source.get(0).getLeft()), -1);
    }


    @Override
    public List<Step> handle(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException {
        this.source = source.get(0);

        boolean hasElementsToSort = true;
        while (hasElementsToSort) {
            if (isThereTermsToSort(source.get(0), source.get(0).findQuadrupleByResult(source.get(0).getLeft()), -1))
                sortQuadruples(this.source.findQuadrupleByResult(this.source.getLeft()), -1);
            else
                hasElementsToSort = false;
        }
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        List<Step> steps = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Agrupando os termos semelhantes."));

        return steps;
    }

    private boolean sortQuadruples(ExpandedQuadruple iterationQuadruple, int lastPower) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            return sortQuadruples(this.source.findQuadrupleByResult(iterationQuadruple.getArgument1()), lastPower);


        NumericValueVariable numericValueVariable = new NumericValueVariable();
        numericValueVariable.setAttributesFromString(iterationQuadruple.getArgument1());
        int power = numericValueVariable.getLabelPower();

        ExpandedQuadruple argument1 = iterationQuadruple;
        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());

        String argument2 = iterationQuadruple.getResult();
        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            argument2 = iterationQuadruple.getArgument2();

        lastPower = swapArgumentWithHigherPower(argument1, power, argument2);

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            return sortQuadruples(this.source.findQuadrupleByResult(iterationQuadruple.getArgument2()), lastPower);

        return true;
    }

    private int swapArgumentWithHigherPower(ExpandedQuadruple originalQuadruple, int lastPower, String iterationValue) {
        ExpandedQuadruple expandedQuadruple = this.source.findQuadrupleByResult(iterationValue);

        if (originalQuadruple.equals(expandedQuadruple) && StringUtil.isVariable(expandedQuadruple.getArgument2()))
            return swapLastArgument(originalQuadruple, lastPower, expandedQuadruple);

        NumericValueVariable numericValueVariable = new NumericValueVariable();
        numericValueVariable.setAttributesFromString(expandedQuadruple.getArgument1());
        int power = numericValueVariable.getLabelPower();
        if (power > lastPower) {
            String argument1 = originalQuadruple.getArgument1();
            originalQuadruple.setArgument1(expandedQuadruple.getArgument1());
            expandedQuadruple.setArgument1(argument1);
            String originalFatherOperator = "+";
            ExpandedQuadruple originalFather = this.source.findDirectFather(originalQuadruple.getResult());

            if (originalQuadruple.isNegative())
                originalFatherOperator = "-";
            else {
                if (originalFather != null) {
                    originalFatherOperator = originalFather.getOperator();
                }
            }

            ExpandedQuadruple quadrupleFather = this.source.findDirectFather(expandedQuadruple.getResult());
            if (!originalFatherOperator.equals(quadrupleFather.getOperator())) {
                if (originalQuadruple.isNegative())
                    this.source.replaceFatherArgumentForSons(originalQuadruple, 1);
                else {

                    if (originalFather == null)
                        this.source.addQuadrupleToList("MINUS", originalQuadruple.getArgument1(), "", originalQuadruple, true);
                    else
                        originalFather.setOperator(quadrupleFather.getOperator());
                }
                quadrupleFather.setOperator(originalFatherOperator);
            }

            return power;
        }

        if (StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            return swapArgumentWithHigherPower(originalQuadruple, lastPower, expandedQuadruple.getArgument2());
        return swapLastArgument(originalQuadruple, lastPower, expandedQuadruple);
    }

    private int swapLastArgument(ExpandedQuadruple originalQuadruple, int lastPower, ExpandedQuadruple iterationQuadruple) {
        NumericValueVariable numericValueVariable = new NumericValueVariable();
        numericValueVariable.setAttributesFromString(iterationQuadruple.getArgument2());
        int power = numericValueVariable.getLabelPower();
        if (power > lastPower) {
            String argument1 = originalQuadruple.getArgument1();
            originalQuadruple.setArgument1(iterationQuadruple.getArgument2());
            iterationQuadruple.setArgument2(argument1);
            String originalFatherOperator = "+";
            ExpandedQuadruple originalFather = this.source.findDirectFather(originalQuadruple.getResult());
            if (originalQuadruple.isNegative())
                originalFatherOperator = "-";
            else {
                if (originalFather != null) {
                    originalFatherOperator = originalFather.getOperator();
                }
            }

            if (!originalFatherOperator.equals(iterationQuadruple.getOperator())) {
                if (originalQuadruple.isNegative())
                    this.source.replaceFatherArgumentForSons(originalQuadruple, 1);
                else {
                    if (originalFather == null)
                        this.source.addQuadrupleToList("MINUS", originalQuadruple.getArgument1(), "", originalQuadruple, true);
                    else
                        originalFather.setOperator(iterationQuadruple.getOperator());
                }
                iterationQuadruple.setOperator(originalFatherOperator);
            }
            return power;
        }
        return lastPower;
    }

    private boolean isThereTermsToSort(ThreeAddressCode source, ExpandedQuadruple iterationQuadruple, int lastPower) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            return isThereTermsToSort(source, source.findQuadrupleByResult(iterationQuadruple.getArgument1()), lastPower);

        NumericValueVariable numericValueVariable = new NumericValueVariable();
        numericValueVariable.setAttributesFromString(iterationQuadruple.getArgument1());
        int power = numericValueVariable.getLabelPower();
        if (power <= lastPower || lastPower == -1)
            lastPower = power;
        else
            return true;
        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());


        //caso só tenha uma quadrupla, com um único valor e MINUS
        if (iterationQuadruple != null) {

            //caso só tenha uma quadrupla, com um único valor
            if (!iterationQuadruple.getArgument2().equals("")) {
                if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString()))
                    return isThereTermsToSort(source, source.findQuadrupleByResult(iterationQuadruple.getArgument2()), lastPower);

                numericValueVariable.setAttributesFromString(iterationQuadruple.getArgument2());
                return numericValueVariable.getLabelPower() > lastPower;
            }
        }
        return false;
    }
}
