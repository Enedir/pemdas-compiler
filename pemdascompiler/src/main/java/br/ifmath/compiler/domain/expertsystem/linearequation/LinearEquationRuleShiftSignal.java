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
import br.ifmath.compiler.infrastructure.util.MathOperatorUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alex_
 */
public class LinearEquationRuleShiftSignal implements IRule {

    private List<ExpandedQuadruple> expandedQuadruples;

    public LinearEquationRuleShiftSignal() {
        this.expandedQuadruples = new ArrayList<>();
    }
        
    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return isThereOnlyPlusAndMinus(source.get(0).getExpandedQuadruples()) && 
                (isThereInnerOperation(source.get(0), source.get(0).getLeft(), false, 0, false) 
                || isThereInnerOperation(source.get(0), source.get(0).getRight(), false, 0, false));
    }
    
    @Override
    public List<Step> handle(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException {
        this.expandedQuadruples = new ArrayList<>();
        
        List<Step> steps = new ArrayList<>();
                  
        handleSignalToShiftInSameLevels(source.get(0), null, source.get(0).getLeft(), true);
        handleSignalToShiftInSameLevels(source.get(0), null, source.get(0).getRight(), false);
        
        handleSignalToShiftInDifferentLevels(source.get(0), source.get(0).getLeft(), null, true);
        handleSignalToShiftInDifferentLevels(source.get(0), source.get(0).getRight(), null, false);
        
        handleDoubleNegative(source.get(0), true, source.get(0).getLeft(), null, null);
        handleDoubleNegative(source.get(0), false, source.get(0).getRight(), null, null);
        
        eliminateNonUsedQuadruples(source.get(0));
                
        ThreeAddressCode step = new ThreeAddressCode(source.get(0).getLeft(), source.get(0).getComparison(), source.get(0).getRight(), expandedQuadruples);
                    
        String latexNotation = step.toLaTeXNotation();
        String mathNotation = step.toMathNotation();
        
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, latexNotation, mathNotation, "Aplicação da regra de sinais em operações prioritárias, em duplas negações ou em somas de números negativos."));
        
        return steps;
    }
    
    private void eliminateNonUsedQuadruples(ThreeAddressCode source) {
        List<ExpandedQuadruple> toRemove = new ArrayList<>();
        
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            boolean used = false;
            
            if (expandedQuadruple != null) {
                for (int i = 0; i < expandedQuadruples.size(); i++) {
                    ExpandedQuadruple quadruple = expandedQuadruples.get(i);
                    if (quadruple != null && (expandedQuadruple.getResult().equals(quadruple.getArgument1()) 
                            || expandedQuadruple.getResult().equals(quadruple.getArgument2()))) {
                        used = true;
                        break;
                    }
                }

                used = used || expandedQuadruple.getResult().equals(source.getLeft()) 
                            || expandedQuadruple.getResult().equals(source.getRight());
            }
            
            if(!used) {
                toRemove.add(expandedQuadruple);
            }
        }
        
        expandedQuadruples.removeAll(toRemove);
    }
    
    private boolean isThereInnerOperation(ThreeAddressCode source, String param, boolean lastOperationIsMinus, int level, boolean secondParamenter) {        
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = source.findQuadrupleByResult(param);
            
            if ((level + 1) == expandedQuadruple.getLevel())
                return true;
            
            if (lastOperationIsMinus && expandedQuadruple.isNegative())
                return true;
            
            if (secondParamenter && expandedQuadruple.isNegative())
                return true;
            
            return isThereInnerOperation(source, expandedQuadruple.getArgument1(), lastOperationIsMinus || expandedQuadruple.isNegative(), level, false) 
                    || isThereInnerOperation(source, expandedQuadruple.getArgument2(), expandedQuadruple.isMinus(), expandedQuadruple.getLevel(), true);
        }
        
        return false;
    }
    
    private boolean isThereOnlyPlusAndMinus(List<ExpandedQuadruple> expandedQuadruples) {
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (!expandedQuadruple.isPlusOrMinus())
                return false;
        }
        
        return true;
    }
    
       
    private void handleSignalToShiftInSameLevels(ThreeAddressCode source, ExpandedQuadruple parent, String param, boolean left) {        
        boolean doNotAddCurrent = false;
        
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) { 
            ExpandedQuadruple current = source.findQuadrupleByResult(param);
            if (current == null)
                return;

            if (current.isNegative() && StringUtil.match(current.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                List<ExpandedQuadruple> derivatedOperations = handleNegativeInTheLeft(source, current, source.getDerivatedOperationsFromParent(param));
                for (ExpandedQuadruple derivatedOperation : derivatedOperations) {
                    if (!expandedQuadruples.contains(derivatedOperation))
                       expandedQuadruples.add(derivatedOperation);
                }
                
                if (parent != null) {
                    if (derivatedOperations.get(0).getOperator().equals("+") && derivatedOperations.get(0).getArgument2() == null) {
                        parent.setArgument1(derivatedOperations.get(0).getArgument1());
                        derivatedOperations.remove(0);
                    } else {
                        parent.setArgument1(derivatedOperations.get(0).getResult());
                    }
                } else {
                    if (left) {
                        source.setLeft(derivatedOperations.get(0).getResult());
                    } else {
                        source.setRight(derivatedOperations.get(0).getResult());
                    }
                }
                doNotAddCurrent = true;
            } else if (current.isMinus() && StringUtil.match(current.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple possibleNegative = getNegativeNumber(source, current.getArgument2(), current.getLevel());
                if (possibleNegative != null) {
                    current.setOperator("+");
                    current.setArgument2(possibleNegative.getArgument1());
                }
            } else if (current.isPlus() && StringUtil.match(current.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple possibleNegative = getNegativeNumber(source, current.getArgument2(), current.getLevel());
                if (possibleNegative != null) {
                    current.setOperator("-");
                    current.setArgument2(possibleNegative.getArgument1());
                }
            } 

            if (!doNotAddCurrent && !expandedQuadruples.contains(current))
                expandedQuadruples.add(current);
            
            handleSignalToShiftInSameLevels(source, current, current.getArgument1(), left);
            handleSignalToShiftInSameLevels(source, current, current.getArgument2(), left);            
        }
    }
    
    private ExpandedQuadruple getNegativeNumber(ThreeAddressCode source, String param, int level) {
        ExpandedQuadruple expandedQuadruple = source.findQuadrupleByResult(param);
        
        if (expandedQuadruple != null && expandedQuadruple.isNegative() && expandedQuadruple.getLevel() == level)
            return expandedQuadruple;
            
        return null;
    }
    
    private boolean handleSignalToShiftInDifferentLevels(ThreeAddressCode source, String param, ExpandedQuadruple parent, boolean left) {        
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) { 
            ExpandedQuadruple current = source.findQuadrupleByResult(param);
           
            List<ExpandedQuadruple> originalDerivatedOperations = source.getDerivatedOperationsFromParent(param);
            List<ExpandedQuadruple> derivatedOperations = shiftSignal(source, current, originalDerivatedOperations);
            
            for (ExpandedQuadruple derivatedOperation : derivatedOperations) {
                if (!handleSignalToShiftInDifferentLevels(source, derivatedOperation.getResult(), derivatedOperation, left) && !expandedQuadruples.contains(derivatedOperation))
                   expandedQuadruples.add(derivatedOperation);
            }
                        
            if (!expandedQuadruples.contains(current))
                expandedQuadruples.add(current);
            
            if (current != null) {
                if (current.getOperator().equals("+") 
                        && current.getArgument2() == null && parent == null) {
                    if (left)
                        source.setLeft(current.getArgument1());
                    else
                        source.setRight(current.getArgument1());

                    derivatedOperations.remove(current);
                }

                handleSignalToShiftInDifferentLevels(source, current.getArgument1(), current, left);
                handleSignalToShiftInDifferentLevels(source, current.getArgument2(), current, left);
            }
            
            return true;
        }
        
        return false;
    }
    
    private List<ExpandedQuadruple> handleNegativeInTheLeft(ThreeAddressCode source, ExpandedQuadruple parent, List<ExpandedQuadruple> derivatedOperations) {
        List<ExpandedQuadruple> handledOperation = new ArrayList<>();
        
        boolean first = true;
        for (ExpandedQuadruple derivatedOperation : derivatedOperations) {
            if (derivatedOperation.getResult().equals(""))
                continue;
            
            derivatedOperation.setOperator(MathOperatorUtil.signalRule(parent.getOperator(), derivatedOperation.getOperator()));
            adjustLevel(source, derivatedOperation.getResult());   
            derivatedOperation.setLevel(parent.getLevel());
            handledOperation.add(derivatedOperation); 
            
            if (first) {
                if (derivatedOperation.getOperator().equals("+") && derivatedOperation.getArgument2() == null)
                    continue;
                
                ExpandedQuadruple possibleNegative = getNegativeNumber(source, derivatedOperation.getArgument1(), derivatedOperation.getLevel() + 1);
                if (possibleNegative != null) {
                    derivatedOperation.setArgument1(possibleNegative.getArgument1());
                    possibleNegative.setResult("");
                } else {
                    possibleNegative = new ExpandedQuadruple("MINUS", derivatedOperation.getArgument1(), "T" + (source.getExpandedQuadruples().size() + 1), 
                                            parent.getPosition(), parent.getLevel());
                    handledOperation.add(possibleNegative);
                    derivatedOperation.setArgument1(possibleNegative.getResult());
                }
              
                first = false;
            } 
        }
        
        return handledOperation;
    }
    private void handleDoubleNegative(ThreeAddressCode source, boolean left, String param, ExpandedQuadruple parent, ExpandedQuadruple grandparent) {        
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) { 
            ExpandedQuadruple current = source.findQuadrupleByResult(param);
            
            if (current == null)
                return;
            
            if (current.isNegative() && parent != null && (parent.isMinus() || parent.isNegative())) {
                String operator = "+";
                
                if (grandparent != null) {
                    grandparent.setOperator(operator);
                    parent.setArgument1(current.getArgument1());
                } else if (parent.isNegative()) {
                    expandedQuadruples.remove(parent);
                    
                    if (left) {
                        source.setLeft(current.getArgument1());
                        handleDoubleNegative(source, left, source.getLeft(), null, null);
                    } else {
                        source.setRight(current.getArgument1());                        
                        handleDoubleNegative(source, left, source.getRight(), null, null);
                    }
                } else if (parent.isArgument2(current.getResult())) {
                    parent.setOperator(operator);
                    parent.setArgument2(current.getArgument1());
                } else {
                    current.setLevel(parent.getLevel());
                    if (!expandedQuadruples.contains(current))
                        expandedQuadruples.add(current);
                    handleDoubleNegative(source, left, current.getArgument1(), current, parent);
                }
            } else {
                if (!expandedQuadruples.contains(current))
                    expandedQuadruples.add(current);
                handleDoubleNegative(source, left, current.getArgument1(), current, parent);
                handleDoubleNegative(source, left, current.getArgument2(), current, null);
            }
        }
    }
    
    private List<ExpandedQuadruple> shiftSignal(ThreeAddressCode source, ExpandedQuadruple parent, List<ExpandedQuadruple> derivatedOperations) {
        List<ExpandedQuadruple> handledOperation = new ArrayList<>();
        
        ExpandedQuadruple grandparent = null;
        if (parent != null)
            grandparent = getQuadrupleByArgument(parent.getResult(), expandedQuadruples);
        
        String grandparentOperator = "";
        if (grandparent != null)
            grandparentOperator = grandparent.getOperator();
        
        for (ExpandedQuadruple derivatedOperation : derivatedOperations) {
            String operator = "";
            
            if (parent != null && derivatedOperation.getResult().equals(parent.getArgument2()))
                operator = MathOperatorUtil.signalRule(parent.getOperator(), derivatedOperation.getOperator());
            else if (grandparent != null)
                operator = MathOperatorUtil.signalRule(grandparentOperator, derivatedOperation.getOperator());
            else
                operator = MathOperatorUtil.signalRule(parent.getOperator(), derivatedOperation.getOperator());
                
            if (derivatedOperation.isNegative()) {
                ExpandedQuadruple brother = getQuadrupleByArgument(derivatedOperation.getResult(), derivatedOperations);
 
                if (brother != null && derivatedOperation.getResult().equals(brother.getArgument2())) {
                    brother.setOperator(operator);
                } else {
                    if (grandparent != null && parent.getResult().equals(grandparent.getArgument2())) {
                        grandparent.setOperator(operator);
                    } else if (parent != null) {
                        if (brother == null || brother.getResult().equals(parent.getArgument2()))
                            parent.setOperator(operator);
                        
                        if (operator.equals("+")) {
                            if (brother == null || !derivatedOperation.getResult().equals(brother.getArgument1()))
                                parent.setArgument2(derivatedOperation.getArgument1());
                        } else if (brother != null && derivatedOperation.getResult().equals(brother.getArgument1()) 
                                && parent != null && brother.getResult().equals(parent.getArgument2())) {
                            brother.setArgument1(derivatedOperation.getArgument1());
                        }
                        
                        //replaceTemporaryVariable(derivatedOperation.getResult(), derivatedOperation.getArgument1(), derivatedOperations);
                    }
                }
                
                if (operator.equals("+")) {
                    replaceTemporaryVariable(derivatedOperation.getResult(), derivatedOperation.getArgument1(), derivatedOperations);
                } else {
                    derivatedOperation.setLevel(parent.getLevel());
                    handledOperation.add(derivatedOperation);
                }
            } else {
                derivatedOperation.setOperator(operator);
                adjustLevel(source, derivatedOperation.getResult());   
                derivatedOperation.setLevel(parent.getLevel());
                handledOperation.add(derivatedOperation);             
            }
        }
        
        return handledOperation;
    }
    
    private void replaceTemporaryVariable(String temporaryVariable, String newVariable, List<ExpandedQuadruple> derivatedOperations) {
        for (ExpandedQuadruple derivatedOperation : derivatedOperations) {
            if (temporaryVariable.equals(derivatedOperation.getArgument1())) {
                derivatedOperation.setArgument1(newVariable);
                break;
            }
            
            if (temporaryVariable.equals(derivatedOperation.getArgument2())) {
                derivatedOperation.setArgument2(newVariable);
                break;
            }
        }
    }
    
    private ExpandedQuadruple getQuadrupleByArgument(String argument, List<ExpandedQuadruple> derivatedOperations) {
        for (ExpandedQuadruple derivatedOperation : derivatedOperations) {
            if (argument.equals(derivatedOperation.getArgument1()) || argument.equals(derivatedOperation.getArgument2())) {
                return derivatedOperation;
            }
        }
        
        return null;
    }
    
    private void adjustLevel(ThreeAddressCode source, String param) {
        for (ExpandedQuadruple derivatedOperation : source.getDerivatedOperationsFromParent(param)) {
            derivatedOperation.setLevel(derivatedOperation.getLevel() - 1);
        }
    }
    
}


