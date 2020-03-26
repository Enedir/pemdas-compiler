package br.ifmath.compiler.domain.expertsystem.polynomial.addandsub;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.MathOperatorUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PolynomialAddAndSubRuleShiftSign implements IRule {
    private boolean hasSignsToChange = true;

    public PolynomialAddAndSubRuleShiftSign() {

    }

    @Override
    public boolean match(List<ThreeAddressCode> source) {

        return isThereAMinusBetweenParentheses(source);
    }


    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {


        ExpandedQuadruple root = source.get(0).findQuadrupleByResult(source.get(0).getLeft());
        while (hasSignsToChange) {
            changeSign(root, source.get(0));
        }

        String left = getHigherLevelQuadruples(source, source.get(0).getLeft());

        List<Step> steps = new ArrayList<>();

        source.get(0).clearNonUsedQuadruples();

        ThreeAddressCode step = new ThreeAddressCode(left, "", "", source.get(0).getExpandedQuadruples());

        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Aplicação da regra de troca de sinais em operações prioritárias, em duplas negações ou em somas de números negativos. Também, removendo parenteses dos polinômios."));

        return steps;
    }


    private void changeSign(ExpandedQuadruple iterationQuadruple, ThreeAddressCode source) {
        String iterationLeft = iterationQuadruple.getArgument1();
        String iterationRight = iterationQuadruple.getArgument2();

        hasSignsToChange = false;

        String highestLevelQuadrupleResult = findHighestLevelQuadruple(source, true);
        if (!highestLevelQuadrupleResult.isEmpty()) {
            shiftSigns(source, iterationLeft, highestLevelQuadrupleResult, true);
            hasSignsToChange = true;
        }

        highestLevelQuadrupleResult = findHighestLevelQuadruple(source, false);
        if (!highestLevelQuadrupleResult.isEmpty()) {
            shiftSigns(source, iterationRight, highestLevelQuadrupleResult, false);
            hasSignsToChange = true;
        }
    }

    private String findHighestLevelQuadruple(ThreeAddressCode source, boolean isIterationLeft) {
        int highestLevel = 1;
        String highestLevelExpQuadResult = "";
        for (ExpandedQuadruple expandedQuadruple : source.getExpandedQuadruples()) {
            if (expandedQuadruple.getLevel() > highestLevel) {
                highestLevel = expandedQuadruple.getLevel();
                highestLevelExpQuadResult = expandedQuadruple.getResult();
            }
        }

        ExpandedQuadruple root = source.findQuadrupleByResult(source.getLeft());
        String expandedQuadrupleOrigin = (isIterationLeft) ? root.getArgument1() : root.getArgument2();
        if (!isQuadrupleInDesiredTreeSide(highestLevelExpQuadResult, expandedQuadrupleOrigin, root.getResult(), source)) {
            return "";
        }

        ExpandedQuadruple father = source.findQuadrupleByArgument(highestLevelExpQuadResult);
        if (father.getArgument1().equals(highestLevelExpQuadResult)) {
            ExpandedQuadruple grandFather = source.findQuadrupleByArgument(father.getResult());
            if (grandFather.isPlus())
                return "";
        } else {
            if (father.isPlus())
                return "";
        }

        return highestLevelExpQuadResult;
    }

    private boolean isQuadrupleInDesiredTreeSide(String iterationResult, String expandedQuadrupleOrigin, String root, ThreeAddressCode source) {
        if (!iterationResult.equals(root)) {
            if (iterationResult.equals(expandedQuadrupleOrigin)) {
                return true;
            } else {
                String father = source.findQuadrupleByArgument(iterationResult).getResult();
                return isQuadrupleInDesiredTreeSide(father, expandedQuadrupleOrigin, root, source);
            }
        } else {
            return false;
        }
    }

    private void shiftSigns(ThreeAddressCode source, String iterationResult, String highestLevelResult, boolean isLeft) {

        ExpandedQuadruple actualQuadruple = source.findQuadrupleByResult(iterationResult);
        if (StringUtil.match(actualQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            if (actualQuadruple.getArgument1().equals(highestLevelResult)) {
                changeQuadrupleSigns(source, actualQuadruple);
                source.findQuadrupleByArgument(iterationResult).setOperator("+");

                ExpandedQuadruple son = source.findQuadrupleByResult(actualQuadruple.getArgument1());
                son.setLevel(son.getLevel() - 1);

            } else
                shiftSigns(source, actualQuadruple.getArgument1(), highestLevelResult, true);
        }


        if (StringUtil.match(actualQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            if (actualQuadruple.getArgument2().equals(highestLevelResult)) {
                changeQuadrupleSigns(source, source.findQuadrupleByResult(actualQuadruple.getArgument2()));
                actualQuadruple.setOperator("+");
                ExpandedQuadruple son = source.findQuadrupleByResult(actualQuadruple.getArgument2());
                son.setLevel(son.getLevel() - 1);

            } else
                shiftSigns(source, actualQuadruple.getArgument2(), highestLevelResult, false);
        }
    }

    private void changeQuadrupleSigns(ThreeAddressCode source, ExpandedQuadruple iterationQuadruple) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.changeQuadrupleSigns(source, source.findQuadrupleByResult(iterationQuadruple.getArgument1()));
        }

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.changeQuadrupleSigns(source, source.findQuadrupleByResult(iterationQuadruple.getArgument2()));
        }

        if (iterationQuadruple.isNegative()) {
            ExpandedQuadruple father = source.findQuadrupleByArgument(iterationQuadruple.getArgument1());
            if (father.getArgument1().equals(iterationQuadruple.getResult()))
                father.setArgument1(iterationQuadruple.getArgument1());
            else
                father.setArgument2(iterationQuadruple.getArgument1());
        } else {
            if (!StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple newQuadruple = new ExpandedQuadruple("MINUS", iterationQuadruple.getArgument1(), null, "T" + (Integer.parseInt(source.getLeft().replaceFirst("T", "")) + 1), 0, iterationQuadruple.getLevel() - 1);
                source.getExpandedQuadruples().add(newQuadruple);
                source.findQuadrupleByResult(iterationQuadruple.getResult()).setArgument1(newQuadruple.getResult());
            }
            iterationQuadruple.setOperator(MathOperatorUtil.signalRule(iterationQuadruple.getOperator(), "-"));
        }


    }

    private String getHigherLevelQuadruples(List<ThreeAddressCode> source, String result) {
        ExpandedQuadruple expandedQuadruple = source.get(0).findQuadrupleByResult(result);

        if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.getHigherLevelQuadruples(source, expandedQuadruple.getArgument1());
        }

        if (StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.getHigherLevelQuadruples(source, expandedQuadruple.getArgument2());
        }

        if (expandedQuadruple.getLevel() > 0) {
            if (expandedQuadruple.isNegative()) {
                ExpandedQuadruple parent = source.get(0).findQuadrupleByArgument(expandedQuadruple.getResult());
                ExpandedQuadruple operationParent = getParentWithOperationToChange(source.get(0), expandedQuadruple);
                operationParent.setOperator(MathOperatorUtil.signalRule(operationParent.getOperator(), "-"));
                if (parent.getArgument1().equals(expandedQuadruple.getResult()))
                    parent.setArgument1(expandedQuadruple.getArgument1());
                else
                    parent.setArgument2(expandedQuadruple.getArgument1());
            }
        }
        expandedQuadruple.setLevel(0);

        return expandedQuadruple.getResult();
    }

    private ExpandedQuadruple getParentWithOperationToChange(ThreeAddressCode source, ExpandedQuadruple expandedQuadruple) {
        ExpandedQuadruple parent = source.findQuadrupleByArgument(expandedQuadruple.getResult());
        if (parent.getArgument2().equals(expandedQuadruple.getResult()))
            return parent;
        else
            return getParentWithOperationToChange(source, parent);
    }


    private boolean isThereAMinusBetweenParentheses(List<ThreeAddressCode> source) {
        ExpandedQuadruple middleQuadruple = source.get(0).findQuadrupleByResult(source.get(0).getLeft());
        return middleQuadruple.isMinus();
    }

}
