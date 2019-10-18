package br.ifmath.compiler.tests.polynomial;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.*;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.domain.expertsystem.polynomial.numericvalue.PolynomialNumericValueExpertSystem;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PolynomialRuleSubstituteVariableTest {

    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String finalResultExplicationExpected;
    private String stepTwoResultExpected;
    private List<NumericValueVariable> userInput = new ArrayList<>();


    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new PolynomialNumericValueExpertSystem();
        stepTwoResultExpected = "Substituindo os valores nas variáveis correspondentes.";
        finalResultExplicationExpected = "Somando os valores.";

        userInput.add(new NumericValueVariable("a", 777));
        userInput.add(new NumericValueVariable("x", 3));
        userInput.add(new NumericValueVariable("z", 4));
    }

    @Test()
    public void switch_variable_y_to_3_scenery_one_with_success() {
        //Arrange
        String expression = "y + 3";

        String stepTwoValueExpected = "3 + 3";
        String lastStepValueExpected = "6";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyseNumeric(expertSystem, AnswerType.BEST, userInput, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), stepTwoResultExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void switch_multiple_variable_scenery_one_with_success() {
        //Arrange
        String expression = "y + 5 + 10 + y - z+a";

        String stepTwoValueExpected = "3 + 5 + 10 + 3 - 4 + 777";
        String lastStepValueExpected = "794";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyseNumeric(expertSystem, AnswerType.BEST, userInput, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), stepTwoResultExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }


}
