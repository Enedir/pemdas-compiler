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

public class PolynomialRuleMultiplyNumbersTest {

    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String stepTwoExplicationExpected;
    private String stepThreeExplicationExpected;
    private String finalResultExplicationExpected;
    private List<NumericValueVariable> userInput = new ArrayList<>();


    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new PolynomialNumericValueExpertSystem();
        stepTwoExplicationExpected = "Substituindo os valores nas variáveis correspondentes.";
        stepThreeExplicationExpected = "Multiplicando os valores.";
        finalResultExplicationExpected = "Somando os valores.";

        userInput.add(new NumericValueVariable("a", 777));
        userInput.add(new NumericValueVariable("y", 3));
        userInput.add(new NumericValueVariable("z", 4));
    }

    @Test()
    public void multiply_few_numbers_scenery_one_with_success() {
        //Arrange
        String expression = "2 * 4 * 5";

        String lastStepValueExpected = "40";

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
    public void multiply_many_numbers_and_variables_scenery_one_with_success() {
        //Arrange
        String expression = "8 * 1 * y * z";

        int positionTwo = 1;

        String stepTwoValueExpected = "8 * 1 * 3 * 4";
        String lastStepValueExpected = "96";

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
        assertEquals(stepTwo.getReason(), stepTwoExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), stepThreeExplicationExpected);
    }

    @Test()
    public void multiply_and_sum_numbers_scenery_one_with_success() {
        //Arrange
        String expression = "2 + 8 * 2 + 1 * y * z * 2 - 5";

        int positionTwo = 1;
        int positionThree = 2;

        String stepTwoValueExpected = "2 + 8 * 2 + 1 * 3 * 4 * 2 - 5";
        String stepThreeValueExpected = "2 + 16 + 24 - 5";
        String lastStepValueExpected = "37";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyseNumeric(expertSystem, AnswerType.BEST, userInput, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(positionTwo);
        Step stepThree = answer.getSteps().get(positionThree);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), stepTwoExplicationExpected);
        assertEquals(stepThree.getMathExpression(), stepThreeValueExpected);
        assertEquals(stepThree.getReason(), stepThreeExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }
}
