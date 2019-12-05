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

public class PolynomialAddAndSubRuleGroupSimilarTermsTest {
    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String finalResultExplicationExpected;
    private String stepTwoResultExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new PolynomialAddAndSubExpertSystem();
        finalResultExplicationExpected = "Soma dos termos semelhantes.";

    }

    @Test()
    public void group_terms_with_three_variables_test() {
        //Arrange
        String expression = "x + 2x + 7 - 8y - 5 + 12x + 2y - z";

        String lastStepValueExpected = "15x - 6y - z + 2";

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

        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void group_only_numbers_test() {
        //Arrange
        String expression = "7 + 7 + 18 - 18";

        String lastStepValueExpected = "14";

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

        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void group_only_variables_test() {
        //Arrange
        String expression = "x + x + z + x + y";

        String lastStepValueExpected = "3x + z + y";

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

        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void group_variables_and_numbers_test() {
        //Arrange
        String expression = "x + x + z + x + y - 7 + 7 + 18=x - 18";

        String lastStepValueExpected = "21x + z + y - 18";

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

        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }


}
