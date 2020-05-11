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

public class PolynomialMultiplicationRuleDistributiveTest {
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
        stepFourResultExpected = "Aplicando regra de troca de sinais em operações prioritárias, em duplas negações ou em somas de números negativos.Também, removendo parenteses dos polinômios.";
        stepFiveResultExpected = "Removendo parênteses dos polinômios";
        finalResultExplicationExpected = "Soma dos termos semelhantes.";

    }

    @Test()
    public void distributive_monomial_simple_terms_scenery_one_with_success() {
        //Arrange
        String expression = "4 * (4x^2 - 6)";

        String stepTwoValueExpected = "4 * 4x^2 + 4 * (-6)";
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
        assertEquals(stepTwoResultExpected, stepTwo.getReason());
        //assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        //assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void distributive_monomial_simple_terms_scenery_two_with_success() {
        //Arrange
        String expression = "(x^2 - 5x) * -9x";

        String lastStepValueExpected = "(-9x) * x^2 + (-9x) * (-5x)";

        // Act)
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepTwoResultExpected, finalStep.getReason());
    }

    @Test()
    public void distributive_monomial_complex_terms_scenery_one_with_success() {
        //Arrange
        String expression = "(12x^4 + 5x^3 - x^2 - 5x + 4) * -2x";

        String lastStepValueExpected = "(-2x) * 12x^4 + (-2x) * 5x^3 + (-2x) * (-x^2) + (-2x) * (-5x) + (-2x) * 4";

        // Act)
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepTwoResultExpected, finalStep.getReason());
    }

    @Test()
    public void distributive_monomial_complex_terms_scenery_two_with_success() {
        //Arrange
        String expression = "8x^2 * (-5x^6 - 8x - 2x^3 + 24 + 4x^2 + 9x^4)";

        String lastStepValueExpected = "8x^2 * (-5x^6) + 8x^2 * (-8x) + 8x^2 * (-2x^3) + 8x^2 * 24 + 8x^2 * 4x^2 + 8x^2 * 9x^4";

        // Act)
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepTwoResultExpected, finalStep.getReason());
    }

    @Test()
    public void distributive_simple_terms_scenery_one_with_success() {
        //Arrange
        String expression = "(2x - 6) * (3x^2 - 10)";

        String stepTwoValueExpected = "2x * 3x^2 + 2x * (-10) + (-6) * 3x^2 + (-6) * (-10)";
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
        assertEquals(stepThreeResultExpected, stepTwo.getReason());
        //assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        //assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }


    @Test()
    public void distributive_simple_terms_scenery_two_with_success() {
        //Arrange
        String expression = "(-5 - x^3) * (-8x + 4)";

        String stepTwoValueExpected = "(-5) * (-8x) + (-5) * 4 + (-x^3) * (-8x) + (-x^3) * 4";
        //String lastStepValueExpected = "16x^2 - 24";

        // Act
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
        assertEquals(stepThreeResultExpected, stepTwo.getReason());
        //assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        //assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void distributive_complex_terms_scenery_one_with_success() {
        //Arrange
        String expression = "(2x^2 + x + 3) * (3x^2 - 4x^3 + 1)";

        String stepTwoValueExpected = "2x^2 * 3x^2 + 2x^2 * (-4x^3) + 2x^2 * 1 + x * 3x^2 + x * (-4x^3) + x * 1 " +
                "+ 3 * 3x^2 + 3 * (-4x^3) + 3 * 1";
        //String lastStepValueExpected = "16x^2 - 24";

        // Act
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
        assertEquals(stepThreeResultExpected, stepTwo.getReason());
        //assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        //assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void distributive_complex_terms_scenery_two_with_success() {
        //Arrange
        String expression = "(-6x + 12) * (2x^3 - 4x + 2)";

        String stepTwoValueExpected = "(-6x) * 2x^3 + (-6x) * (-4x) + (-6x) * 2" +
                " + 12 * 2x^3 + 12 * (-4x) + 12 * 2";
        //String lastStepValueExpected = "16x^2 - 24";

        // Act
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
        assertEquals(stepThreeResultExpected, stepTwo.getReason());
        //assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        //assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void distributive_complex_terms_scenery_three_with_success() {
        //Arrange
        String expression = "(5x^4 + 2x^3 + 8x - 4) * (x^2 - 5x)";

        String stepTwoValueExpected = "5x^4 * x^2 + 5x^4 * (-5x) + 2x^3 * x^2 + 2x^3 * (-5x)" +
                " + 8x * x^2 + 8x * (-5x) + (-4) * x^2 + (-4) * (-5x)";
        //String lastStepValueExpected = "16x^2 - 24";

        // Act
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
        assertEquals(stepThreeResultExpected, stepTwo.getReason());
        //assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        //assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void distributive_complex_terms_scenery_four_with_success() {
        //Arrange
        String expression = "(-5 - x^3 + 2x^2 - 8x^4) * (8x + 4 - 12x^2 - 5x^3 - 7x^4)";

        String stepTwoValueExpected = "(-5) * 8x + (-5) * 4 + (-5) * (-12x^2) + (-5) * (-5x^3) + (-5) * (-7x^4)" +
                " + (-x^3) * 8x + (-x^3) * 4 + (-x^3) * (-12x^2) + (-x^3) * (-5x^3) + (-x^3) * (-7x^4)" +
                " + 2x^2 * 8x + 2x^2 * 4 + 2x^2 * (-12x^2) + 2x^2 * (-5x^3) + 2x^2 * (-7x^4)" +
                " + (-8x^4) * 8x + (-8x^4) * 4 + (-8x^4) * (-12x^2) + (-8x^4) * (-5x^3) + (-8x^4) * (-7x^4)";
        //String lastStepValueExpected = "16x^2 - 24";

        // Act
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
        assertEquals(stepThreeResultExpected, stepTwo.getReason());
        //assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        //assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }
}
