package br.ifmath.compiler.tests.polynomial.addAndSub;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.*;
import br.ifmath.compiler.domain.expertsystem.polynomial.addandsub.PolynomialAddAndSubExpertSystem;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class PolynomialAddAndSubRuleRemoveParenthesisTest {

    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String finalResultExplicationExpected;
    private String stepTwoExplicationExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new PolynomialAddAndSubExpertSystem();
        finalResultExplicationExpected = "Aplicação da regra de troca de sinais em operações prioritárias, em duplas negações ou em somas de números negativos.";

    }

    @Test
    public void remove_parenthesis_simple_scenery_one_with_success(){
        //Arrange
        String expression = "(2x + 5y) + (-4z - 2z)";
        String lastStepValueExpected = "2x + 5y - 4z - 2z";

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

    @Test
    public void remove_parenthesis_with_multiple_levels_scenery_one_with_success(){
        //Arrange
        String expression = "(2x + 5z) + ((-4a + (2y - 4x + 5z)))";
        String lastStepValueExpected = "2x + 5z - 4a + 2y - 4x + 5z";

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

    @Test
    public void remove_parenthesis_with_multiple_levels_scenery_two_with_success(){
        //Arrange
        String expression = "(-2z + 5a) + ((-4x + (2x - (4y + 5a))))";
        String lastStepValueExpected = "-2z + 5a - 4x + 2x - 4y + 5a";

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
