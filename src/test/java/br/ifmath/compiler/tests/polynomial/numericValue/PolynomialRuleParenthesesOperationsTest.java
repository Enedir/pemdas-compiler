package br.ifmath.compiler.tests.polynomial.numericValue;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.AnswerType;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.domain.expertsystem.polynomial.numericvalue.PolynomialNumericValueExpertSystem;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PolynomialRuleParenthesesOperationsTest {
    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String stepTwoExplicationExpected;
    private String stepThreeExplicationExpected;
    private String stepFourExplicationExpected;
    private String stepFiveExplicationExpected;
    private String stepSixExplicationExpected;
    private String stepSevenExplicationExpected;
    private String finalResultExplicationExpected;
    private List<NumericValueVariable> userInput = new ArrayList<>();


    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new PolynomialNumericValueExpertSystem();
        stepTwoExplicationExpected = "Substituindo os valores nas variáveis correspondentes.";
        stepThreeExplicationExpected = "Elevando os valores a suas potências dentro dos parênteses.";
        stepFourExplicationExpected = "Multiplicando os valores dentro dos parênteses.";
        stepFiveExplicationExpected = "Somando os valores dentro dos parênteses.";
        stepSixExplicationExpected = "Somando os valores.";
        stepSevenExplicationExpected = "Multiplicando os valores.";

        finalResultExplicationExpected = "Resolvendo as suas  potências.";

        userInput.add(new NumericValueVariable("a", 777));
        userInput.add(new NumericValueVariable("y", 3));
        userInput.add(new NumericValueVariable("z", 4));
        userInput.add(new NumericValueVariable("x", 2));
    }

    @Test()
    public void sum_operation_in_parentheses_scenery_one_with_success() {
        //Arrange
        String expression = "y * (8 - 2)";

        String stepTwoValueExpected = "3 . (8 - 2)";
        String stepThreeValueExpected = "3 . 6";
        String lastStepValueExpected = "18";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyseNumeric(expertSystem, AnswerType.BEST, userInput, expression);
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
        assertEquals(stepThree.getReason(), stepFiveExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void multiple_operations_in_parentheses_scenery_one_with_success() {
        //Arrange
        String expression = "y * (8 - (2 * (3 ^ 2)))";


        String stepTwoValueExpected = "3 . (8 - (2 . (3 ^ 2)))";
        String stepThreeValueExpected = "3 . (8 - (2 . 9))";
        String stepFourValueExpected = "3 . (8 - 18)";
        String stepFiveValueExpected = "3 . -10";
        String lastStepValueExpected = "-30";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyseNumeric(expertSystem, AnswerType.BEST, userInput, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 5);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 4);
        Step stepFour = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepFive = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), stepTwoExplicationExpected);
        assertEquals(stepThree.getMathExpression(), stepThreeValueExpected);
        assertEquals(stepThree.getReason(), stepThreeExplicationExpected);
        assertEquals(stepFour.getMathExpression(), stepFourValueExpected);
        assertEquals(stepFour.getReason(), stepFourExplicationExpected);
        assertEquals(stepFive.getMathExpression(), stepFiveValueExpected);
        assertEquals(stepFive.getReason(), stepFiveExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void operations_in_double_parentheses_scenery_one_with_success() {
        //Arrange
        String expression = "(y - 7) + (2 ^ z)";


        String stepTwoValueExpected = "(3 - 7) + (2 ^ 4)";
        String stepThreeValueExpected = "-4 + (2 ^ 4)";
        String stepFourValueExpected = "-4 + 16";
        String lastStepValueExpected = "12";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyseNumeric(expertSystem, AnswerType.BEST, userInput, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 4);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepFour = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), stepTwoExplicationExpected);
        assertEquals(stepThree.getMathExpression(), stepThreeValueExpected);
        assertEquals(stepThree.getReason(), stepFiveExplicationExpected);
        assertEquals(stepFour.getMathExpression(), stepFourValueExpected);
        assertEquals(stepFour.getReason(), stepThreeExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), stepSixExplicationExpected);
    }

    @Test()
    public void multiple_operations_in_multiple_parentheses_scenery_one_with_success() {
        //Arrange
        String expression = "(y + 3 * (2 - 7 + (a - 770))) + (z * (6 - (2 ^ 2)))";

        String stepTwoValueExpected = "(3 + 3 . (2 - 7 + (777 - 770))) + (4 . (6 - (2 ^ 2)))";
        String stepThreeValueExpected = "(3 + 3 . (2 - 7 + 7)) + (4 . (6 - (2 ^ 2)))";
        String stepFourValueExpected = "(3 + 3 . 2) + (4 . (6 - (2 ^ 2)))";
        String stepFiveValueExpected = "(3 + 6) + (4 . (6 - (2 ^ 2)))";
        String stepSixValueExpected = "9 + (4 . (6 - (2 ^ 2)))";
        String stepSevenValueExpected = "9 + (4 . (6 - 4))";
        String stepEightValueExpected = "9 + (4 . 2)";
        String stepNineValueExpected = "9 + 8";
        String lastStepValueExpected = "17";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyseNumeric(expertSystem, AnswerType.BEST, userInput, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 9);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 8);
        Step stepFour = answer.getSteps().get(answer.getSteps().size() - 7);
        Step stepFive = answer.getSteps().get(answer.getSteps().size() - 6);
        Step stepSix = answer.getSteps().get(answer.getSteps().size() - 5);
        Step stepSeven = answer.getSteps().get(answer.getSteps().size() - 4);
        Step stepEight = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepNine = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), stepTwoExplicationExpected);
        assertEquals(stepThree.getMathExpression(), stepThreeValueExpected);
        assertEquals(stepThree.getReason(), stepFiveExplicationExpected);
        assertEquals(stepFour.getMathExpression(), stepFourValueExpected);
        assertEquals(stepFour.getReason(), stepFiveExplicationExpected);
        assertEquals(stepFive.getMathExpression(), stepFiveValueExpected);
        assertEquals(stepFive.getReason(), stepFourExplicationExpected);
        assertEquals(stepSix.getMathExpression(), stepSixValueExpected);
        assertEquals(stepSix.getReason(), stepFiveExplicationExpected);
        assertEquals(stepSeven.getMathExpression(), stepSevenValueExpected);
        assertEquals(stepSeven.getReason(), stepThreeExplicationExpected);
        assertEquals(stepEight.getMathExpression(), stepEightValueExpected);
        assertEquals(stepEight.getReason(), stepFiveExplicationExpected);
        assertEquals(stepNine.getMathExpression(), stepNineValueExpected);
        assertEquals(stepNine.getReason(), stepFourExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), stepSixExplicationExpected);
    }

    @Test()
    public void multiply_operation_in_double_parentheses_scenery_one_with_success() {
        //Arrange
        String expression = "(2 * 7 * (y - 2))";


        String stepTwoValueExpected = "(2 . 7 . (3 - 2))";
        String stepThreeValueExpected = "(2 . 7 . 1)";
        String lastStepValueExpected = "14";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyseNumeric(expertSystem, AnswerType.BEST, userInput, expression);
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
        assertEquals(stepThree.getReason(), stepFiveExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), stepFourExplicationExpected);
    }

    @Test()
    public void multiple_operations_with_parentheses_scenery_one_with_success() {
        //Arrange
        String expression = "3 * x^8 + 2 * x + 2 * (5 + 7 * x)";


        String stepTwoValueExpected = "3 . 2^8 + 2 . 2 + 2 . (5 + 7 . 2)";
        String stepThreeValueExpected = "3 . 2^8 + 2 . 2 + 2 . (5 + 14)";
        String stepFourValueExpected = "3 . 2^8 + 2 . 2 + 2 . 19";
        String stepFiveValueExpected = "3 . 256 + 2 . 2 + 2 . 19";
        String stepSixValueExpected = "768 + 4 + 38";
        String lastStepValueExpected = "810";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyseNumeric(expertSystem, AnswerType.BEST, userInput, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 6);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 5);
        Step stepFour = answer.getSteps().get(answer.getSteps().size() - 4);
        Step stepFive = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepSix = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), stepTwoExplicationExpected);
        assertEquals(stepThree.getMathExpression(), stepThreeValueExpected);
        assertEquals(stepThree.getReason(), stepFourValueExpected);
        assertEquals(stepFour.getMathExpression(), stepFourValueExpected);
        assertEquals(stepFour.getReason(), stepFiveExplicationExpected);
        assertEquals(stepFive.getMathExpression(), stepFiveValueExpected);
        assertEquals(stepFive.getReason(), finalResultExplicationExpected);
        assertEquals(stepSix.getMathExpression(), stepSixValueExpected);
        assertEquals(stepSix.getReason(), stepSevenExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), stepSixExplicationExpected);
    }
}
