package br.ifmath.compiler.tests.polynomial.multiplication;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.AnswerType;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.multiplication.PolynomialMultiplicationExpertSystem;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PolynomialMultiplicationRuleSortSimilarTermsTest {
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
        stepTwoResultExpected = "Aplicando a propriedade distributiva, onde cada elemento dentro dos parênteses é multiplicado pelo elemento do outro termo.";
        stepThreeResultExpected = "Aplicando a propriedade distributiva, onde cada elemento do primeiro termo é multiplicado por cada um dos elementos do segundo termo.";
        stepFourResultExpected = "Multiplica-se os coeficientes, considerando a regra dos sinais, e para as variáveis, somam-se os expoentes pela propriedade das potências.";
        stepFiveResultExpected = "Agrupando os termos semelhantes.";
        finalResultExplicationExpected = "Soma dos termos semelhantes.";
    }

    @Test()
    public void sort_simple_terms_scenery_one_with_success() {
        //Arrange
        String expression = "(x^3 + 3x^4) * 5";

        String stepTwoValueExpected = "5 . x^3 + 5 . 3x^4";
        String stepThreeValueExpected = "5x^3 + 15x^4";
        String lastStepValueExpected = "15x^4 + 5x^3";


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
        assertEquals(stepFiveResultExpected, finalStep.getReason());
    }

    @Test()
    public void sort_simple_terms_scenery_two_with_success() {
        //Arrange
        String expression = "3x * (2x^2 + 8x^3 - x^3)";

        String stepTwoValueExpected = "3x . 2x^2 + 3x . 8x^3 + 3x . (-x^3)";
        String stepThreeValueExpected = "6x^3 + 24x^4 - 3x^4";
        String stepFourValueExpected = "24x^4 - 3x^4 + 6x^3";
        String lastStepValueExpected = "21x^4 + 6x^3";


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
    public void sort_simple_terms_scenery_three_with_success() {
        //Arrange
        String expression = " (-2) * (3x^2 + 4x^4)";

        String stepTwoValueExpected = "(-2) . 3x^2 + (-2) . 4x^4";
        String stepThreeValueExpected = "-6x^2 - 8x^4";
        String lastStepValueExpected = "-8x^4 - 6x^2";


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
        assertEquals(stepFiveResultExpected, finalStep.getReason());
    }


    @Test()
    public void sort_complex_terms_scenery_one_with_success() {
        //Arrange
        String expression = "(4 + x^4) * (x - 6x^3)";

        String stepTwoValueExpected = "4 . x + 4 . (-6x^3) + x^4 . x + x^4 . (-6x^3)";
        String stepThreeValueExpected = "4x - 24x^3 + x^5 - 6x^7";
        String lastStepValueExpected = "-6x^7 + x^5 - 24x^3 + 4x";

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
        assertEquals(stepThreeResultExpected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepFourResultExpected, stepThree.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFiveResultExpected, finalStep.getReason());

    }


    @Test()
    public void sort_complex_terms_scenery_two_with_success() {
        //Arrange
        String expression = "(4x^5 - x + 2x^3) * (-3x^4 - 2x^2)";

        String stepTwoValueExpected = "4x^5 . (-3x^4) + 4x^5 . (-2x^2) + (-x) . (-3x^4) + (-x) . (-2x^2)" +
                " + 2x^3 . (-3x^4) + 2x^3 . (-2x^2)";
        String stepThreeValueExpected = "-12x^9 - 8x^7 + 3x^5 + 2x^3 - 6x^7 - 4x^5";
        String stepFourValueExpected = "-12x^9 - 8x^7 - 6x^7 + 3x^5 - 4x^5 + 2x^3";
        String lastStepValueExpected = "-12x^9 - 14x^7 - x^5 + 2x^3";

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
    public void sort_complex_terms_scenery_three_with_success() {
        //Arrange
        String expression = "(2x^2 - 4x^4) * (5x^3 - 2x + 9x^4)";

        String stepTwoValueExpected = "2x^2 . 5x^3 + 2x^2 . (-2x) + 2x^2 . 9x^4 + " +
                "(-4x^4) . 5x^3 + (-4x^4) . (-2x) + (-4x^4) . 9x^4";
        String stepThreeValueExpected = "10x^5 - 4x^3 + 18x^6 - 20x^7 + 8x^5 - 36x^8";
        String stepFourValueExpected = "-36x^8 - 20x^7 + 18x^6 + 8x^5 + 10x^5 - 4x^3";
        String lastStepValueExpected = "-36x^8 - 20x^7 + 18x^6 + 18x^5 - 4x^3";

        // Act)
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
