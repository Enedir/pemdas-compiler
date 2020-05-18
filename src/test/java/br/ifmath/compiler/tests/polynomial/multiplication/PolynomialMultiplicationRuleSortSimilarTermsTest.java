package br.ifmath.compiler.tests.polynomial.multiplication;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.AnswerType;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.multiplication.PolynomialMultiplicationExpertSystem;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PolynomialMultiplicationRuleSortSimilarTermsTest {
    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String finalResultExplicationExpected;
    private String stepTwoResultExpected;
    private String stepThreeResultExpected;
    private String stepFourResultExpected;
    private String stepFiveResultExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new PolynomialMultiplicationExpertSystem();
        stepTwoResultExpected = "Aplicando a propriedade distributiva, onde cada elemento dentro dos parênteses é multiplicado pelo elemento do outro termo";
        stepThreeResultExpected = "Aplicando a propriedade distributiva, onde cada elemento do primeiro termo é multiplicado por cada um dos elementos do segundo termo.";
        stepFourResultExpected = "Multiplica-se os coeficientes, considerando a regra dos sinais, e para as variáveis, somam-se os expoentes pela propriedade das potências.";
        stepFiveResultExpected = "Agrupando os termos semelhantes.";
        finalResultExplicationExpected = "Adicionando dos termos semelhantes.";
    }

    @Test()
    public void sort_simple_terms_scenery_one_with_success() {
        //Arrange
        String expression = "(x^2 + 3x^3) * 5";

        String stepTwoValueExpected = "5 * x^2 + 5 * 3x^3";
        String stepThreeValueExpected = "5x^2 + 15x^3";
        String lastStepValueExpected = "15x^3 + 5x^2";


        // Act)
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResultExpected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepFourResultExpected, stepThree.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFiveResultExpected, finalStep.getReason());
    }

    @Test()
    public void sort_simple_terms_scenery_two_with_success() {
        //Arrange
        String expression = "2 * (3x^2 - 4x^4)";

        String stepTwoValueExpected = "2 * 3x^2 + 2 * (-4x^4)";
        String stepThreeValueExpected = "6x^2 - 8x^4";
        String lastStepValueExpected = "-8x^4 + 6x^2";


        // Act)
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResultExpected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepFourResultExpected, stepThree.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFiveResultExpected, finalStep.getReason());
    }

}
