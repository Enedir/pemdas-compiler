package br.ifmath.compiler.tests.polynomial;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.*;
import br.ifmath.compiler.domain.expertsystem.linearequation.LinearEquationExpertSystem;
import br.ifmath.compiler.domain.expertsystem.polynomial.PolynomialExpertSystem;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class PolynomialRuleSubstituteVariableTest {

    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String finalResultExplicationExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new PolynomialExpertSystem();
        finalResultExplicationExpected = "Substituindo os valores nas variáveis correspondentes.";
    }

    @Test()
    public void switch_variable_y_to_3_scenery_one_with_success() {
        //Arrange
        String expression = "x = y + 3";

        int positionTwo = 4;

        String lastStepValueExpected = "x = 3+3";

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
    public void switch_multiple_variable_scenery_one_with_success() {
        //Arrange
        String expression = "x = y + 5 + 10 + y - z+a";

        int positionTwo = 4;

        String lastStepValueExpected = "x = 3+5+10+3-4+777";

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


}
