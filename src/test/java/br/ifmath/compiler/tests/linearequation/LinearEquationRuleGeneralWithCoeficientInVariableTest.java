package br.ifmath.compiler.tests.linearequation;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.*;
import br.ifmath.compiler.domain.expertsystem.linearequation.LinearEquationExpertSystem;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LinearEquationRuleGeneralWithCoeficientInVariableTest {

    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String generalWithCoeficientInVariableExplicationExpected;
    private String finalResultExplicationExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new LinearEquationExpertSystem();
        generalWithCoeficientInVariableExplicationExpected = "O coeficiente da variável foi movido para o outro lado da igualdade. Isso implica em inverter a operação realiza sobre ele para uma inversamente proporcional. Neste caso, ele deixará de multiplicar a variável para dividir o termo independente.";
        finalResultExplicationExpected = "Resultado final.";
    }

    @Test()
    public void  linear_equation_should_use_general_with_coeficient_in_variable_rule_scenery_one_with_success() {
        //Arrange
        String expression = "2x = 4";

        int positionTwo = 1;

        String stepTwoValueExpected = "x = 4 / 2";
        String lastStepValueExpected = "x = 2";

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
        assertEquals(stepTwo.getReason(), generalWithCoeficientInVariableExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_use_general_with_coeficient_in_variable_rule_scenery_two_with_success()  {
        //Arrange
        String expression = "-2x = 4";
        String multipliExplicationOfOneNegative = "Multiplicação de todos os termos da equação por -1 pois a variável é negativa.";

        int positionTwo = 1;
        int positionFour = 3;

        String stepTwoValueExpected = " -2x * (-1) = 4 * (-1)";
        String stepFourValueExpected = "x =  -4 / 2";
        String lastStepValueExpected = "x =  -2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(positionTwo);
        Step stepFour = answer.getSteps().get(positionFour);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), multipliExplicationOfOneNegative);
        assertEquals(stepFour.getMathExpression(), stepFourValueExpected);
        assertEquals(stepFour.getReason(), generalWithCoeficientInVariableExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_use_general_with_coeficient_in_variable_rule_scenery_five_with_success() {
        //Arrange
        String expression = "-5 = 4x";

        int positionTwo = 1;

        String lastStepValueExpected = "x = 5 / 4";

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

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

}
