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
public class LinearEquationRuleGeneralWithCoeficientInVariable implements IRule {

    private final List<ExpandedQuadruple> expandedQuadruples;

    public LinearEquationRuleGeneralWithCoeficientInVariable() {
        this.expandedQuadruples = new ArrayList<>();
    }
    
    @Override
    public boolean match(List<ThreeAddressCode> sources) {
        return isGeneralWithCoeficientInVariable(sources.get(0), sources.get(0).getLeft(), sources.get(0).getRight())
                || isGeneralWithCoeficientInVariable(sources.get(0), sources.get(0).getRight(), sources.get(0).getLeft());
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        expandedQuadruples.clear();
        
        List<Step> steps = new ArrayList<>();
            
        ThreeAddressCode step;        
        if (isGeneralWithCoeficientInVariable(sources.get(0), sources.get(0).getLeft(), sources.get(0).getRight())) {
            step = variableInTheLeft(sources.get(0));
        } else {
            step = variableInTheRight(sources.get(0));
        }
        
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation(), step.toMathNotation(), 
                "O coeficiente da variável foi movido para o outro lado da igualdade. "
                        + "Isso implica em inverter a operação realiza sobre ele para uma inversamente proporcional. "
                        + "Neste caso, ele deixará de multiplicar a variável para dividir o termo independente."));
        
        return steps;
    }
        
    private boolean isGeneralWithCoeficientInVariable(ThreeAddressCode threeAddressCode, String possibleVariable, String possibleNumber) {        
        boolean isNumber = false;
        boolean isVariableWithCoeficient = false;
        
        if (StringUtil.matchAny(possibleNumber, RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString())) {
            isNumber = true;
        } else if (StringUtil.match(possibleNumber, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(possibleNumber);
            
            isNumber = expandedQuadruple.isNegative() && StringUtil.matchAny(expandedQuadruple.getArgument1(), RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString());
        }     
           
        if (StringUtil.match(possibleVariable, RegexPattern.VARIABLE_WITH_COEFFICIENT.toString())) {
            isVariableWithCoeficient = true;
        } else if (StringUtil.match(possibleVariable, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(possibleVariable);
            
            isVariableWithCoeficient = expandedQuadruple.isNegative() && StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_COEFFICIENT.toString());
        }
        
        return isNumber && isVariableWithCoeficient;
    }
    
    private ThreeAddressCode variableInTheLeft(ThreeAddressCode threeAddressCode) {        
        String left = "";
        String right = "";
        String coeficient = "";
        String variable = "";
        String variableWithCoeficient = "";
        
        ExpandedQuadruple expandedQuadrupleVariable = null;
        
        if (StringUtil.match(threeAddressCode.getLeft(), RegexPattern.VARIABLE_WITH_COEFFICIENT.toString())) {
            variableWithCoeficient = threeAddressCode.getLeft();
        } else { 
            expandedQuadrupleVariable = threeAddressCode.findQuadrupleByResult(threeAddressCode.getLeft());
            variableWithCoeficient = expandedQuadrupleVariable.getArgument1();
        }

        coeficient = StringUtil.removeNonNumericChars(variableWithCoeficient);
        variable = StringUtil.remove(variableWithCoeficient, coeficient);

        if (expandedQuadrupleVariable != null) {
            ExpandedQuadruple variableWithoutCoeficient = new ExpandedQuadruple(expandedQuadrupleVariable);
            variableWithoutCoeficient.setArgument1(variable);
            expandedQuadruples.add(variableWithoutCoeficient);
            left = variableWithoutCoeficient.getResult();
        } else {
            left = variable;                
        }

        ExpandedQuadruple currentRight = threeAddressCode.findQuadrupleByResult(threeAddressCode.getRight());
        if (currentRight != null) {
            expandedQuadruples.add(new ExpandedQuadruple(currentRight));
        }
        
        ExpandedQuadruple expandedQuadrupleConstant = new ExpandedQuadruple("/", threeAddressCode.getRight(), coeficient, 
                "T" + (threeAddressCode.getExpandedQuadruples().size() + 1), 1, 0);
        expandedQuadruples.add(expandedQuadrupleConstant);            
        right = expandedQuadrupleConstant.getResult();
        
        return new ThreeAddressCode(left, threeAddressCode.getComparison(), right, expandedQuadruples);
    }
        
    private ThreeAddressCode variableInTheRight(ThreeAddressCode threeAddressCode) {        
        String left = "";
        String right = "";
        String coeficient = "";
        String variable = "";
        String variableWithCoeficient = "";
        
        ExpandedQuadruple expandedQuadrupleVariable = null;
        
        if (StringUtil.match(threeAddressCode.getRight(), RegexPattern.VARIABLE_WITH_COEFFICIENT.toString())) {
            variableWithCoeficient = threeAddressCode.getRight();
        } else { 
            expandedQuadrupleVariable = threeAddressCode.findQuadrupleByResult(threeAddressCode.getRight());
            variableWithCoeficient = expandedQuadrupleVariable.getArgument1();
        }

        coeficient = StringUtil.removeNonNumericChars(variableWithCoeficient);
        variable = StringUtil.remove(variableWithCoeficient, coeficient);

        if (expandedQuadrupleVariable != null) {
            ExpandedQuadruple variableWithoutCoeficient = new ExpandedQuadruple(expandedQuadrupleVariable);
            variableWithoutCoeficient.setArgument1(variable);
            expandedQuadruples.add(variableWithoutCoeficient);
            right = variableWithoutCoeficient.getResult();
        } else {
            right = variable;                
        }

        ExpandedQuadruple currentLeft = threeAddressCode.findQuadrupleByResult(threeAddressCode.getLeft());
        if (currentLeft != null) {
            expandedQuadruples.add(new ExpandedQuadruple(currentLeft));
        }
        
        ExpandedQuadruple expandedQuadrupleConstant = new ExpandedQuadruple("/", threeAddressCode.getLeft(), coeficient, 
                "T" + (threeAddressCode.getExpandedQuadruples().size() + 1), 1, 0);
        expandedQuadruples.add(expandedQuadrupleConstant);            
        left = expandedQuadrupleConstant.getResult();
        
        return new ThreeAddressCode(left, threeAddressCode.getComparison(), right, expandedQuadruples);
    }
    
}
