/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.expertsystem;


import br.ifmath.compiler.domain.compiler.ThreeAddressCode;

import java.util.List;

/**
 *
 * @author alex_
 */
public class Step {
    
    private List<ThreeAddressCode> source;
    private String latexExpression;
    private String mathExpression;
    private String reason;

    public Step(List<ThreeAddressCode> source, String latexExpression, String mathExpression, String reason) {
        this.source = source;
        this.latexExpression = latexExpression;
        this.mathExpression = mathExpression;
        this.reason = reason;
    }

    public List<ThreeAddressCode> getSource() {
        return source;
    }

    public void setSource(List<ThreeAddressCode> source) {
        this.source = source;
    }

    public String getLatexExpression() {
        return latexExpression;
    }

    public void setLatexExpression(String latexExpression) {
        this.latexExpression = latexExpression;
    }

    public String getMathExpression() {
        return mathExpression;
    }

    public void setMathExpression(String mathExpression) {
        this.mathExpression = mathExpression;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    
}
