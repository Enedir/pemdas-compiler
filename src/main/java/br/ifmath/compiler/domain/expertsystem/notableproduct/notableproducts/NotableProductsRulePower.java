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
        return isThereAPowerOperation(source);
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
        for (ExpandedQuadruple expandedQuadruple : this.source.getExpandedQuadruples()) {
            if (!expandedQuadruple.isNegative()) {

                if (expandedQuadruple.isPotentiation()) {
                    String base = this.getPowerBase(expandedQuadruple.getArgument1(), this.source);
                    if (!base.equals("")) {
                        String result;
                        if (StringUtil.matchAny(base.replace("-", ""), RegexPattern.VARIABLE_WITH_COEFICIENT.toString(),
                                RegexPattern.VARIABLE_WITH_EXPOENT.toString())) {
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
                    }
                } else {
                    String result = this.applyPower(expandedQuadruple.getArgument1());
                    if (!result.isEmpty())
                        expandedQuadruple.setArgument1(result);

                    result = this.applyPower(expandedQuadruple.getArgument2());
                    if (!result.isEmpty())
                        expandedQuadruple.setArgument2(result);
                }
            }
        }
    }

    private String applyPower(String argument) {
        String result = "";
        if (!StringUtil.match(argument, RegexPattern.TEMPORARY_VARIABLE.toString()) &&
                this.isThereANumberPower(argument)) {
            String[] splitArgument = argument.split("\\^");
            double poweredValue = Math.pow(Integer.parseInt(splitArgument[0]), Integer.parseInt(splitArgument[1]));
            result = String.valueOf(Math.round(poweredValue));
        }
        return result;
    }

    private boolean isThereAPowerOperation(List<ThreeAddressCode> source) {
        for (ExpandedQuadruple expandedQuadruple : source.get(0).getExpandedQuadruples()) {
            if (!expandedQuadruple.isNegative()) {
                if (!StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()) &&
                        this.isThereANumberPower(expandedQuadruple.getArgument1()))
                    return true;


                if (!StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString()) &&
                        this.isThereANumberPower(expandedQuadruple.getArgument2()))
                    return true;

                if (expandedQuadruple.isPotentiation()) {
                    if (!this.getPowerBase(expandedQuadruple.getArgument1(), source.get(0)).equals("")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String getPowerBase(String argument, ThreeAddressCode source) {
        if (StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString())) {
            return argument;
        }
        ExpandedQuadruple innerQuadruple = source.findQuadrupleByResult(argument);
        if (innerQuadruple != null) {
            if (StringUtil.matchAny(innerQuadruple.getArgument1(), RegexPattern.NATURAL_NUMBER.toString(),
                    RegexPattern.VARIABLE_WITH_COEFICIENT.toString(), RegexPattern.VARIABLE_WITH_EXPOENT.toString())) {
                return (innerQuadruple.isNegative()) ? "-" + innerQuadruple.getArgument1() : innerQuadruple.getArgument1();
            }
        }
        return "";
    }

    private boolean isThereANumberPower(String pattern) {
        String[] splitPattern = pattern.split("\\^");
        if (splitPattern.length > 1) {
            return StringUtil.match(splitPattern[0], RegexPattern.NATURAL_NUMBER.toString());
        }
        return false;
    }

}
