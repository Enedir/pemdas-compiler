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

public class PolynomialAddAndSubRuleGroupSimilarTermsTest {
    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String finalResultExplicationExpected;
    private String stepTwoResultExpected;
    private String stepThreeResultExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new PolynomialAddAndSubExpertSystem();
        stepTwoResultExpected = "Aplicando regra de troca de sinais em operações prioritárias, em duplas negações ou em somas de números negativos.Também, removendo parenteses dos polinômios.";
        stepThreeResultExpected = "Removendo parênteses dos polinômios";
        finalResultExplicationExpected = "Soma dos termos semelhantes.";

    }

    @Test()
    public void group_simple_terms_scenery_one_with_success() {
        //Arrange
        String expression = "(x + 2x) - (7 - 2)";

        String stepTwoValueExpected = "x + 2x - 7 + 2";
        String lastStepValueExpected = "3x - 5";

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
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void group_simple_terms_scenery_two_with_success() {
        //Arrange
        String expression = "(-3x^2 - 2x^2) - (9x + x)";

        String stepTwoValueExpected = "-3x^2 - 2x^2 - 9x - x";
        String lastStepValueExpected = "-5x^2 - 10x";

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
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }


    @Test()
    public void group_only_numbers_test_scenery_one_with_success() {
        //Arrange
        String expression = "(2 + (7 - 5)) + (20 - 5)";

        String stepTwoValueExpected = "2 + 7 - 5 + 20 - 5";
        String lastStepValueExpected = "19";

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
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void group_only_variables_test_scenery_one_with_success() {
        //Arrange
        String expression = "(2x^2 + (5x^3 + x^3)) - (3x - 3x^2)";

        String stepTwoValueExpected = "2x^2 + 5x^3 + x^3 - 3x + 3x^2";
        String lastStepValueExpected = "6x^3 + 5x^2 - 3x";

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
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void group_variables_and_numbers_test_scenery_one_with_success() {
        //Arrange
        String expression = "(3x^3 - (2x^2 + 12x^4)) - (-3 + (2x^3 - 8))";

        String stepTwoValueExpected = "3x^3 - 2x^2 - 12x^4 + 3 - 2x^3 + 8";
        String lastStepValueExpected = "-12x^4 + x^3 - 2x^2 + 11";

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
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void group_variables_and_numbers_test_scenery_two_with_success() {
        //Arrange
        String expression = "((3x^3 - 3x) + 8) - (2x^2 - (2x^3 - (5x + 9)))";

        String stepTwoValueExpected = "3x^3 - 3x + 8 - 2x^2 + 2x^3 - 5x - 9";
        String lastStepValueExpected = "5x^3 - 2x^2 - 8x - 1";

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
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }


}
