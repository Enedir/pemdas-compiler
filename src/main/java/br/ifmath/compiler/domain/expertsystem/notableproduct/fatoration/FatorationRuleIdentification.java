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
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Identificação do tipo de produto notável a partir da equação inicial: "));
        return steps;
    }

    private String identify() {
        if (this.isCommonFactor(this.source.getRootQuadruple())) {
            return "Fator comum em evidência.";
        }
        return "";
    }

    private boolean isCommonFactor(ExpandedQuadruple iterationQuadruple) {
        NumericValueVariable patternNVV = new NumericValueVariable(iterationQuadruple.getArgument1());
        return this.isThereAEqualPattern(iterationQuadruple, patternNVV.getLabel());
    }

    private boolean isThereAEqualPattern(ExpandedQuadruple iterationQuadruple, String pattern) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.isThereAEqualPattern(this.source.findQuadrupleByResult(iterationQuadruple.getArgument1()), pattern);
        }

        NumericValueVariable iterationArgumentNVV = new NumericValueVariable(iterationQuadruple.getArgument1());
        if (iterationArgumentNVV.getLabel().contains(pattern))
            return true;

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.isThereAEqualPattern(this.source.findQuadrupleByResult(iterationQuadruple.getArgument2()), pattern);
        }

        iterationArgumentNVV = new NumericValueVariable(iterationQuadruple.getArgument2());
        return iterationArgumentNVV.getLabel().contains(pattern);
    }
}
