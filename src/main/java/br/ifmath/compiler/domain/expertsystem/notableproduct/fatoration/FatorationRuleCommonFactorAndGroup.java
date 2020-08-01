package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FatorationRuleCommonFactorAndGroup implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return FatorationRuleIdentification.isCommonFactor(source.get(0).getRootQuadruple(), source.get(0));
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);

        String commonFactor = this.getCommonFactor();
        if (!commonFactor.isEmpty())
            this.groupCommonFactor(commonFactor);

        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Verificamos o elemento que temos em comum e colocamos em evidência."));
        return steps;
    }

    private String getCommonFactor() {
        NumericValueVariable patternNVV = new NumericValueVariable(this.source.getRootQuadruple().getArgument1());
        return this.getEqualPattern(this.source.getRootQuadruple(), patternNVV.toString());
    }

    private String getEqualPattern(ExpandedQuadruple iterationQuadruple, String pattern) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getEqualPattern(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), pattern);
        }

        NumericValueVariable iterationArgumentNVV = new NumericValueVariable(iterationQuadruple.getArgument1());
        if (StringUtil.match(pattern, RegexPattern.NATURAL_NUMBER.toString())) {
            int numberPattern = Integer.parseInt(pattern);
            //FIXME a lógica é quase isso
            if (numberPattern % iterationArgumentNVV.getValue() != 0) {
                //ja identifica que não é o mesmo padrão. Vai ter um else
            }
        } else if (StringUtil.match(pattern, RegexPattern.VARIABLE.toString())) {
            if (!iterationArgumentNVV.getLabel().contains(pattern))
                //FIXME não retorna.

                // só identifica que não é o mesmo padrão, não retorna. Vai ter um else
                return iterationArgumentNVV.getLabel();

        } else {
            //TODO se for uma variavel com expoente
        }


        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getEqualPattern(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), pattern);
        }

        iterationArgumentNVV = new NumericValueVariable(iterationQuadruple.getArgument2());
        if (iterationArgumentNVV.getLabel().contains(pattern)) {
            return iterationArgumentNVV.getLabel();
        }

        //aqui que deve fazer o return de verdade, ou seja, só depois de passar por todos os valores das quadruplas e
        //estarem iguais pra ele retornar o result do primeiro pattern
        return "";
    }

    private void groupCommonFactor(String commonFactor) {
        this.removeCommonFactor(this.source.getRootQuadruple(), commonFactor);
        this.surroundQuadruplesWithParentheses(this.source.getLeft());
        ExpandedQuadruple newRoot = new ExpandedQuadruple("*", commonFactor, this.source.getLeft(), this.source.retrieveNextTemporary(), 0, 0);
        this.source.getExpandedQuadruples().add(newRoot);
        this.source.setLeft(newRoot.getResult());
    }

    private void surroundQuadruplesWithParentheses(String companionResult) {
        ExpandedQuadruple iterationQuadruple = this.source.findQuadrupleByResult(companionResult);
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.surroundQuadruplesWithParentheses(source.findQuadrupleByResult(iterationQuadruple.getArgument1()).getResult());
        }

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.surroundQuadruplesWithParentheses(source.findQuadrupleByResult(iterationQuadruple.getArgument2()).getResult());
        }
        iterationQuadruple.setLevel(1);
    }

    private void removeCommonFactor(ExpandedQuadruple iterationQuadruple, String commonFactor) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.removeCommonFactor(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), commonFactor);
        }

        if (iterationQuadruple.getArgument1().contains(commonFactor)) {
            iterationQuadruple.setArgument1(
                    (iterationQuadruple.getArgument1().equals(commonFactor)) ? "1" : iterationQuadruple.getArgument1().replace(commonFactor, ""));
        }


        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.removeCommonFactor(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), commonFactor);
        }

        if (iterationQuadruple.getArgument2().contains(commonFactor))
            iterationQuadruple.setArgument2(
                    (iterationQuadruple.getArgument2().equals(commonFactor)) ? "1" : iterationQuadruple.getArgument2().replace(commonFactor, ""));
    }
}
