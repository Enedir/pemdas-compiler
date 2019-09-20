package br.ifmath.compiler.tests.polynomial;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.*;
import br.ifmath.compiler.domain.expertsystem.polynomial.PolynomialExpertSystem;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class PolynomialRuleNumbersPotentiationTest {

    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String stepTwoExplicationExpected;
    private String stepThreeExplicationExpected;
    private String stepFourExplicationExpected;
    private String finalResultExplicationExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new PolynomialExpertSystem();
        stepTwoExplicationExpected = "Substituindo os valores nas variáveis correspondentes.";
        stepThreeExplicationExpected = "Elevando os valores a suas potências.";
        stepFourExplicationExpected = "Multiplicando os valores.";
        finalResultExplicationExpected = "Somando os valores.";
    }

    @Test()
    public void power_few_numbers_scenery_one_with_success() {
        //Arrange
        String expression = "x = 2 ^ 2 - 4";

        String lastStepValueExpected = "x = 0";

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
    public void power_a_number_with_negative_exponent_scenery_one_with_success() {
        //Arrange
        String expression = "x = 2 ^ -2";

        String lastStepValueExpected = "x = 0.25";

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
        assertEquals(finalStep.getReason(), stepThreeExplicationExpected);
    }

    @Test()
    public void power_many_numbers_and_variables_scenery_one_with_success() {
        //Arrange
        String expression = "x = 8 * 1 * y ^ -2 + 5";

        int positionTwo = 2;

        String stepTwoValueExpected = "x = 8 * 1 * 0.1111 + 5";
        String lastStepValueExpected = "x = 5.8888";

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
        assertEquals(stepTwo.getReason(), stepThreeExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void professor_Ailton_example_scenery_one_with_success() {
        //Arrange
        String expression = "x = -3 * y ^ 3 + 2 * y ^ 2 - 4 * y * z";


        String stepTwoValueExpected = "x = -3 * -1 ^ 3 + 2 * -1 ^ 2 - 4 * -1 * 3";
        String stepThreeValueExpected = "x = -3 * -1 + 2 * 1 - 4 * -1 * 3";
        String stepFourValueExpected = "x = 3 + 2 + 12";
        String lastStepValueExpected = "x = 17";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(1);
        Step stepThree = answer.getSteps().get(2);
        Step stepFour = answer.getSteps().get(3);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), stepTwoExplicationExpected);
        assertEquals(stepThree.getMathExpression(), stepThreeValueExpected);
        assertEquals(stepThree.getReason(), stepThreeExplicationExpected);
        assertEquals(stepFour.getMathExpression(), stepFourValueExpected);
        assertEquals(stepFour.getReason(), stepFourExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void power_with_sum_in_exponentiation_scenery_one_with_fail() {
        //Arrange
        String expression = "x = 2 ^ (1 + 2) * y";

        // Act
        try {
            IAnswer answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(NullPointerException.class));
        }
    }
}
