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


        ThreeAddressCode step = new ThreeAddressCode(source.get(0).getLeft(), source.get(0).getComparison(), source.get(0).getRight(), expandedQuadruples);

        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation(), step.toMathNotation(), "Aplicação da regra de sinais em operações prioritárias, em duplas negações ou em somas de números negativos."));

        return steps;
    }

    private void changeSign(ExpandedQuadruple iterationQuadruple, ThreeAddressCode source, int level) {
        String iterationLeft = iterationQuadruple.getArgument1();
        String iterationRight = iterationQuadruple.getArgument2();

        shiftSigns(iterationQuadruple, source, level, iterationLeft);

        isFirstIteration = false;

        shiftSigns(iterationQuadruple, source, level, iterationRight);


    }

    private void shiftSigns(ExpandedQuadruple iterationQuadruple, ThreeAddressCode source, int level, String iterationResult) {
        if (StringUtil.match(iterationResult, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = source.findQuadrupleByResult(iterationResult);
            if (expandedQuadruple.isNegative()) {
                ExpandedQuadruple parent = source.findQuadrupleByArgument1(expandedQuadruple.getResult());
                parent.setArgument1(expandedQuadruple.getArgument1());
                source.findQuadrupleByResult(source.getLeft()).setOperator("+");
                source.getExpandedQuadruples().remove(expandedQuadruple);
            } else {
                changeSign(expandedQuadruple, source, expandedQuadruple.getLevel());
                changeSign(expandedQuadruple, source, expandedQuadruple.getLevel());
            }
        } else if (isFirstIteration) {
            ExpandedQuadruple newQuadruple = new ExpandedQuadruple("MINUS", iterationResult, "", "T" + (source.getExpandedQuadruples().size() + 1), 0, level);
            source.getExpandedQuadruples().add(newQuadruple);
            source.findQuadrupleByResult(iterationQuadruple.getResult()).setArgument1(newQuadruple.getResult());
            isFirstIteration = false;

        } else {
            iterationQuadruple.setOperator(MathOperatorUtil.signalRule(iterationQuadruple.getOperator(), "-"));

        }
    }


}
