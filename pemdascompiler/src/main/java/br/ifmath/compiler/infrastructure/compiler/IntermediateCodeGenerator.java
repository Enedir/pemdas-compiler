/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.infrastructure.compiler;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.Temporary;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alex_
 */
public class IntermediateCodeGenerator implements IIntermediateCodeGenerator {
    
    private final List<ExpandedQuadruple> expandedQuadruples;
    private int temporaryId;
    
    public IntermediateCodeGenerator() {
        this.expandedQuadruples = new ArrayList<>();
        this.temporaryId = 1;
    }
    
    @Override
    public void addNewOperation(String operator, String argument1, String argument2, String result, int position, int level) {
        this.expandedQuadruples.add(new ExpandedQuadruple(operator, argument1, argument2, result, position, level));
    }
    
    @Override
    public void addNewOperation(String operator, String argument1, String result, int position, int level) {
        this.expandedQuadruples.add(new ExpandedQuadruple(operator, argument1, result, position, level));
    }
    
    @Override
    public ThreeAddressCode generateCode(String left, String comparison, String right) {
        return new ThreeAddressCode(left, comparison, right, expandedQuadruples);
    }

    @Override
    public Temporary getNextTemporary() {
        return new Temporary(temporaryId++);
    }

    @Override
    public void clearValues() {
        temporaryId = 1;
        expandedQuadruples.clear();
    }
    
}
