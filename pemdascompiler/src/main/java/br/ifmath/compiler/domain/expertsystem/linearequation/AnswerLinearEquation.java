/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifmath.compiler.domain.expertsystem.linearequation;


import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.Step;

import java.util.List;

/**
 *
 * @author alex_
 */
public class AnswerLinearEquation implements IAnswer {

    private String a;
    private String b;
    private String x;
    private final List<Step> steps;

    public AnswerLinearEquation(List<Step> steps) {
        this.steps = steps;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }
    
    @Override
    public List<Step> getSteps() {
        return steps;
    }
    
}
