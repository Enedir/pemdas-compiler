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

public class PolynomialMultiplicationRuleMultiplicationTest {
    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String finalResultExplicationExpected;
    private String stepTwoResultExpected;
    private String stepThreeResultExpected;
    private String stepFourResultExpected;
    private String stepFiveResultExpected;
    private String stepSixResultExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new PolynomialMultiplicationExpertSystem();
        stepTwoResultExpected = "Aplicando a propriedade distributiva, onde cada elemento dentro dos parênteses é multiplicado pelo elemento do outro termo";
        stepThreeResultExpected = "Aplicando a propriedade distributiva, onde cada elemento do primeiro termo é multiplicado por cada um dos elementos do segundo termo.";
        stepFourResultExpected = "Multiplica-se os coeficientes, considerando a regra dos sinais, e para as variáveis, somam-se os expoentes pela propriedade das potências.";
        stepFiveResultExpected = "Aplicando regra de troca de sinais em operações prioritárias, em duplas negações ou em somas de números negativos.Também, removendo parenteses dos polinômios.";
        stepSixResultExpected = "Removendo parênteses dos polinômios";
        finalResultExplicationExpected = "Soma dos termos semelhantes.";

    }

    @Test()
    public void multiplication_without_distributive_scenery_one_with_success() {
        //Arrange
        String expression = "4x * (-6)";

        String stepTwoValueExpected = "-24x";
        //String lastStepValueExpected = "16x^2 - 24";

        // Act)
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 1);
        //Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepFourResultExpected, stepTwo.getReason());
        //assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        //assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void multiplication_without_distributive_scenery_two_with_success() {
        //Arrange
        String expression = "(-2x^3) * (-3x)";

        String stepTwoValueExpected = "6x^4";
        //String lastStepValueExpected = "16x^2 - 24";

        // Act)
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 1);
        //Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepFourResultExpected, stepTwo.getReason());
        //assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        //assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void multiplication_between_few_values_scenery_one_with_success() {
        //Arrange
        String expression = "(x^2 + 5x^5) * (-4)";

        String stepTwoValueExpected = "(-4) * x^2 + (-4) * 5x^5";
        String lastStepValueExpected = "-4x^2 - 20x^5";

        // Act)
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResultExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFourResultExpected, finalStep.getReason());
    }

    @Test()
    public void multiplication_between_few_values_scenery_two_with_success() {
        //Arrange
        String expression = "x^3 * (-x^2 + 7)";

        String stepTwoValueExpected = "x^3 * (-x^2) + x^3 * 7";
        String lastStepValueExpected = "-x^5 + 7x^3";

        // Act)
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResultExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFourResultExpected, finalStep.getReason());
    }

    @Test()
    public void multiplication_between_few_values_scenery_three_with_success() {
        //Arrange
        String expression = "(5x + 2x^2) * (3x^2 - 7)";

        String stepTwoValueExpected = "5x * 3x^2 + 5x * (-7) + 2x^2 * 3x^2 + 2x^2 * (-7)";
        String lastStepValueExpected = "15x^3 - 35x + 6x^4 - 14x^2";

        // Act)
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepThreeResultExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFourResultExpected, finalStep.getReason());
    }

    @Test()
    public void multiplication_between_few_values_scenery_four_with_success() {
        //Arrange
        String expression = "(-3x^2 - 4) * (-2x^2 - 10)";

        String stepTwoValueExpected = "(-3x^2) * (-2x^2) + (-3x^2) * (-10) + (-4) * (-2x^2) + (-4) * (-10)";
        String lastStepValueExpected = "6x^4 + 30x^2 + 8x^2 + 40";

        // Act)
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepThreeResultExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFourResultExpected, finalStep.getReason());
    }

    @Test()
    public void multiplication_between_many_values_scenery_one_with_success() {
        //Arrange
        String expression = "(x + 12) * (4x^2 - x^3 + 1)";

        String stepTwoValueExpected = "x * 4x^2 + x * (-x^3) + x * 1 + 12 * 4x^2 + 12 * (-x^3) + 12 * 1";
        String lastStepValueExpected = "4x^3 - x^4 + x + 48x^2 - 12x^3 + 12";

        // Act)
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepThreeResultExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFourResultExpected, finalStep.getReason());
    }

    @Test()
    public void multiplication_between_many_values_scenery_two_with_success() {
        //Arrange
        String expression = "(3 - 4x^3 + x - 2x^2) * (4x^2 - x)";

        String stepTwoValueExpected = "3 * 4x^2 + 3 * (-x) + (-4x^3) * 4x^2 + (-4x^3) * (-x)" +
                " + x * 4x^2 + x * (-x) + (-2x^2) * 4x^2 + (-2x^2) * (-x)";
        String lastStepValueExpected = "12x^2 - 3x - 16x^5 + 4x^4 + 4x^3 - x^2 - 8x^4 + 2x^3";

        // Act)
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepThreeResultExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFourResultExpected, finalStep.getReason());
    }

}
