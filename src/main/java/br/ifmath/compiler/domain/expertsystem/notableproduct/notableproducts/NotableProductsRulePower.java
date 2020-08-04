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

public class NotableProductsRulePower implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return true;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        this.source = source.get(0);

        this.numbersPower();
        this.adjustNegativeArgument();

        this.source.clearNonUsedQuadruples();
        this.adjustFirstArgument();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        List<Step> steps = new ArrayList<>();
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Resolvendo as operações de potência."));

        return steps;
    }

    private void adjustNegativeArgument() {
        ExpandedQuadruple root = this.source.findQuadrupleByResult(this.source.getLeft());
        if (root.getArgument1().startsWith("-")) {
            this.source.addQuadrupleToList("MINUS", root.getArgument1().replace("-", ""), "", root, true);
        }

    }

    private void adjustFirstArgument() {

        ExpandedQuadruple root = this.source.findQuadrupleByResult(this.source.getLeft());
        if (StringUtil.match(root.getArgument1(), RegexPattern.INTEGER_NUMBER.toString()) &&
                Integer.parseInt(root.getArgument1()) < 0)
            this.source.addQuadrupleToList("MINUS", root.getArgument1().replace("-", ""), "", root, true);
    }

    private void numbersPower() {
        for (int i = 0; i < this.source.getExpandedQuadruples().size(); i++) {
            ExpandedQuadruple expandedQuadruple = this.source.getExpandedQuadruples().get(i);

            if (!expandedQuadruple.isNegative()) {

                if (expandedQuadruple.isPotentiation()) {
                    String base = this.getPowerBase(expandedQuadruple.getArgument1(), this.source);
                    if (!base.equals("")) {
                        String result;
                        if (StringUtil.matchAny(base.replace("-", ""), RegexPattern.VARIABLE_WITH_COEFFICIENT.toString(),
                                RegexPattern.VARIABLE_WITH_EXPONENT.toString())) {
                            NumericValueVariable nvv = new NumericValueVariable();
                            nvv.setAttributesFromString(base);
                            String exponent = expandedQuadruple.getArgument2();
                            nvv.setValue(
                                    (int) Math.pow(nvv.getValue(), Double.parseDouble(exponent)));
                            int power = nvv.getLabelPower();
                            if (power != 1)
                                exponent = String.valueOf(power * Integer.parseInt(expandedQuadruple.getArgument2()));
                            if (nvv.getLabel().contains("^")) {
                                nvv.setLabelPower(exponent);
                            } else {
                                nvv.setLabel(nvv.getLabel() + "^" + exponent);
                            }
                            result = nvv.toString();
                        } else
                            result = String.valueOf(
                                    Math.round(
                                            Math.pow(Double.parseDouble(base), Double.parseDouble(expandedQuadruple.getArgument2()))
                                    )
                            );
                        ExpandedQuadruple father = this.source.findQuadrupleByArgument(expandedQuadruple.getResult());
                        if (father.getArgument1().equals(expandedQuadruple.getResult()))
                            father.setArgument1(result);
                        else
                            father.setArgument2(result);
                    } else {
                        ExpandedQuadruple innerQuadruple = this.source.findQuadrupleByResult(expandedQuadruple.getArgument1());
                        String basePower = innerQuadruple.getArgument1();
                        if (innerQuadruple.isNegative())
                            basePower = innerQuadruple.getResult();
                        String argument = this.applyPower(basePower, expandedQuadruple.getArgument2());
                        ExpandedQuadruple father = this.source.findQuadrupleByArgument(expandedQuadruple.getResult());
                        if (father.getArgument1().equals(expandedQuadruple.getResult())) {
                            father.setArgument1(argument);
                        } else {
                            father.setArgument2(argument);
                        }
                    }
                }
            }
        }
    }

    private String applyPower(String argument, String exponent) {
        if (StringUtil.match(argument, RegexPattern.TEMPORARY_VARIABLE.toString()))
            return applyMinusPower(this.source.findQuadrupleByResult(argument).getArgument1(), exponent);
        if (StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString())) {
            return String.valueOf(Math.round(Math.pow(Double.parseDouble(argument), Double.parseDouble(exponent))));
        }
        return argument + "^" + exponent;
    }

    private String applyMinusPower(String argument, String exponent) {
        if (exponent.equals("3")) {
            String result = this.source.retrieveNextTemporary();
            this.source.getExpandedQuadruples().add(new ExpandedQuadruple("MINUS", argument + "^3", "", result, 0, 1));
            return result;
        }
        return argument + "^2";
    }

    private String getPowerBase(String argument, ThreeAddressCode source) {
        if (StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString())) {
            return argument;
        }
        ExpandedQuadruple innerQuadruple = source.findQuadrupleByResult(argument);
        if (innerQuadruple != null) {
            if (innerQuadruple.getArgument1().equals("")) {
                innerQuadruple.setArgument1(innerQuadruple.getOperator());
                innerQuadruple.setOperator("");
            }
            if (StringUtil.matchAny(innerQuadruple.getArgument1(), RegexPattern.NATURAL_NUMBER.toString(),
                    RegexPattern.VARIABLE_WITH_COEFFICIENT.toString(), RegexPattern.VARIABLE_WITH_EXPONENT.toString())) {
                return (innerQuadruple.isNegative()) ? "-" + innerQuadruple.getArgument1() : innerQuadruple.getArgument1();
            }
        }
        return "";
    }

}
