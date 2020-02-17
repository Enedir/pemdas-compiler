package br.ifmath.compiler.tests.linearequation;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.*;
import br.ifmath.compiler.domain.expertsystem.linearequation.LinearEquationExpertSystem;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class LinearEquationRuleShiftSignalTest {

    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String shiftSignalExplicationExpected;
    private String finalResultExplicationExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new LinearEquationExpertSystem();
        shiftSignalExplicationExpected = "Multiplicação de todos os termos da equação por -1 pois a variável é negativa.";
        finalResultExplicationExpected = "Resultado final.";
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_one_with_success()  {
        //Arrange
        String expression = "-x - (-5) = x + 5";

        int positionTwo = 4;

        String stepTwoValueExpected = " -2x * (-1) = 0 * (-1)";
        String lastStepValueExpected = "x = 0";

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
        assertEquals(stepTwo.getReason(), shiftSignalExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_two_with_success()  {
        //Arrange
        String expression = "-x = 3 + 5";

        int positionTwo = 2;

        String stepTwoValueExpected = " -x * (-1) = 8 * (-1)";
        String lastStepValueExpected = "x =  -8";

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
        assertEquals(stepTwo.getReason(), shiftSignalExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }


    @Test()
    public void linear_equation_should_shift_signal_scenery_three_with_success()  {
        //Arrange
        String expression = "-(-x) = 3 + 5";

        String lastStepValueExpected = "x = 8";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_four_with_success()  {
        //Arrange
        String expression = "2x = 6 - (-x) - 8";

        String lastStepValueExpected = "x =  -2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_five_with_success()  {
        //Arrange
        String expression = "2x - (-6) = 6 + 5 - 4 - (-x) - 8";

        String lastStepValueExpected = "x =  -7";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_six_with_success() {
        //Arrange
        String expression = "2x - (-6) = 6 + 5 - 4 + 8 - (-x)";

        String lastStepValueExpected = "x = 9";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_seven_with_success()  {
        //Arrange
        String expression = "x - (3 + 5)  = 0";

        String lastStepValueExpected = "x = 8";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_eight_with_success() {
        //Arrange
        String expression = "x - (-3 + 5)  = 0";

        String lastStepValueExpected = "x = 2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_nine_with_success()  {
        //Arrange
        String expression = "x + (-3 + 5)  = 0";

        String lastStepValueExpected = "x =  -2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_ten_with_success()  {
        //Arrange
        String expression = "x - (3 + ( 4 - x))  = 0";

        String lastStepValueExpected = "x = 7 / 2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }


    @Test()
    public void linear_equation_should_shift_signal_scenery_eleven_with_success()  {
        //Arrange
        String expression = "x - 3 + 5  = 0";

        String lastStepValueExpected = "x =  -2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_twelve_with_success()  {
        //Arrange
        String expression = "-(x + 7 - 5) + 3  = 0";

        String lastStepValueExpected = "x = 1";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }


    @Test()
    public void linear_equation_should_shift_signal_scenery_thirteen_with_success() {
        //Arrange
        String expression = "x - -3  = 0";

        String lastStepValueExpected = "x =  -3";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_fourteen_with_success()  {
        //Arrange
        String expression = "x - -3  = 0";

        String lastStepValueExpected = "x =  -3";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_fifteen_with_success()  {
        //Arrange
        String expression = "x + -3  = 0";

        String lastStepValueExpected = "x = 3";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_sixteen_with_success()  {
        //Arrange
        String expression = "-(-x + 2) + 3 = 0";

        String lastStepValueExpected = "x =  -1";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_seventeen_with_success()  {
        //Arrange
        String expression = "-(-x + 2)= 0";

        String lastStepValueExpected = "x = 2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_eightteen_with_success() {
        //Arrange
        String expression = "-(-x) + 3 = 0";

        String lastStepValueExpected = "x =  -3";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_nineteen_with_success() {
        //Arrange
        String expression = "-(-x) + 3 + 2x = 0";

        String lastStepValueExpected = "x =  -1";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_twenty_with_success() {
        //Arrange
        String expression = "-(-x + 2) + 3 - (3 + 2x - 4) - (3x + 1)= 0";

        String lastStepValueExpected = "x = 1 / 4";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_twenty_one_with_success()  {
        //Arrange
        String expression = "(-x + 2) + 3 - (-3 + 2x - 4) - (3x + 1)= 0";

        String lastStepValueExpected = "x = 11 / 6";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_twenty_two_with_success()  {
        //Arrange
        String expression = "(-x + 2) + 3 + (-3 + 2x - 4) - (3x + 1)= 0";

        String lastStepValueExpected = "x =  -3 / 2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_twenty_three_with_success()  {
        //Arrange
        String expression = "3 + (3 + 2x - 4) + (3x + 1)= 0";

        String lastStepValueExpected = "x =  -3 / 5";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_twenty_four_with_success()  {
        //Arrange
        String expression = "3 - (3 + 2x - 4) + (3x + 1)= 0";

        String lastStepValueExpected = "x =  -5";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_shift_signal_scenery_twenty_one_fail()  {
        //Arrange
        String expression = "x + 8 = 2x - (x + 7)";

        // Act
        try {
            IAnswer answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidAlgebraicExpressionException.class));
        }
    }
}
