package br.ifmath.compiler.domain.expertsystem.notableproduct.notableproducts;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
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

        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        List<Step> steps = new ArrayList<>();
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Resolvendo as operações de potência."));

        return steps;
    }

    private void numbersPower() {
        for (ExpandedQuadruple expandedQuadruple : source.getExpandedQuadruples()) {
            String result = this.applyPower(expandedQuadruple.getArgument1());
            if (!result.isEmpty())
                expandedQuadruple.setArgument1(result);

            result = this.applyPower(expandedQuadruple.getArgument2());
            if (!result.isEmpty())
                expandedQuadruple.setArgument2(result);
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
            if (!StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()) &&
                    this.isThereANumberPower(expandedQuadruple.getArgument1()))
                return true;


            if (!StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString()) &&
                    this.isThereANumberPower(expandedQuadruple.getArgument2()))
                return true;
        }
        return false;
    }

    private boolean isThereANumberPower(String pattern) {
        String[] splitPattern = pattern.split("\\^");
        if (splitPattern.length > 1) {
            return StringUtil.match(splitPattern[0], RegexPattern.NATURAL_NUMBER.toString());
        }
        return false;
    }

}
