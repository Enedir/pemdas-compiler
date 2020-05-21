package br.ifmath.compiler.tests.polynomial.multiplication;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.*;
import br.ifmath.compiler.domain.expertsystem.polynomial.multiplication.PolynomialMultiplicationExpertSystem;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PolynomialMultiplicationRuleGroupSimilarTermsTest {
    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String finalResultExplicationExpected;
    private String stepTwoResultExpected;
    private String stepThreeResultExpected;
    private String stepFourResultExpected;
    private String stepFiveResultExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new PolynomialMultiplicationExpertSystem();
        stepTwoResultExpected = "Aplicando a propriedade distributiva, onde cada elemento dentro dos parênteses é multiplicado pelo elemento do outro termo";
        stepThreeResultExpected = "Aplicando a propriedade distributiva, onde cada elemento do primeiro termo é multiplicado por cada um dos elementos do segundo termo.";
        stepFourResultExpected = "Multiplica-se os coeficientes, considerando a regra dos sinais, e para as variáveis, somam-se os expoentes pela propriedade das potências.";
        stepFiveResultExpected = "Agrupando os termos semelhantes.";
        finalResultExplicationExpected = "Soma dos termos semelhantes.";
    }

    @Test()
    public void group_simple_terms_scenery_one_with_success() {
        //Arrange
        String expression = "2x * (5x^2 - 3x^2)";

        String stepTwoValueExpected = "2x * 5x^2 + 2x * (-3x^2)";
        String stepThreeValueExpected = "10x^3 - 6x^3";
        String lastStepValueExpected = "4x^3";


        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResultExpected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepFourResultExpected, stepThree.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void group_simple_terms_scenery_two_with_success() {
        //Arrange
        String expression = "(8x^3 - 3x + 2x^3) * -4x^2";

        String stepTwoValueExpected = "(-4x^2) * 8x^3 + (-4x^2) * (-3x) + (-4x^2) * 2x^3";
        String stepThreeValueExpected = "-32x^5 + 12x^3 - 8x^5";
        String stepFourValueExpected = "-32x^5 - 8x^5 + 12x^3";
        String lastStepValueExpected = "-40x^5 + 12x^3";


        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 4);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepFour = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResultExpected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepFourResultExpected, stepThree.getReason());
        assertEquals(stepFourValueExpected, stepFour.getMathExpression());
        assertEquals(stepFiveResultExpected, stepFour.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void group_simple_terms_scenery_three_with_success() {
        //Arrange
        String expression = "3x^2 * (-x^2 - 4x^2 + 2x^2)";

        String stepTwoValueExpected = "3x^2 * (-x^2) + 3x^2 * (-4x^2) + 3x^2 * 2x^2";
        String stepThreeValueExpected = "-3x^4 - 12x^4 + 6x^4";
        String lastStepValueExpected = "-9x^4";


        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResultExpected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepFourResultExpected, stepThree.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void group_complex_terms_scenery_one_with_success() {
        //Arrange
        String expression = "(-5x^2 + 3x) * (-2x + 4x^2)";

        String stepTwoValueExpected = "(-5x^2) * (-2x) + (-5x^2) * 4x^2 + 3x * (-2x) + 3x * 4x^2";
        String stepThreeValueExpected = "10x^3 - 20x^4 - 6x^2 + 12x^3";
        String stepFourValueExpected = "-20x^4 + 10x^3 + 12x^3 - 6x^2";
        String lastStepValueExpected = "-20x^4 + 22x^3 - 6x^2";


        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 4);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepFour = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepThreeResultExpected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepFourResultExpected, stepThree.getReason());
        assertEquals(stepFourValueExpected, stepFour.getMathExpression());
        assertEquals(stepFiveResultExpected, stepFour.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void group_complex_terms_scenery_two_with_success() {
        //Arrange
        String expression = "(3y^2 + 1) * (-6 + 4y^2)";

        String stepTwoValueExpected = "3y^2 * (-6) + 3y^2 * 4y^2 + 1 * (-6) + 1 * 4y^2";
        String stepThreeValueExpected = "-18y^2 + 12y^4 - 6 + 4y^2";
        String stepFourValueExpected = "12y^4 - 18y^2 + 4y^2 - 6";
        String lastStepValueExpected = "12y^4 - 14y^2 - 6";


        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 4);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepFour = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepThreeResultExpected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepFourResultExpected, stepThree.getReason());
        assertEquals(stepFourValueExpected, stepFour.getMathExpression());
        assertEquals(stepFiveResultExpected, stepFour.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

    @Test()
    public void group_complex_terms_scenery_three_with_success() {
        //Arrange
        String expression = "(2a^2 - 7) * (-3a + 4a^3 - a^2)";

        String stepTwoValueExpected = "2a^2 * (-3a) + 2a^2 * 4a^3 + 2a^2 * (-a^2) + " +
                "(-7) * (-3a) + (-7) * 4a^3 + (-7) * (-a^2)";
        String stepThreeValueExpected = "-6a^3 + 8a^5 - 2a^4 + 21a - 28a^3 + 7a^2";
        String stepFourValueExpected = "8a^5 - 2a^4 - 6a^3 - 28a^3 + 7a^2 + 21a";
        String lastStepValueExpected = "8a^5 - 2a^4 - 34a^3 + 7a^2 + 21a";


        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 4);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepFour = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepThreeResultExpected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepFourResultExpected, stepThree.getReason());
        assertEquals(stepFourValueExpected, stepFour.getMathExpression());
        assertEquals(stepFiveResultExpected, stepFour.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }


}
