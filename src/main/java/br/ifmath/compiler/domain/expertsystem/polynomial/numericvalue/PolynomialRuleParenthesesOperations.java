package br.ifmath.compiler.domain.expertsystem.polynomial.numericvalue;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PolynomialRuleParenthesesOperations implements IRule {
    private List<ExpandedQuadruple> expandedQuadruples;
    private List<Step> steps = new ArrayList<>();


    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return isThereOperationsBetweenParentheses(source.get(0).getExpandedQuadruples());
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException {
        expandedQuadruples = new ArrayList<>();
        expandedQuadruples.addAll(source.get(0).getExpandedQuadruples());

        resolveInnerOperations(source);
        clearNonUsedQuadruples();

        return steps;
    }

    private void clearNonUsedQuadruples() {
        expandedQuadruples.removeIf(expandedQuadruple -> expandedQuadruple.getLevel() != 0);
    }

    private void resolveInnerOperations(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException {
        for (int i = 0; i < expandedQuadruples.size() - 1; i++) {
            ExpandedQuadruple expandedQuadruple = expandedQuadruples.get(i);
            if (expandedQuadruple.getLevel() != 0) {
                i += selectCorrectOperation(expandedQuadruple, source);
            }
        }
    }

    private int selectCorrectOperation(ExpandedQuadruple expandedQuadruple, List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {

        IRule operation;
        if (expandedQuadruple.isPotentiation()) {
            operation = new PolynomialRuleNumbersPotentiation();
        } else if (expandedQuadruple.isTimes()) {
            operation = new PolynomialRuleMultiplyNumbers();
        } else {
            operation = new PolynomialRuleSumNumbers();
        }

        List<ThreeAddressCode> operationsSource = new ArrayList<>();
        List<ExpandedQuadruple> sameLevelQuadruples = getQuadruplesInSameLevel(expandedQuadruple, sources);
        String lastQuadResult = sameLevelQuadruples.get(sameLevelQuadruples.size() - 1).getResult();
        operationsSource.add(new ThreeAddressCode(lastQuadResult, sameLevelQuadruples));
        List<Step> parenthesesOperation = operation.handle(operationsSource);

        //Verificar se esse if está certo
        String result;
        if (StringUtil.match(parenthesesOperation.get(0).getSource().get(0).getLeft(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            result = parenthesesOperation.get(0).getSource().get(0).getExpandedQuadruples().get(0).getArgument1();
        } else {
            result = parenthesesOperation.get(0).getMathExpression();
        }

        ExpandedQuadruple aux = sources.get(0).findQuadrupleByArgument1(lastQuadResult);

        if (aux == null) {
            aux = sources.get(0).findQuadrupleByArgument2(lastQuadResult);
            aux.setArgument2(result);
        } else {
            aux.setArgument1(result);
        }
        sources.get(0).getExpandedQuadruples().removeAll(sameLevelQuadruples);
        Step lastStep = parenthesesOperation.get(parenthesesOperation.size() - 1);
        lastStep.setSource(sources);
        lastStep.setLatexExpression(sources.get(0).toLaTeXNotation().trim());
        lastStep.setMathExpression(sources.get(0).toMathNotation().trim());
        lastStep.setReason(lastStep.getReason().replace(".", " dentro dos parênteses."));
        steps.add(lastStep);
        //retorna o passo do for que esse metodo esta, para avançar caso pegue mais de uma quadrupla por vez
        return sameLevelQuadruples.size() - 1;
    }

    private List<ExpandedQuadruple> getQuadruplesInSameLevel(ExpandedQuadruple expandedQuadruple, List<ThreeAddressCode> sources) {
        List<ExpandedQuadruple> expandedQuadruples = new ArrayList<>();
        for (ExpandedQuadruple sourceQuadruple : sources.get(0).getExpandedQuadruples()) {
            if (sourceQuadruple.getLevel() == expandedQuadruple.getLevel() && (sourceQuadruple.getOperator().equals(expandedQuadruple.getOperator()) || (sourceQuadruple.isPlusOrMinus() && expandedQuadruple.isPlusOrMinus()))) {
                expandedQuadruples.add(sourceQuadruple);
            } else {
                expandedQuadruples.forEach(lowQuadruple -> lowQuadruple.setLevel(lowQuadruple.getLevel() - 1));
                return expandedQuadruples;
            }
        }
        expandedQuadruples.forEach(expandedQuadruple1 -> expandedQuadruple1.setLevel(expandedQuadruple1.getLevel() - 1));
        return expandedQuadruples;
    }


    private boolean isThereOperationsBetweenParentheses(List<ExpandedQuadruple> expandedQuadruples) {
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.getLevel() != 0) {
                return true;
            }
        }
        return false;
    }
}
