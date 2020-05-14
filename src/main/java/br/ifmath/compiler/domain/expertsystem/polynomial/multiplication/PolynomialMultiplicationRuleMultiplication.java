package br.ifmath.compiler.domain.expertsystem.polynomial.multiplication;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PolynomialMultiplicationRuleMultiplication implements IRule {

    private ThreeAddressCode source;


    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return true;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        this.source = source.get(0);

        multiply();
        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        List<Step> steps = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Multiplica-se os coeficientes, considerando a regra dos sinais, " +
                "e para as variáveis, somam-se os expoentes pela propriedade das potências."));

        return steps;
    }

    private void multiply() {
        List<ExpandedQuadruple> iterationList = new ArrayList<>(this.source.getExpandedQuadruples());
        for (ExpandedQuadruple expandedQuadruple : iterationList) {
            if (expandedQuadruple.isTimes()) {
                boolean isMinus = false;
                String argument1 = expandedQuadruple.getArgument1();
                if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    ExpandedQuadruple innerQuadruple = this.source.findQuadrupleByResult(expandedQuadruple.getArgument1());
                    argument1 = innerQuadruple.getArgument1();
                    isMinus = true;
                }

                String argument2 = expandedQuadruple.getArgument2();
                if (StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    ExpandedQuadruple innerQuadruple = this.source.findQuadrupleByResult(expandedQuadruple.getArgument2());
                    argument2 = innerQuadruple.getArgument1();
                    isMinus = !isMinus;
                }
                String result = this.multiplyQuadrupleArguments(argument1, argument2);


                if (expandedQuadruple.getResult().equals(this.source.getLeft())) {
                    if (isMinus) {
                        expandedQuadruple.setOperator("MINUS");
                    } else
                        expandedQuadruple.setOperator("");
                    expandedQuadruple.setArgument1(result);
                    expandedQuadruple.setArgument2("");
                } else {
                    ExpandedQuadruple father = this.source.findQuadrupleByArgument(expandedQuadruple.getResult());
                    if (father.getArgument1().equals(expandedQuadruple.getResult())) {
                        father.setArgument1(result);
                        if (isMinus) {
                            if (father.getResult().equals(this.source.getLeft())) {
                                this.source.addQuadrupleToList("MINUS", father.getArgument1(), "", father, true);
                            } else
                                this.findArgumentFather(father).setOperator("-");
                        }
                    } else {
                        father.setArgument2(result);
                        if (isMinus)
                            father.setOperator("-");
                    }
                }

            }
        }

    }

    private String multiplyQuadrupleArguments(String quadrupleArgument1, String quadrupleArgument2) {

        NumericValueVariable argument1 = new NumericValueVariable();
        argument1.setAttributesFromString(quadrupleArgument1);
        NumericValueVariable argument2 = new NumericValueVariable();
        argument2.setAttributesFromString(quadrupleArgument2);

        NumericValueVariable product = new NumericValueVariable();

        product.setValue(argument1.getValue() * argument2.getValue());

        if (argument1.getLabel().equals("") && argument2.getLabel().equals(""))
            product.setLabel("");
        else if (!argument1.getLabel().equals("") && !argument2.getLabel().equals("")) {
            int power = argument1.getLabelPower() + argument2.getLabelPower();
            String label = StringUtil.removeNonAlphanumericChars(StringUtil.removeNumericChars(argument1.getLabel()));
            product.setLabel(label + "^" + power);
        } else {
            if (argument1.getLabel().equals(""))
                product.setLabel(argument2.getLabel());
            else
                product.setLabel(argument1.getLabel());
        }
        if (product.getValue() == 1 && !product.getLabel().equals(""))
            return product.getLabel();
        return product.getValue() + product.getLabel();
    }


    private ExpandedQuadruple findArgumentFather(ExpandedQuadruple expandedQuadruple) {
        ExpandedQuadruple father = this.source.findQuadrupleByArgument(expandedQuadruple.getResult());
        if (father.getArgument1().equals(expandedQuadruple.getResult())) {
            return this.findArgumentFather(father);
        }
        return father;
    }

}
