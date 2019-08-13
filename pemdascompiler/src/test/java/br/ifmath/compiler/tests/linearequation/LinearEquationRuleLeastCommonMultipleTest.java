package br.ifmath.compiler.tests.linearequation;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.*;
import br.ifmath.compiler.domain.expertsystem.linearequation.LinearEquationExpertSystem;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class LinearEquationRuleLeastCommonMultipleTest {

    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String leastCommonMultipleExplicationExpected;
    private String finalResultExplicationExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new LinearEquationExpertSystem();
        leastCommonMultipleExplicationExpected = "Calcula-se o MMC de todos os denominadores (lembre-se que quando um termo está em uma fração, seu denominador é 1). Este número será o novo denominador de todos os termos da equação. Lembre-se que quando trocamos o denominador de uma fração, o numerador precisa sofrer uma correção. Este processo é conhecido como \"dividir pelo debaixo, multiplicar pelo de cima\".";
        finalResultExplicationExpected = "Resultado final.";
    }

    @Test()
    public void linear_equation_should_least_common_multiple_term_scenery_one_with_success()  {
        //Arrange
        String expression = "2x/3 + 5 = x/2 + 1 + x";

        int positionTwo = 1;

        String stepTwoValueExpected = "2 * 2x / 6 + 6 * 5 / 6 = 3 * x / 6 + 6 * 1 / 6 + 6 * x / 6";
        String lastStepValueExpected = "x = 24 / 5";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(positionTwo);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), leastCommonMultipleExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_least_common_multiple_term_scenery_two_with_success()  {
        //Arrange
        String expression = "5x/4 + 5 + 2x/5 = x/2 + 1 + x + 3x/2";

        int positionTwo = 1;

        String stepTwoValueExpected = "5 * 5x / 20 + 20 * 5 / 20 + 4 * 2x / 20 = 10 * x / 20 + 20 * 1 / 20 + 20 * x / 20 + 10 * 3x / 20";
        String lastStepValueExpected = "x = 80 / 27";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(positionTwo);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), leastCommonMultipleExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_least_common_multiple_term_scenery_three_with_success()  {
        //Arrange
        String expression = "(5x-2)/4 + 5 + 2x/5 = x/2 + 1 + x + 3x/2";

        int positionTwo = 1;

        String stepTwoValueExpected = "5 * (5x - 2) / 20 + 20 * 5 / 20 + 4 * 2x / 20 = 10 * x / 20 + 20 * 1 / 20 + 20 * x / 20 + 10 * 3x / 20";
        String lastStepValueExpected = "x = 70 / 27";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(positionTwo);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), leastCommonMultipleExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_least_common_multiple_term_scenery_four_with_success() {
        //Arrange
        String expression = "-(5x-2)/4 + 5 + 2x/5 = x/2 + 1 + x + 3x/2";

        int positionTwo = 1;

        String stepTwoValueExpected = "5 *  -(5x - 2) / 20 + 20 * 5 / 20 + 4 * 2x / 20 = 10 * x / 20 + 20 * 1 / 20 + 20 * x / 20 + 10 * 3x / 20";
        String lastStepValueExpected = "x = 90 / 77";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(positionTwo);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), leastCommonMultipleExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_least_common_multiple_term_scenery_five_with_success()  {
        //Arrange
        String expression = "-(5x-2)/4 + 5 - 2x/5 = x/2 - 1 + (-x) + 3x/2";

        int positionTwo = 1;

        String stepTwoValueExpected = "5 *  -(5x - 2) / 20 + 20 * 5 / 20 - 4 * 2x / 20 = 10 * x / 20 - 20 * 1 / 20 + 20 * ( -x) / 20 + 10 * 3x / 20";
        String lastStepValueExpected = "x = 130 / 53";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(positionTwo);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), leastCommonMultipleExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_least_common_multiple_term_scenery_six_with_success()  {
        //Arrange
        String expression = "-((5x-2)/4) + 5 - 2x/5 = x/2 - 1 + (-x) + 3x/14";

        int positionTwo = 1;

        String stepTwoValueExpected = " -35 * (5x - 2) / 140 + 140 * 5 / 140 - 28 * 2x / 140 = 70 * x / 140 - 140 * 1 / 140 + 140 * ( -x) / 140 + 10 * 3x / 140";
        String lastStepValueExpected = "x = 910 / 191";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(positionTwo);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), leastCommonMultipleExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_least_common_multiple_term_scenery_seven_with_success()  {
        //Arrange
        String expression = "-((5x-2)/4) + 5 - 2x/5 = x/2 - 1 + (-x)/(-1) + 3x/14";

        int positionTwo = 1;

        String stepTwoValueExpected = " -35 * (5x - 2) / 140 + 140 * 5 / 140 - 28 * 2x / 140 = 70 * x / 140 - 140 * 1 / 140 + ( -140) * ( -x) / 140 + 10 * 3x / 140";
        String lastStepValueExpected = "x = 910 / 471";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(positionTwo);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), leastCommonMultipleExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_least_common_multiple_term_scenery_one_with_fail()  {
        //Arrange
        String expression = "-((5x-2)/x) + 5 - 2x/5 = x/2 - 1 + (-x)/(-1) + 3x/14";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            // Assert
            assertThat(e, instanceOf(InvalidAlgebraicExpressionException.class));
        }

        // Assert
    }

    @Test()
    public void linear_equation_should_least_common_multiple_term_scenery_two_with_fail()  {
        //Arrange
        String expression = "-((5x-2)/-4x) + 5 - 2x/5 = x/2 - 1 + (-x)/(-1) + 3x/1";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            // Assert
            assertThat(e, instanceOf(InvalidAlgebraicExpressionException.class));
        }

        // Assert
    }

    @Test()
    public void linear_equation_should_least_common_multiple_term_scenery_three_with_fail()  {
        //Arrange
        String expression = "-((5x-2)/-4x) + 5 - 2x/5 = x/2 - 1 + (-x)/(-1) + 3x/1";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            // Assert
            assertThat(e, instanceOf(InvalidAlgebraicExpressionException.class));
        }

        // Assert
    }

    @Test()
    public void linear_equation_should_least_common_multiple_term_scenery_four_with_fail()  {
        //Arrange
        String expression = "-((5x-2)/(-x)) + 5 - 2x/5 = x/2 - 1 + (-x)/(-1) + 3x/14";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            // Assert
            assertThat(e, instanceOf(InvalidAlgebraicExpressionException.class));
        }

        // Assert
    }

    @Test()
    public void linear_equation_should_least_common_multiple_term_scenery_five_with_fail()  {
        //Arrange
        String expression = "-((5x-2)/4) + 5 - 2x/x = x/2 - 1 + (-x)/(-1) + 3x/14";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            // Assert
            assertThat(e, instanceOf(InvalidAlgebraicExpressionException.class));
        }
    }

    @Test()
    public void linear_equation_should_least_common_multiple_term_scenery_six_with_fail() {
        //Arrange
        String expression = "-((5x-2)/4) + 5 - 2x/5 = x/2 - 1 + (-x)/(-1) + 3x/14x";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            // Assert
            assertThat(e, instanceOf(InvalidAlgebraicExpressionException.class));
        }
    }
}
