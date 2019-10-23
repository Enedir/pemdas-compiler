package br.ifmath.compiler.domain.expertsystem.polynomial.numericvalue;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class PolynomialRuleParenthesesOperations implements IRule {
    private List<ExpandedQuadruple> expandedQuadruples;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return isThereOperationsBetweenParentheses(source.get(0).getExpandedQuadruples());
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();
        expandedQuadruples = new ArrayList<>();
        expandedQuadruples.addAll(source.get(0).getExpandedQuadruples());

        resolveInnerOperations(source);
        clearNonUsedQuadruples();

        ThreeAddressCode step = new ThreeAddressCode(source.get(0).getLeft(), expandedQuadruples);
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Realizando as operações dentro dos parênteses."));

        return steps;
    }

    private void clearNonUsedQuadruples() {
        Iterator<ExpandedQuadruple> i = expandedQuadruples.iterator();
        while(i.hasNext()){
            ExpandedQuadruple expandedQuadruple = i.next();
            if(expandedQuadruple.getLevel() != 0)
                i.remove();
        }
    }

    private void resolveInnerOperations(List<ThreeAddressCode> source) {
        for (int i = 0; i < expandedQuadruples.size(); i++) {
            ExpandedQuadruple expandedQuadruple = expandedQuadruples.get(i);
            if(expandedQuadruple.getLevel() != 0){
                if(expandedQuadruple.isMinus()){
                    applyCorrectOperation(expandedQuadruple,i,"MINUS",source);
                } else if(expandedQuadruple.isPlus()){
                    applyCorrectOperation(expandedQuadruple,i,"PLUS",source);
                } else if(expandedQuadruple.isTimes()){
                    applyCorrectOperation(expandedQuadruple,i,"TIMES",source);
                }
            }
        }
    }

    private void applyCorrectOperation(ExpandedQuadruple expandedQuadruple,int listIndex, String operation,List<ThreeAddressCode> source) {
        ExpandedQuadruple nextQuadruple = expandedQuadruples.get(listIndex + 1);
        switch (operation){
            case "MINUS":
            case "PLUS":
                if(nextQuadruple.getArgument2().equals(expandedQuadruple.getResult())){
                    double newValue = new PolynomialRuleSumNumbers().sumTerms(source.get(0),expandedQuadruple.getResult(),false);
                    nextQuadruple.setArgument2(String.valueOf(newValue));
                }
                break;
            case "TIMES":
//                if(nextQuadruple.getArgument2().equals(expandedQuadruple.getResult())){
//                    double newValue = new PolynomialRuleMultiplyNumbers().multiply());
//                    nextQuadruple.setArgument2(String.valueOf(newValue));
//                }
                break;
        }
    }

    private boolean isThereOperationsBetweenParentheses(List<ExpandedQuadruple> expandedQuadruples) {
        for (ExpandedQuadruple expandedQuadruple: expandedQuadruples) {
            if(expandedQuadruple.getLevel() != 0){
                return true;
            }
        }
        return false;
    }
}
