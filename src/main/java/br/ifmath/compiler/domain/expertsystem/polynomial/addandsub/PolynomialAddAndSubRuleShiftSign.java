package br.ifmath.compiler.domain.expertsystem.polynomial.addandsub;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.MathOperatorUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PolynomialAddAndSubRuleShiftSign implements IRule {
    private List<ExpandedQuadruple> expandedQuadruples;
    private boolean isFirstIteration = true;

    public PolynomialAddAndSubRuleShiftSign() {
        this.expandedQuadruples = new ArrayList<>();
    }

    @Override
    public boolean match(List<ThreeAddressCode> source) {

        return isThereAMinusBetweenParentheses(source);
    }

    private boolean isThereAMinusBetweenParentheses(List<ThreeAddressCode> source) {
        ExpandedQuadruple middleQuadruple = source.get(0).findQuadrupleByResult(source.get(0).getLeft());
        return middleQuadruple.isMinus();
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException {
        this.expandedQuadruples = new ArrayList<>();

        ExpandedQuadruple rightPart = source.get(0).findQuadrupleByResult(source.get(0).findQuadrupleByResult(source.get(0).getLeft()).getArgument2());
        changeSign(rightPart, source.get(0), rightPart.getLevel());

        List<Step> steps = new ArrayList<>();

        ThreeAddressCode step = new ThreeAddressCode(source.get(0).getLeft(), "", "", source.get(0).getExpandedQuadruples());

        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Aplicação da regra de troca de sinais em operações prioritárias, em duplas negações ou em somas de números negativos."));

        return steps;
    }

    private void changeSign(ExpandedQuadruple iterationQuadruple, ThreeAddressCode source, int level) {
        String iterationLeft = iterationQuadruple.getArgument1();
        String iterationRight = iterationQuadruple.getArgument2();

        shiftSigns(iterationQuadruple, source, iterationLeft, false);

        isFirstIteration = false;

        shiftSigns(iterationQuadruple, source, iterationRight, true);


    }

    private void shiftSigns(ExpandedQuadruple iterationQuadruple, ThreeAddressCode source, String iterationResult, boolean isArgument2) {
        if (isArgument2) {
            iterationQuadruple.setOperator(MathOperatorUtil.signalRule(iterationQuadruple.getOperator(), "-"));
        }

        if (StringUtil.match(iterationResult, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = source.findQuadrupleByResult(iterationResult);
            if (expandedQuadruple.isNegative()) {
                ExpandedQuadruple currentQuadruple = source.findQuadrupleByArgument1(expandedQuadruple.getResult());
                currentQuadruple.setArgument1(expandedQuadruple.getArgument1());
                ExpandedQuadruple parent = source.findQuadrupleByArgument(currentQuadruple.getResult());
                if (!parent.getResult().equals(source.getLeft()) && parent.getArgument2().equals(currentQuadruple.getResult())) {
                    parent.setOperator("+");
                }
            } else {
                changeSign(expandedQuadruple, source, expandedQuadruple.getLevel());
            }
        } else if (!isFirstIteration && !isArgument2) {
            ExpandedQuadruple newQuadruple = new ExpandedQuadruple("MINUS", iterationResult, "", "T" + (source.getExpandedQuadruples().size() + 1), 0, iterationQuadruple.getLevel());
            source.getExpandedQuadruples().add(newQuadruple);
            source.findQuadrupleByResult(iterationQuadruple.getResult()).setArgument1(newQuadruple.getResult());
            isFirstIteration = false;
        }
    }


}
