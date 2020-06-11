package br.ifmath.compiler.tests.polynomial.addAndSub;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.AnswerType;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.addandsub.PolynomialAddAndSubExpertSystem;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PolynomialAddAndSubRuleSortTermsTest {
    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String finalResultExplicationExpected;
    private String stepTwoResultExpected;
    private String stepThreeResultExpected;
    private String stepFourResultExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new PolynomialAddAndSubExpertSystem();
        stepTwoResultExpected = "Aplicando a regra de troca de sinais em operações prioritárias, em duplas negações ou " +
                "em somas de números negativos. E, removendo os parênteses dos polinômios.";
        stepThreeResultExpected = "Removendo os parênteses dos polinômios";
        stepFourResultExpected = "Agrupando os termos semelhantes.";
        finalResultExplicationExpected = "Soma dos termos semelhantes.";

    }

    @Test()
    public void sort_simple_terms_scenery_one_with_success() {
        //Arrange
        String expression = "(7 - 2) + (x + 2x^2)";

        String stepTwoValueExpected = "7 - 2 + x + 2x^2";
        String stepThreeValueExpected = "2x^2 + x + 7 - 2";
        String lastStepValueExpected = "2x^2 + x + 5";

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
        assertEquals(stepThreeResultExpected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepFourResultExpected, stepThree.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void sort_simple_terms_scenery_two_with_success() {
        //Arrange
        String expression = "(2x - 7) - (3x^2 + x^2)";

        String stepTwoValueExpected = "2x - 7 - 3x^2 - x^2";
        String stepThreeValueExpected = "-3x^2 - x^2 + 2x - 7";
        String lastStepValueExpected = "-4x^2 + 2x - 7";

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
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }


    @Test()
    public void sort_simple_terms_scenery_three_with_success() {
        //Arrange
        String expression = "(x - 4x^3) + (3x^2 - 2x^4)";

        String stepTwoValueExpected = "x - 4x^3 + 3x^2 - 2x^4";
        String lastStepValueExpected = "-2x^4 - 4x^3 + 3x^2 + x";

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
    public void sort_simple_terms_scenery_four_with_success() {
        //Arrange
        String expression = "(3x) + (5x^2 + 2x)";

        String stepTwoValueExpected = "3x + 5x^2 + 2x";
        String stepThreeValueExpected = "5x^2 + 3x + 2x";
        String lastStepValueExpected = "5x^2 + 5x";

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
        assertEquals(stepThreeResultExpected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepFourResultExpected, stepThree.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void sort_simple_terms_scenery_five_with_success() {
        //Arrange
        String expression = "(5x^2 - 2x) - (3x^3)";

        String stepTwoValueExpected = "5x^2 - 2x - 3x^3";
        String lastStepValueExpected = "-3x^3 + 5x^2 - 2x";

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
    public void sort_complex_terms_scenery_one_with_success() {
        //Arrange
        String expression = "(2x^2 + 5x^3 + x^3) - (3x - 3x^2 + x^4)";

        String stepTwoValueExpected = "2x^2 + 5x^3 + x^3 - 3x + 3x^2 - x^4";
        String stepThreeValueExpected = "-x^4 + x^3 + 5x^3 + 3x^2 + 2x^2 - 3x";
        String lastStepValueExpected = "-x^4 + 6x^3 + 5x^2 - 3x";

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
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void sort_complex_terms_scenery_two_with_success() {
        //Arrange
        String expression = "(a^3 + 2a^2 - 5) - (a^3 - a^2 - 5)";

        String stepTwoValueExpected = "a^3 + 2a^2 - 5 - a^3 + a^2 + 5";
        String stepThreeValueExpected = "a^3 - a^3 + 2a^2 + a^2 - 5 + 5";
        String lastStepValueExpected = "3a^2";

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
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void sort_complex_terms_scenery_three_with_success() {
        //Arrange
        String expression = "(3x^3 - (2x^2 + 12x^4)) - (-3 + (2x^3 - 8))";

        String stepTwoValueExpected = "3x^3 - 2x^2 - 12x^4 + 3 - 2x^3 + 8";
        String stepThreeValueExpected = "-12x^4 + 3x^3 - 2x^3 - 2x^2 + 3 + 8";
        String lastStepValueExpected = "-12x^4 + x^3 - 2x^2 + 11";

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
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

}
