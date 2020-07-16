package br.ifmath.compiler.domain.expertsystem.notableproduct.notableproducts;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.MathOperatorUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class NotableProductsRuleMultiplication implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return isThereAMultiplication(source.get(0));
    }


    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        this.source = source.get(0);

        while (isThereAMultiplication(this.source)) {
            List<String> multiplicationBounds = new ArrayList<>();
            this.retrieveBoundsAndValue(multiplicationBounds, this.source.findQuadrupleByResult(this.source.getLeft()), true);
            if (!multiplicationBounds.isEmpty()) {
                int startBound = 0, endBound = multiplicationBounds.size() - 1;
                this.multiplyBetweenBounds(multiplicationBounds.get(startBound), multiplicationBounds.get(endBound), multiplicationBounds.subList(startBound + 1, endBound));
            }
            this.source.clearNonUsedQuadruples();
        }
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        List<Step> steps = new ArrayList<>();
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Resolvendo as multiplicações."));
        return steps;
    }

    private void retrieveBoundsAndValue(List<String> resultsList, ExpandedQuadruple iterationQuadruple, boolean isStart) {
        if (isStart) {
            if (iterationQuadruple.isTimes()) {
                resultsList.add(iterationQuadruple.getResult());
                resultsList.add(iterationQuadruple.getArgument1());
                isStart = false;
            }
            this.retrieveBoundsAndValue(resultsList, this.source.findQuadrupleByResult(iterationQuadruple.getArgument2()), isStart);
        } else {
            if (!iterationQuadruple.isTimes()) {
                resultsList.add(iterationQuadruple.getArgument1());
                resultsList.add(iterationQuadruple.getResult());
            } else {
                resultsList.add(iterationQuadruple.getArgument1());
                this.retrieveBoundsAndValue(resultsList, this.source.findQuadrupleByResult(iterationQuadruple.getArgument2()), false);
            }
        }
    }

    private void multiplyBetweenBounds(String startBound, String endBound, List<String> values) {
        NumericValueVariable nvv = new NumericValueVariable();
        for (String value : values) {
            boolean isMinus = false;
            if (StringUtil.match(value, RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple innerQuadruple = source.findQuadrupleByResult(value);
                if (innerQuadruple != null) {
                    if (StringUtil.match(innerQuadruple.getArgument1(), RegexPattern.NATURAL_NUMBER.toString()))
                        value = (innerQuadruple.isNegative()) ? "-" + innerQuadruple.getArgument1() : innerQuadruple.getArgument1();

                    if (StringUtil.match(innerQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()) &&
                            innerQuadruple.isPotentiation()) {
                        ExpandedQuadruple variableQuadruple = this.source.findQuadrupleByResult(innerQuadruple.getArgument1());
                        if (StringUtil.isVariable(variableQuadruple.getArgument1())) {
                            value = variableQuadruple.getArgument1() + "^" + innerQuadruple.getArgument2();
                            isMinus = true;
                        }
                    }

                    if (StringUtil.isVariable(innerQuadruple.getArgument1())) {
                        value = innerQuadruple.getArgument1();
                        isMinus = true;
                    }
                }
            }
            NumericValueVariable numericVariable = new NumericValueVariable();
            if (StringUtil.match(value, RegexPattern.VARIABLE_WITH_COEFICIENT.toString()) || StringUtil.match(value, RegexPattern.VARIABLE_WITH_EXPOENT.toString())) {
                numericVariable.setAttributesFromString(value);
                value = numericVariable.getValue().toString();
            }

            if (StringUtil.match(value, RegexPattern.INTEGER_NUMBER.toString()))
                nvv.setValue((nvv.getValue() == null) ? Integer.parseInt(value) : nvv.getValue() * Integer.parseInt(value));

            if (isMinus)
                nvv.setValue(nvv.getValue() * -1);

            if (numericVariable.getLabel() != null)
                value = numericVariable.getLabel();

            if (StringUtil.isVariable(value))
                if (nvv.getLabel() == null)
                    nvv.setLabel(value);
                else
                    nvv.setLabel((nvv.getLabel().compareTo(value) < 0) ? nvv.getLabel() + value : value + nvv.getLabel());
        }
        ExpandedQuadruple startQuadrupleFather = this.source.findQuadrupleByArgument(startBound);
        if (startQuadrupleFather.getArgument1().equals(startBound))
            startQuadrupleFather.setArgument1(endBound);
        else
            startQuadrupleFather.setArgument2(endBound);


        ExpandedQuadruple endBoundQuadruple = this.source.findQuadrupleByResult(endBound);
        if (nvv.getValue() < 0) {
            nvv.setValue(nvv.getValue() * -1);

            startQuadrupleFather.setOperator(MathOperatorUtil.signalRule(startQuadrupleFather.getOperator(), "-"));
        }
        endBoundQuadruple.setArgument1(nvv.toString());

    }

    private boolean isThereAMultiplication(ThreeAddressCode source) {
        for (ExpandedQuadruple expandedQuadruple : source.getExpandedQuadruples()) {
            if (expandedQuadruple.isTimes())
                return true;
        }
        return false;
    }


}
