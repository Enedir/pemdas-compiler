package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration;

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

public class FatorationRuleIdentification implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return true;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);

        String correctExplanation = identify();

        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Identificação do tipo de fatoração a partir da equação inicial: " + correctExplanation));
        return steps;
    }

    private String identify() {
        if (isCommonFactor(this.source.getRootQuadruple(), this.source)) {
            return "Fator comum em evidência.";
        }
        return "";
    }

    public static boolean isCommonFactor(ExpandedQuadruple iterationQuadruple, ThreeAddressCode source) {
        String argument = (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
                ? source.findQuadrupleByResult(iterationQuadruple.getArgument1()).getArgument1() : iterationQuadruple.getArgument1();
        NumericValueVariable patternNVV = new NumericValueVariable(argument);
        return isThereAEqualPattern(iterationQuadruple, patternNVV.getLabel(), source);
    }

    private static boolean isThereAEqualPattern(ExpandedQuadruple iterationQuadruple, String pattern, ThreeAddressCode source) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return isThereAEqualPattern(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), pattern, source);
        }

        NumericValueVariable iterationArgumentNVV = new NumericValueVariable(iterationQuadruple.getArgument1());
        if (iterationArgumentNVV.getLabel().contains(pattern))
            return true;

        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return isThereAEqualPattern(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), pattern, source);
        }

        iterationArgumentNVV = new NumericValueVariable(iterationQuadruple.getArgument2());
        return iterationArgumentNVV.getLabel().contains(pattern);
    }
}
