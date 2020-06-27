package br.ifmath.compiler.domain.expertsystem.polynomial.addandsub;

import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.Step;

import java.util.List;

public class AnswerPolynomialAddAndSub implements IAnswer {

    private String result;
    private final List<Step> steps;

    public AnswerPolynomialAddAndSub(List<Step> steps) {
        this.steps = steps;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public List<Step> getSteps() {
        return steps;
    }
}
