/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.expertsystem.linearequation;


import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alex_
 */
public class LinearEquationRuleIsolateTerms implements IRule {
    
    private final List<ExpandedQuadruple> expandedQuadruples;
    private ExpandedQuadruple quadrupleVariable;
    private ExpandedQuadruple quadrupleNumber;
    private String left;
    private String right;
    private int count;
    
    public LinearEquationRuleIsolateTerms() {
        this.expandedQuadruples = new ArrayList<>();
    }
    
    @Override
    public boolean match(List<ThreeAddressCode> sources) {
        return isThereOnlyOperationInLevel0(sources.get(0).getExpandedQuadruples())
                && (isThereTermsToIsolate(sources.get(0).getOperationsFromLeft()) || isThereTermsToIsolate(sources.get(0).getOperationsFromRight())
                    || isThereSimpleTermToIsolate(sources.get(0), sources.get(0).getLeft(), true) || isThereSimpleTermToIsolate(sources.get(0), sources.get(0).getRight(), false));
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        expandedQuadruples.clear();
        quadrupleNumber = null;
        quadrupleVariable = null;
        left = "";
        right = "";
        count = 0;
               
        
        List<Step> steps = new ArrayList<>();
        
        isolateTerms(sources.get(0), sources.get(0).getLeft(), null, null, true, false);
        isolateTerms(sources.get(0), sources.get(0).getRight(), null, null, false, false);
        
        if (StringUtil.isEmpty(quadrupleVariable.getArgument2()) && !quadrupleVariable.isNegative()) {
            left = quadrupleVariable.getArgument1();
        } else {
            expandedQuadruples.add(quadrupleVariable);
        } 
        
        if (quadrupleNumber == null) {
            right = "0";
        } else if (StringUtil.isEmpty(quadrupleNumber.getArgument2()) && !quadrupleNumber.isNegative()) {
            right = quadrupleNumber.getArgument1();
        } else {
            expandedQuadruples.add(quadrupleNumber);
        }

        ThreeAddressCode step = new ThreeAddressCode(left, sources.get(0).getComparison(), right, expandedQuadruples);
                    
        String latexNotation = step.toLaTeXNotation();
        String mathNotation = step.toMathNotation();
        
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        
        steps.add(new Step(codes, latexNotation, mathNotation, "Os termos semelhantes foram isolados. "
                + "As variáveis foram movidas para a esquerda da igualdade e as constantes foram movidas para a direita. "
                + "Ao trocar a posição de um termo perante a igualdade, é necessário mudar o seu sinal, aplicando a operação inversa. "
                + "Neste caso, quando o termo mudou de posição, + passou a ser – e – passou a ser +."));
        
        return steps;
    }
        
