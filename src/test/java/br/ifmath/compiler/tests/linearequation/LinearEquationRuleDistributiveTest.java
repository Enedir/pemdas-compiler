package br.ifmath.compiler.tests.linearequation;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.*;
import br.ifmath.compiler.domain.expertsystem.linearequation.LinearEquationExpertSystem;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LinearEquationRuleDistributiveTest {

    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String distributiveExplicationExpected;
    private String finalResultExplicationExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new LinearEquationExpertSystem();
        distributiveExplicationExpected = "Utilizando a propriedade distributiva, o elemento externo multiplicou cada elemento da operação interna";
        finalResultExplicationExpected = "Resultado final.";
    }

    @Test()
    public void  linear_equation_should_use_distributive_rule_scenery_one_with_success() {
        //Arrange
        String expression = "5x = x + 9 * (-x - 1 + 8)";

        int positionTwo = 1;

        String stepTwoValueExpected = "5x = x - 9x - 9 + 72";
        String lastStepValueExpected = "x = 63 / 13";

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
        assertEquals(stepTwo.getReason(), distributiveExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_use_distributive_rule_scenery_two_with_success()  {
        //Arrange
        String expression = "5x = x + 9 * (x - 1 + 8)";

        int positionTwo = 1;

        String stepTwoValueExpected = "5x = x + 9x - 9 + 72";
        String lastStepValueExpected = "x =  -63 / 5";

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
        assertEquals(stepTwo.getReason(), distributiveExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_use_distributive_rule_scenery_three_with_success()  {
        //Arrange
        String expression = "5x = -9 * (x - 1 + 8)";

        int positionTwo = 1;

        String stepTwoValueExpected = "5x =  -9x + 9 - 72";
        String lastStepValueExpected = "x =  -9 / 2";

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
        assertEquals(stepTwo.getReason(), distributiveExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_use_distributive_rule_scenery_four_with_success()  {
        //Arrange
        String expression = "5x = (x - 1 + 8) * (-9)";

        int positionTwo = 1;

        String stepTwoValueExpected = "5x =  -9x + 9 - 72";
        String lastStepValueExpected = "x =  -9 / 2";

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
        assertEquals(stepTwo.getReason(), distributiveExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_use_distributive_rule_scenery_five_with_success()  {
        //Arrange
        String expression = "1 = 2x * (4 + 9)";

        int positionTwo = 1;

        String stepTwoValueExpected = "1 = 8x + 18x";
        String lastStepValueExpected = "x = 1 / 26";

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
        assertEquals(stepTwo.getReason(), distributiveExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_use_distributive_rule_scenery_six_with_success() {
        //Arrange
        String expression = "5x - 6 = 2 * (x + 9)";

        int positionTwo = 1;

        String stepTwoValueExpected = "5x - 6 = 2x + 18";
        String lastStepValueExpected = "x = 8";

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
        assertEquals(stepTwo.getReason(), distributiveExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_use_distributive_rule_scenery_seven_with_success() {
        //Arrange
        String expression = "5x = 9 * (x - 1 + 8)";

        int positionTwo = 1;

        String stepTwoValueExpected = "5x = 9x - 9 + 72";
        String lastStepValueExpected = "x =  -63 / 4";

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
        assertEquals(stepTwo.getReason(), distributiveExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_use_distributive_rule_scenery_eight_with_success()  {
        //Arrange
        String expression = "5x = 9 * (x - (x + 8))";

        int positionTwo = 1;
        int positionThree = 2;

        String stepTwoValueExpected = "5x = 9x - 9 * (x + 8)";
        String stepThreeValueExpected = "5x = 9x - 9x - 72";
        String lastStepValueExpected = "x =  -72 / 5";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(positionTwo);
        Step stepThree = answer.getSteps().get(positionThree);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), distributiveExplicationExpected);
        assertEquals(stepThree.getMathExpression(), stepThreeValueExpected);
        assertEquals(stepThree.getReason(), distributiveExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_use_distributive_rule_scenery_nine_with_success() {
        //Arrange
        String expression = "5 *  -(5x - 2) = 8";

        int positionTwo = 1;

        String stepTwoValueExpected = " -25x + 10 = 8";
        String lastStepValueExpected = "x = 2 / 25";

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
        assertEquals(stepTwo.getReason(), distributiveExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }
    
    @Test()
    public void linear_equation_should_use_distributive_rule_scenery_ten_with_success() {
        //Arrange
        String expression = "x+3*(-10)=-x";

        int positionTwo = 1;

        String secondStepValueExpected = "x - 30 =  -x";
        String lastStepValueExpected = "x = 15";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step secondStep = answer.getSteps().get(positionTwo);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(secondStep.getMathExpression(), secondStepValueExpected);
        assertEquals(secondStep.getReason(), distributiveExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }
    
    @Test()
    public void linear_equation_should_use_distributive_rule_scenery_eleven_with_success() {
        //Arrange
        String expression = "x+3*(-10x)=-1";

        int positionTwo = 1;

        String secondStepValueExpected = "x - 30x =  -1";
        String lastStepValueExpected = "x = 1 / 29";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step secondStep = answer.getSteps().get(positionTwo);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(secondStep.getMathExpression(), secondStepValueExpected);
        assertEquals(secondStep.getReason(), distributiveExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }
}
