package br.ifmath.compiler.domain.expertsystem.notableproduct.notableproducts;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.Monomial;
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
        Monomial monomial = new Monomial();
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
            Monomial valueMonomy = new Monomial();
            if (StringUtil.isVariable(value)) {
                valueMonomy.setAttributesFromString(value);
                value = valueMonomy.getCoefficient().toString();
            }

            if (StringUtil.match(value, RegexPattern.INTEGER_NUMBER.toString()))
                monomial.setCoefficient((monomial.getCoefficient() == null) ? Integer.parseInt(value) : monomial.getCoefficient() * Integer.parseInt(value));

            if (isMinus)
                monomial.setCoefficient(monomial.getCoefficient() * -1);

            if (valueMonomy.getLiteral() != null)
                value = valueMonomy.getLiteral();

            if (StringUtil.isVariable(value))
                if (monomial.getLiteral() == null)
                    monomial.setLiteral(value);
                else
                    monomial.setLiteral((monomial.getLiteral().compareTo(value) < 0) ? monomial.getLiteral() + value : value + monomial.getLiteral());
        }
        ExpandedQuadruple startQuadrupleFather = this.source.findQuadrupleByArgument(startBound);
        if (startQuadrupleFather.getArgument1().equals(startBound))
            startQuadrupleFather.setArgument1(endBound);
        else
            startQuadrupleFather.setArgument2(endBound);


        ExpandedQuadruple endBoundQuadruple = this.source.findQuadrupleByResult(endBound);
        if (monomial.getCoefficient() < 0) {
            monomial.setCoefficient(monomial.getCoefficient() * -1);

            startQuadrupleFather.setOperator(MathOperatorUtil.signalRule(startQuadrupleFather.getOperator(), "-"));
        }
        endBoundQuadruple.setArgument1(monomial.toString());

    }

    private boolean isThereAMultiplication(ThreeAddressCode source) {
        for (ExpandedQuadruple expandedQuadruple : source.getExpandedQuadruples()) {
            if (expandedQuadruple.isTimes())
                return true;
        }
        return false;
    }


}
