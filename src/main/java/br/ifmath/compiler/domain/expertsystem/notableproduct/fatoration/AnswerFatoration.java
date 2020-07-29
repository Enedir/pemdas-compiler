package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration;

import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.Step;

import java.util.List;

public class AnswerFatoration implements IAnswer {

    private String result;
    private List<Step> steps;

    public AnswerFatoration(List<Step> steps) {
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
