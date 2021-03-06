package br.ifmath.compiler.tests.polynomial.numericValue;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.*;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.Monomial;
import br.ifmath.compiler.domain.expertsystem.polynomial.numericvalue.PolynomialNumericValueExpertSystem;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PolynomialRuleNumbersPotentiationTest {

    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String stepTwoExplicationExpected;
    private String stepThreeExplicationExpected;
    private String stepFourExplicationExpected;
    private String finalResultExplicationExpected;
    private List<Monomial> userInput = new ArrayList<>();


    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new PolynomialNumericValueExpertSystem();
        stepTwoExplicationExpected = "Substituindo os valores nas variáveis correspondentes.";
        stepThreeExplicationExpected = "Resolvendo as suas potências.";
        stepFourExplicationExpected = "Multiplicando os valores.";
        finalResultExplicationExpected = "Somando os valores.";


        userInput.add(new Monomial("a", 777));
        userInput.add(new Monomial("y", 3));
        userInput.add(new Monomial("z", 4));
    }

    @Test()
    public void power_few_numbers_scenery_one_with_success() {
        //Arrange
        String expression = "2 ^ 2 - 4";

        String lastStepValueExpected = "0";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyseNumeric(expertSystem, AnswerType.BEST, userInput, expression);
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
        String expression = "2 ^ -2";

        String lastStepValueExpected = "0.25";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyseNumeric(expertSystem, AnswerType.BEST, userInput, expression);
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
        String expression = "8 * 1 * y ^ -2 + 5";

        int positionTwo = 2;

        String stepTwoValueExpected = "8 * 1 * 0.1111 + 5";
        String lastStepValueExpected = "5.8888";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyseNumeric(expertSystem, AnswerType.BEST, userInput, expression);
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
    public void multiple_operations_example_scenery_one_with_success() {
        //Arrange
        String expression = "3 * y ^ 3 + 2 * y ^ 2 - 4 * y * z";


        String stepTwoValueExpected = "3 * 3 ^ 3 + 2 * 3 ^ 2 - 4 * 3 * 4";
        String stepThreeValueExpected = "3 * 27 + 2 * 9 - 4 * 3 * 4";
        String stepFourValueExpected = "81 + 18 - 48";
        String lastStepValueExpected = "51";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyseNumeric(expertSystem, AnswerType.BEST, userInput, expression);
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
}
