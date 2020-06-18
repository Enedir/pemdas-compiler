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
    private String stepFourResultExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new PolynomialAddAndSubExpertSystem();
        stepTwoResultExpected = "Aplicando a regra de troca de sinais em operações prioritárias, em duplas negações ou " +
                "em somas de números negativos. E, removendo os parênteses dos polinômios.";
        stepThreeResultExpected = "Removendo os parênteses dos polinômios.";
        stepFourResultExpected = "Agrupando os termos semelhantes.";
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
        String stepThreeValueExpected = "5x^3 + x^3 + 2x^2 + 3x^2 - 3x";
        String lastStepValueExpected = "6x^3 + 5x^2 - 3x";

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
    public void group_terms_without_parentheses_test_scenery_one_with_success() {
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
    public void group_terms_without_parentheses_test_scenery_two_with_success() {
        //Arrange
        String expression = "(-x^4 + 2x^3 + x + 2) + (x^4 - 2x^3 + x - 2)";

        String stepTwoValueExpected = "-x^4 + 2x^3 + x + 2 + x^4 - 2x^3 + x - 2";
        String stepThreeValueExpected = "-x^4 + x^4 + 2x^3 - 2x^3 + x + x + 2 - 2";
        String lastStepValueExpected = "2x";

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
    public void group_terms_without_parentheses_test_scenery_three_with_success() {
        //Arrange
        String expression = "(-x^3 - 2x - x^2 - 1) - (-x^3 - 2x + x^2 + 1)";

        String stepTwoValueExpected = "-x^3 - 2x - x^2 - 1 + x^3 + 2x - x^2 - 1";
        String stepThreeValueExpected = "-x^3 + x^3 - x^2 - x^2 + 2x - 2x - 1 - 1";
        String lastStepValueExpected = "-2x^2 - 2";

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
    public void group_variables_and_numbers_test_scenery_one_with_success() {
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

    @Test()
    public void group_variables_and_numbers_test_scenery_two_with_success() {
        //Arrange
        String expression = "(3x^3 - 3x + 8) - (2x^2 - 2x^3 - 5x + 9)";

        String stepTwoValueExpected = "3x^3 - 3x + 8 - 2x^2 + 2x^3 + 5x - 9";
        String stepThreeValueExpected = "3x^3 + 2x^3 - 2x^2 - 3x + 5x + 8 - 9";
        String lastStepValueExpected = "5x^3 - 2x^2 + 2x - 1";

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
