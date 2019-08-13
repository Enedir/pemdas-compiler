package br.ifmath.compiler.tests.linearequation;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.*;
import br.ifmath.compiler.domain.expertsystem.linearequation.LinearEquationExpertSystem;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LinearEquationRuleCrossMultiplicationTest {

    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String crossMultiplicationExplicationExpected;
    private String finalResultExplicationExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new LinearEquationExpertSystem();
        finalResultExplicationExpected = "Resultado final.";
    }

    @Test()
    public void linear_equation_should_cross_multiplication_scenery_one_with_success() {
        //Arrange
        String expression = "5x / 4 = 3/5";

        int positionTwo = 1;

        String stepTwoValueExpected = "5x * 5 = 3 * 4";
        String lastStepValueExpected = "x = 12 / 25";
        crossMultiplicationExplicationExpected = "O numerador da fração antes do sinal de comparação é multiplicado pelo denominador da fração após o sinal de comparação, assim como, o numerador da fração após o sinal de comparação é multiplicado pelo denominador da fração antes do sinal de comparação. Uma informação importante a ser destacada é que este é o princípio da regra de 3.";

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
        assertEquals(stepTwo.getReason().trim(), crossMultiplicationExplicationExpected.trim());
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_cross_multiplication_scenery_two_with_success()  {
        //Arrange
        String expression = "-5x / 4 = -3/5";

        int positionTwo = 1;

        String stepTwoValueExpected = " -5x * 5 =  -3 * 4";
        String lastStepValueExpected = "x = 12 / 25";
        crossMultiplicationExplicationExpected = "O numerador da fração antes do sinal de comparação é multiplicado pelo denominador da fração após o sinal de comparação, assim como, o numerador da fração após o sinal de comparação é multiplicado pelo denominador da fração antes do sinal de comparação. Uma informação importante a ser destacada é que este é o princípio da regra de 3.";

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
        assertEquals(stepTwo.getReason().trim(), crossMultiplicationExplicationExpected.trim());
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_cross_multiplication_scenery_three_with_success() {
        //Arrange
        String expression = "3x / 2 = 5";

        int positionTwo = 1;

        String stepTwoValueExpected = "3x * 1 = 5 * 2";
        String lastStepValueExpected = "x = 10 / 3";
        crossMultiplicationExplicationExpected = "O numerador da fração antes do sinal de comparação é multiplicado pelo denominador da fração após o sinal de comparação, assim como, o numerador da fração após o sinal de comparação é multiplicado pelo denominador da fração antes do sinal de comparação. Uma informação importante a ser destacada é que este é o princípio da regra de 3. Por fim, quando um operando não está em uma fração, assume-se que seu denominador é 1.";

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
        assertEquals(stepTwo.getReason(), crossMultiplicationExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_cross_multiplication_scenery_four_with_success()  {
        //Arrange
        String expression = "3x / 2 = (5 + 1)";

        int positionTwo = 1;

        String stepTwoValueExpected = "3x * 1 = (5 + 1) * 2";
        String lastStepValueExpected = "x = 4";
        crossMultiplicationExplicationExpected = "O numerador da fração antes do sinal de comparação é multiplicado pelo denominador da fração após o sinal de comparação, assim como, o numerador da fração após o sinal de comparação é multiplicado pelo denominador da fração antes do sinal de comparação. Uma informação importante a ser destacada é que este é o princípio da regra de 3. Por fim, quando um operando não está em uma fração, assume-se que seu denominador é 1.";

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
        assertEquals(stepTwo.getReason(), crossMultiplicationExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_cross_multiplication_scenery_five_with_success() {
        //Arrange
        String expression = "x / 3 = (x + 1)/2";

        int positionTwo = 1;

        String stepTwoValueExpected = "x * 2 = (x + 1) * 3";
        String lastStepValueExpected = "x =  -3";
        crossMultiplicationExplicationExpected = "O numerador da fração antes do sinal de comparação é multiplicado pelo denominador da fração após o sinal de comparação, assim como, o numerador da fração após o sinal de comparação é multiplicado pelo denominador da fração antes do sinal de comparação. Uma informação importante a ser destacada é que este é o princípio da regra de 3.";

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
        assertEquals(stepTwo.getReason().trim(), crossMultiplicationExplicationExpected.trim());
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }
}
