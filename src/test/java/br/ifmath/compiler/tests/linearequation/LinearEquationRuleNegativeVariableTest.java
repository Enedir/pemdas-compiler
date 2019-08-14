package br.ifmath.compiler.tests.linearequation;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.*;
import br.ifmath.compiler.domain.expertsystem.linearequation.LinearEquationExpertSystem;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class LinearEquationRuleNegativeVariableTest {

    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String negativeVariableExplicationExpected;
    private String finalResultExplicationExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new LinearEquationExpertSystem();
        negativeVariableExplicationExpected = "Multiplicação de todos os termos da equação por -1 pois a variável é negativa.";
        finalResultExplicationExpected = "Resultado final.";
    }


    @Test()
    public void  linear_equation_should_use_negative_variable_rule_scenery_one_with_success()  {
        //Arrange
        String expression = "-2x = 4";

        int positionTwo = 1;

        String stepTwoValueExpected = " -2x * ( -1) = 4 * ( -1)";
        String lastStepValueExpected = "x =  -2";

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
        assertEquals(stepTwo.getReason(), negativeVariableExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_use_negative_variable_rule_scenery_two_with_success() {
        //Arrange
        String expression = "-2x = 4";

        int positionTwo = 1;

        String stepTwoValueExpected = " -2x * ( -1) = 4 * ( -1)";
        String lastStepValueExpected = "x =  -2";

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
        assertEquals(stepTwo.getReason(), negativeVariableExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_use_negative_variable_rule_scenery_three_with_success()  {
        //Arrange
        String expression = "5 + 6= -4x";

        int positionThree = 2;
        String lastStepValueExpected = "x = 11 / 4";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepThree = answer.getSteps().get(positionThree);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_use_negative_variable_rule_scenery_four_with_success() {
        //Arrange
        String expression = "5 = 6 - 4x";

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
    public void  linear_equation_should_use_negative_variable_rule_scenery_five_with_success()  {
        //Arrange
        String expression = "5 = 6 - 4x + 6";

        String lastStepValueExpected = "x = 7 / 4";

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
    public void  linear_equation_should_use_negative_variable_rule_scenery_six_with_success()  {
        //Arrange
        String expression = "2x -x = 4 + 6";

        String lastStepValueExpected = "x = 10";

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
    public void  linear_equation_should_use_negative_variable_rule_scenery_seven_with_success()  {
        //Arrange
        String expression = "-x = 4 - 7";

        int positionThree = 2;

        String stepThreeValueExpected = " -x * ( -1) =  -3 * ( -1)";
        String lastStepValueExpected = "x = 3";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepThree = answer.getSteps().get(positionThree);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepThree.getMathExpression(), stepThreeValueExpected);
        assertEquals(stepThree.getReason(), negativeVariableExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_use_negative_variable_rule_scenery_eight_with_success()  {
        //Arrange
        String expression = "2x -x -5x = 4 + 6 - 10";

        int positionThree = 2;

        String stepThreeValueExpected = " -4x * ( -1) = 0 * ( -1)";
        String lastStepValueExpected = "x = 0";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepThree = answer.getSteps().get(positionThree);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepThree.getMathExpression(), stepThreeValueExpected);
        assertEquals(stepThree.getReason(), negativeVariableExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_use_negative_variable_rule_scenery_nine_with_success()  {
        //Arrange
        String expression = "2x + 8 + x + 2 = 2x - 5 - x + 7";

        String lastStepValueExpected = "x =  -4";

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
    public void  linear_equation_should_use_negative_variable_rule_scenery_teen_with_success()  {
        //Arrange
        String expression = "2x + 2x - 9 = - 5 - 7";

        String lastStepValueExpected = "x =  -3 / 4";

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
    public void  linear_equation_should_use_negative_variable_rule_scenery_eleven_with_success()  {
        //Arrange
        String expression = "2x - 3x = - 5 - 7 + x";

        int positionThree = 3;

        String stepThreeValueExpected = " -2x * ( -1) =  -12 * ( -1)";
        String lastStepValueExpected = "x = 6";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepThree = answer.getSteps().get(positionThree);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepThree.getMathExpression(), stepThreeValueExpected);
        assertEquals(stepThree.getReason(), negativeVariableExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }
}
