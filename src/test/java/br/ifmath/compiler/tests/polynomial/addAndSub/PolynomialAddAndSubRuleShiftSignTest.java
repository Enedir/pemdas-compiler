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

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new PolynomialAddAndSubExpertSystem();
        finalResultExplicationExpected = "Aplicação da regra de troca de sinais em operações prioritárias, em duplas negações ou em somas de números negativos.";

    }

    @Test()
    public void shift_sign_simple_numbers_scenery_one_with_success() {
        //Arrange
        String expression = "(2 + 5) - (4 - 2)";
        String lastStepValueExpected = "(2 + 5) + (-4 + 2)";

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
    public void shift_sign_simple_variables_scenery_one_with_success() {
        //Arrange
        String expression = "(2x + 5) - (-4y - (2y + 4))";
        String lastStepValueExpected = "(2x + 5) + (4y + (-2y - 4))";

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
    public void shift_sign_many_values_scenery_one_with_success() {
        //Arrange
        String expression = "(2x + 5) - (((4y + 3) - x) - (-2y + 4))";
        String lastStepValueExpected = "(2x + 5) + ((( -4y - 3) + x) + (2y - 4))";

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
    public void shift_sign_numbers_and_variables_scenery_one_with_success() {
        //Arrange
        String expression = "(2 + 2x) - ((-4x + 2) - 7)";
        String lastStepValueExpected = "(2 + 2x) - ((x + 7))";

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
