package br.ifmath.compiler.tests.polynomial;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.AnswerType;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.PolynomialExpertSystem;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PolynomialRuleSumNumbersTest {

    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String stepTwoExplicationExpected;
    private String finalResultExplicationExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new PolynomialExpertSystem();
        stepTwoExplicationExpected = "Substituindo os valores nas variáveis correspondentes.";
        finalResultExplicationExpected = "Somando os numeros.";
    }

    @Test()
    public void sum_numbers_scenery_one_with_success()  {
        //Arrange
        String expression = "x = y + 5 + 6";

        int positionTwo = 1;

        String stepTwoValueExpected = "x = 4+5+6";
        String lastStepValueExpected = "x = 15";

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
        assertEquals(stepTwo.getReason(), stepTwoExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }
}
