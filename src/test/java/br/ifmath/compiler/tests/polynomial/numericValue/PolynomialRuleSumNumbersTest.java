package br.ifmath.compiler.tests.polynomial.numericValue;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.AnswerType;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.Monomial;
import br.ifmath.compiler.domain.expertsystem.polynomial.numericvalue.PolynomialNumericValueExpertSystem;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PolynomialRuleSumNumbersTest {

    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String stepTwoExplicationExpected;
    private String finalResultExplicationExpected;
    private List<Monomial> userInput = new ArrayList<>();


    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new PolynomialNumericValueExpertSystem();
        stepTwoExplicationExpected = "Substituindo os valores nas variáveis correspondentes.";
        finalResultExplicationExpected = "Somando os valores.";

        userInput.add(new Monomial("a", 777));
        userInput.add(new Monomial("y", 3));
        userInput.add(new Monomial("z", 4));
    }

    @Test()
    public void sum_numbers_scenery_one_with_success() {
        //Arrange
        String expression = "y - 5 + 6";

        int positionTwo = 1;

        String stepTwoValueExpected = "3 - 5 + 6";
        String lastStepValueExpected = "4";

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
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }
}
