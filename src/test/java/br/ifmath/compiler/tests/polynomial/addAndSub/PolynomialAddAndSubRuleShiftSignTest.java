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

public class PolynomialAddAndSubRuleShiftSignTest {
    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String finalResultExplicationExpected;
    private String stepTwoExplicationExpected;
    private String stepThreeExplicationExpected;
    private String stepFourExplicationExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new PolynomialAddAndSubExpertSystem();
        stepTwoExplicationExpected = "Aplicando a regra de troca de sinais em operações prioritárias, em duplas negações ou " +
                "em somas de números negativos. E, removendo os parênteses dos polinômios.";
        stepThreeExplicationExpected = "Removendo os parênteses dos polinômios";
        stepFourExplicationExpected = "Agrupando os termos semelhantes.";
        finalResultExplicationExpected = "Soma dos termos semelhantes.";

    }

    @Test()
    public void take_off_parentheses_scenery_one_with_success() {
        //Arrange
        String expression = "(8 + 5) + (4 - 3)";
        String stepTwoValueExpected = "8 + 5 + 4 - 3";
        String lastStepValueExpected = "14";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), stepThreeExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void shift_sign_simple_numbers_scenery_one_with_success() {
        //Arrange
        String expression = "(2 + 5) - (4 - 2)";
        String stepTwoValueExpected = "2 + 5 - 4 + 2";
        String lastStepValueExpected = "5";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), stepTwoExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void shift_sign_negative_on_both_sides_scenery_one_with_success() {
        //Arrange
        String expression = "(6 - (2x - 5x)) - (-2)";
        String stepTwoValueExpected = "6 - 2x + 5x + 2";
        String stepThreeValueExpected = "-2x + 5x + 6 + 2";
        String lastStepValueExpected = "3x + 8";

        // Act
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

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), stepTwoExplicationExpected);
        assertEquals(stepThree.getMathExpression(), stepThreeValueExpected);
        assertEquals(stepThree.getReason(), stepFourExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }


    @Test()
    public void shift_sign_simple_variables_scenery_one_with_success() {
        //Arrange
        String expression = "(2x + 5) - (-4x - (2x + 4))";
        String stepTwoValueExpected = "2x + 5 + 4x + 2x + 4";
        String stepThreeValueExpected = "2x + 4x + 2x + 5 + 4";
        String lastStepValueExpected = "8x + 9";

        // Act
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

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), stepTwoExplicationExpected);
        assertEquals(stepThree.getMathExpression(), stepThreeValueExpected);
        assertEquals(stepThree.getReason(), stepFourExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void shift_sign_simple_variables_scenery_two_with_success() {
        //Arrange
        String expression = "(3x) - (2x)";

        String stepTwoValueExpected = "3x - 2x";
        String lastStepValueExpected = "x";

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
        assertEquals(stepThreeExplicationExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void shift_sign_many_values_scenery_one_with_success() {
        //Arrange
        String expression = "(2x^2 + 5) - (4x^2 + 3 - x^2 - (-2x + 4))";
        String stepTwoValueExpected = "2x^2 + 5 - 4x^2 - 3 + x^2 - 2x + 4";
        String stepThreeValueExpected = "2x^2 - 4x^2 + x^2 - 2x + 5 - 3 + 4";
        String lastStepValueExpected = "-x^2 - 2x + 6";

        // Act
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

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), stepTwoExplicationExpected);
        assertEquals(stepThree.getMathExpression(), stepThreeValueExpected);
        assertEquals(stepThree.getReason(), stepFourExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void shift_sign_numbers_and_variables_scenery_one_with_success() {
        //Arrange
        String expression = "(2 + 2x) - ((-4x + 2) - 7)";
        String stepTwoValueExpected = "2 + 2x + 4x - 2 + 7";
        String stepThreeValueExpected = "2x + 4x - 2 + 2 + 7";
        String lastStepValueExpected = "6x + 7";

        // Act
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

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), stepTwoExplicationExpected);
        assertEquals(stepThree.getMathExpression(), stepThreeValueExpected);
        assertEquals(stepThree.getReason(), stepFourExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void shift_sign_on_left_side_scenery_one_with_success() {
        //Arrange
        String expression = "(2x^2 - (2x - 3x + x^2)) - (-4x)";

        String stepTwoValueExpected = "2x^2 - 2x + 3x - x^2 + 4x";
        String stepThreeValueExpected = "2x^2 - x^2 + 3x - 2x + 4x";
        String lastStepValueExpected = "x^2 + 5x";

        // Act
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

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), stepTwoExplicationExpected);
        assertEquals(stepThree.getMathExpression(), stepThreeValueExpected);
        assertEquals(stepThree.getReason(), stepFourExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void shift_sign_on_both_sides_scenery_one_with_success() {
        //Arrange

        String expression = "(9x^2 - (-7x + 2x^2 + x)) - (- 4x^2 - 3x - 2x^2 + 6)";
        String stepTwoValueExpected = "9x^2 + 7x - 2x^2 - x + 4x^2 + 3x + 2x^2 - 6";
        String stepThreeValueExpected = "9x^2 - 2x^2 + 4x^2 + 2x^2 + 7x + 3x - x - 6";
        String lastStepValueExpected = "13x^2 + 9x - 6";

        // Act
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

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), stepTwoExplicationExpected);
        assertEquals(stepThree.getMathExpression(), stepThreeValueExpected);
        assertEquals(stepThree.getReason(), stepFourExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }


}
