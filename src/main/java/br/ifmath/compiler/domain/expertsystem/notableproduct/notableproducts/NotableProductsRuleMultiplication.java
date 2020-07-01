package br.ifmath.compiler.domain.expertsystem.notableproduct.notableproducts;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
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
            if (StringUtil.match(value, RegexPattern.NATURAL_NUMBER.toString()))
                nvv.setValue((nvv.getValue() == null) ? Integer.parseInt(value) : nvv.getValue() * Integer.parseInt(value));

            if (StringUtil.isVariable(value))
                if (nvv.getLabel() == null)
                    nvv.setLabel(value);
                else
                    nvv.setLabel((nvv.getLabel().compareTo(value) < 0) ? nvv.getLabel() + value : value + nvv.getLabel());
        }
        ExpandedQuadruple endBoundQuadruple = this.source.findQuadrupleByResult(endBound);
        endBoundQuadruple.setArgument1(nvv.toString());

        ExpandedQuadruple startQuadrupleFather = this.source.findQuadrupleByArgument(startBound);
        if (startQuadrupleFather.getArgument1().equals(startBound))
            startQuadrupleFather.setArgument1(endBound);
        else
            startQuadrupleFather.setArgument2(endBound);

    }

    private boolean isThereAMultiplication(ThreeAddressCode source) {
        for (ExpandedQuadruple expandedQuadruple : source.getExpandedQuadruples()) {
            if (expandedQuadruple.isTimes())
                return true;
        }
        return false;
    }


}