    private boolean isVariable(String param) {
        return StringUtil.matchAny(param, RegexPattern.VARIABLE.toString(), RegexPattern.VARIABLE_WITH_COEFICIENT.toString())
                    && !StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString());
    }     
    
    private boolean isThereSimpleTermToIsolate(ThreeAddressCode source, String param, boolean left) {
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = source.findQuadrupleByResult(param);
            
            if (expandedQuadruple.isNegative()) {
                param = expandedQuadruple.getArgument1();
            }
        }
        
        if (left) {
            return StringUtil.matchAny(param, RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString());
        }
        
        return isVariable(param);
    }
    
    private boolean isThereTermsToIsolate(List<ExpandedQuadruple> expandedQuadruples) {        
        int variableAmount = 0;
        int numberAmount = 0;
        
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (!expandedQuadruple.isPlusOrMinus())
                return false;
            
            if (StringUtil.matchAny(expandedQuadruple.getArgument1(), RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString()))
                numberAmount++;
            
            if (StringUtil.matchAny(expandedQuadruple.getArgument2(), RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString()))
                numberAmount++;
            
            if (isVariable(expandedQuadruple.getArgument1()))
                variableAmount++;
            
            if (isVariable(expandedQuadruple.getArgument2()))
                variableAmount++;
        }
                
        return variableAmount > 0 && numberAmount > 0;
    }
    
    private boolean isThereOnlyOperationInLevel0(List<ExpandedQuadruple> expandedQuadruples) {
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.getLevel() != 0)
                return false;
        }
        
        return true;
    }
    
    private void isolateTerms(ThreeAddressCode source, String param, ExpandedQuadruple parent, ExpandedQuadruple grandparent, boolean left, boolean lastOperationIsMinus) {
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
           ExpandedQuadruple current = source.findQuadrupleByResult(param);

           isolateTerms(source, current.getArgument1(), current, parent, left, current.isNegative() || lastOperationIsMinus);
           isolateTerms(source, current.getArgument2(), current, parent, left, current.isMinus());
        } else if (StringUtil.matchAny(param, RegexPattern.VARIABLE.toString(), RegexPattern.VARIABLE_WITH_COEFICIENT.toString())) { 
             isolateVariable(param, parent, grandparent, left, lastOperationIsMinus);
        } else if (StringUtil.matchAny(param, RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString())) { 
             isolateNumber(param, parent, grandparent, left, lastOperationIsMinus);
        }
    }
    
    private void isolateVariable(String param, ExpandedQuadruple parent, ExpandedQuadruple grandparent, boolean left, boolean lastOperationIsMinus) {
        if (parent == null || (parent.getArgument1().equals(param) && grandparent == null)) {
            quadrupleVariable = addTermToQuadruple(quadrupleVariable, param, !left && !lastOperationIsMinus, 0, false);
        } else if (parent.getArgument1().equals(param) && grandparent != null && grandparent.getArgument2().equals(parent.getResult())) {
            quadrupleVariable = addTermToQuadruple(quadrupleVariable, param, !(grandparent.isMinusOrNegative() ^ left), 0, false);
        } else if (parent.getArgument1().equals(param) && !parent.isNegative()){
            quadrupleVariable = addTermToQuadruple(quadrupleVariable, param, !lastOperationIsMinus && !left, 0, false);
        } else {
            quadrupleVariable = addTermToQuadruple(quadrupleVariable, param, !(parent.isMinusOrNegative() ^ left), 0, false);
        }
    }
    
    private void isolateNumber(String param, ExpandedQuadruple parent, ExpandedQuadruple grandparent, boolean left, boolean lastOperationIsMinus) {
       if (parent == null || (parent.getArgument1().equals(param) && grandparent == null)) {
            quadrupleNumber = addTermToQuadruple(quadrupleNumber, param, left, 0, true);
        } else if (parent.getArgument1().equals(param) && grandparent != null && grandparent.getArgument2().equals(parent.getResult())) {
            quadrupleNumber = addTermToQuadruple(quadrupleNumber, param, grandparent.isMinusOrNegative() ^ left, 0, true);
        } else if (parent.getArgument1().equals(param) && !parent.isNegative()) {
            quadrupleNumber = addTermToQuadruple(quadrupleNumber, param, !lastOperationIsMinus && left, 0, true);
        } else {
            quadrupleNumber = addTermToQuadruple(quadrupleNumber, param, parent.isMinusOrNegative() ^ left, 0, true);
        }
    }
    
    private ExpandedQuadruple addTermToQuadruple(ExpandedQuadruple quadruple, String param, boolean minus, int position, boolean number) {
        if (quadruple == null) {
            if (minus) {
                quadruple = new ExpandedQuadruple("MINUS", param, "T" + (++count), position, 0);
                expandedQuadruples.add(quadruple);
                quadruple = new ExpandedQuadruple(quadruple.getResult(), "T" + (++count), position, 0);
            } else {
                quadruple = new ExpandedQuadruple(param, "T" + (++count), position, 0);
            }
            
            if (number) {
                this.right = quadruple.getResult();
            } else {
                this.left = quadruple.getResult();
            }
        } else {
            if (StringUtil.isNotEmpty(quadruple.getArgument2())) {
                ExpandedQuadruple aux = new ExpandedQuadruple((minus ? "-" : "+"), quadruple.getArgument2() , param, "T" + (++count), position, 0);
                quadruple.setArgument2(aux.getResult());
                expandedQuadruples.add(quadruple);
                quadruple = aux;
            } else {
                quadruple.setArgument2(param);
                quadruple.setOperator(minus ? "-" : "+");
            }
        }
        
        return quadruple;
    }
    
}
