package br.ifmath.compiler.domain.expertsystem.polynomial.addandsub;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.MathOperatorUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.*;

public class PolynomialAddAndSubRuleShiftSign implements IRule {
    ThreeAddressCode source;

    public PolynomialAddAndSubRuleShiftSign() {
    }

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return true;
    }


    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) {
        String reason = "Removendo parênteses dos polinômios";
        this.source = sources.get(0);

        ExpandedQuadruple root = this.source.findQuadrupleByResult(this.source.getLeft());
        ExpandedQuadruple sideQuadruple = this.getQuadrupleWithMinus(this.source.findQuadrupleByResult(root.getArgument1()));
        for (int i = 0; i < 2; i++) {
            if (sideQuadruple != null) {
                this.changeSign(sideQuadruple, true);
                reason = "Aplicando regra de troca de sinais em operações prioritárias, em duplas negações ou em somas de números negativos.Também, removendo parenteses dos polinômios.";
            }
            sideQuadruple = this.getQuadrupleWithMinus(root);
        }
        this.handleParentheses();

        this.source.clearNonUsedQuadruples();

        List<Step> steps = new ArrayList<>();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), "", "", this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), reason));

        return steps;
    }

    private void changeSign(ExpandedQuadruple iterationQuadruple, boolean itHasToChange) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.changeSign(this.source.findQuadrupleByResult(iterationQuadruple.getArgument1()), itHasToChange);
        }

        if (iterationQuadruple.isPlus() && itHasToChange) {
            iterationQuadruple.setOperator("-");
        } else if (iterationQuadruple.isMinus() && itHasToChange) {

            if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString()))
                itHasToChange = false;
            iterationQuadruple.setOperator("+");

        } else if (iterationQuadruple.isMinus() && !itHasToChange)
            itHasToChange = true;

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.changeSign(this.source.findQuadrupleByResult(iterationQuadruple.getArgument2()), itHasToChange);
        }


    }

    private void handleParentheses() {
        for (ExpandedQuadruple expandedQuadruple : this.source.getExpandedQuadruples()) {

            if (expandedQuadruple.getLevel() != -1) {
                if (expandedQuadruple.isNegative())
                    this.handleMinusParentheses(expandedQuadruple);

                if (expandedQuadruple.isMinus() && StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    this.handleSubParentheses(expandedQuadruple);
                }
            }
        }

        for (ExpandedQuadruple expandedQuadruple : this.source.getExpandedQuadruples()) {
            expandedQuadruple.setLevel(0);
        }
    }

    private void handleSubParentheses(ExpandedQuadruple expandedQuadruple) {
        ExpandedQuadruple son = this.source.findQuadrupleByResult(expandedQuadruple.getArgument2());
        son = findDirectSon(son);
        if (son.isNegative()) {
            expandedQuadruple.setOperator(MathOperatorUtil.signalRule(expandedQuadruple.getOperator(), "-"));
            this.source.replaceFatherArgumentForSons(son, 1);

            son.setLevel(-1);
            expandedQuadruple.setLevel(-1);
        }

    }

    private ExpandedQuadruple findDirectSon(ExpandedQuadruple iterationQuadruple) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple son = this.source.findQuadrupleByResult(iterationQuadruple.getArgument1());
            return this.findDirectSon(son);
        }
        return iterationQuadruple;
    }

    private void handleMinusParentheses(ExpandedQuadruple expandedQuadruple) {
        ExpandedQuadruple grandfather = this.findDirectFather(expandedQuadruple);
        grandfather.setOperator(MathOperatorUtil.signalRule(grandfather.getOperator(), "-"));
        this.source.replaceFatherArgumentForSons(expandedQuadruple, 1);

        grandfather.setLevel(-1);
        expandedQuadruple.setLevel(-1);
    }

    private ExpandedQuadruple findDirectFather(ExpandedQuadruple iterationQuadruple) {
        ExpandedQuadruple father = this.source.findQuadrupleByArgument(iterationQuadruple.getResult());
        if (father.getArgument1().equals(iterationQuadruple.getResult())) {
            return this.findDirectFather(father);
        }
        return father;

    }


    private ExpandedQuadruple getQuadrupleWithMinus(ExpandedQuadruple iterationQuadruple) {

        if (iterationQuadruple.isMinus() && StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.source.findQuadrupleByResult(iterationQuadruple.getArgument2());
        }

        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return getQuadrupleWithMinus(this.source.findQuadrupleByResult(iterationQuadruple.getArgument1()));
        }

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return getQuadrupleWithMinus(this.source.findQuadrupleByResult(iterationQuadruple.getArgument2()));
        }

        return null;
    }

}

