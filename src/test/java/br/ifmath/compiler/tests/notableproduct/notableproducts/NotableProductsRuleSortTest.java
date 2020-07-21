package br.ifmath.compiler.tests.notableproduct.notableproducts;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.AnswerType;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.notableproducts.NotableProductsExpertSystem;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class NotableProductsRuleSortTest {
    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String stepOneResult1Expected;
    private String stepOneResult2Expected;
    private String stepOneResult3Expected;
    private String stepOneResult4Expected;
    private String stepOneResult5Expected;
    private String stepTwoResult1Expected;
    private String stepTwoResult2Expected;
    private String stepTwoResult3Expected;
    private String stepTwoResult4Expected;
    private String stepTwoResult5Expected;
    private String stepThreeResultExpected;
    private String stepFourResultExpected;
    private String stepFiveResultExpected;


    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new NotableProductsExpertSystem();
        String stepOneBaseResult = "Identificação do tipo de produto notável a partir da equação inicial: ";
        stepOneResult1Expected = stepOneBaseResult + "Quadrado da soma de dois termos.";
        stepOneResult2Expected = stepOneBaseResult + "Quadrado da diferença de dois termos.";
        stepOneResult3Expected = stepOneBaseResult + "Produto da soma pela diferença de dois termos.";
        stepOneResult4Expected = stepOneBaseResult + "Cubo da soma de dois termos.";
        stepOneResult5Expected = stepOneBaseResult + "Cubo da diferença de dois termos.";

        String stepTwoBaseResult = "Aplicando o produto notável fazendo o ";

        stepTwoResult1Expected = stepTwoBaseResult + "quadrado do primeiro termo, mais o dobro do produto " +
                "do primeiro pelo segundo termo, mais o quadrado do segundo termo.";
        stepTwoResult2Expected = stepTwoBaseResult + "quadrado do primeiro termo, menos o dobro do " +
                "produto do primeiro pelo segundo termo, mais o quadrado do segundo termo.";
        stepTwoResult3Expected = stepTwoBaseResult + "quadrado do primeiro termo, menos o quadrado do " +
                "segundo termo.";
        stepTwoResult4Expected = stepTwoBaseResult + "cubo do primeiro termo, mais o triplo do produto do " +
                "quadrado do primeiro termo pelo segundo termo, mais o triplo do produto do primeiro pelo " +
                "quadrado do segundo termo, mais o cubo do segundo termo.";
        stepTwoResult5Expected = stepTwoBaseResult + "cubo do primeiro termo, menos o triplo do produto do " +
                "quadrado do primeiro termo pelo segundo termo, mais o triplo do produto do primeiro pelo " +
                "quadrado do segundo termo, menos o cubo do segundo termo.";

        stepThreeResultExpected = "Resolvendo as operações de potência.";
        stepFourResultExpected = "Resolvendo as multiplicações.";
        stepFiveResultExpected = "Ou ainda, colocando em ordem decrescente.";
    }

    @Test()
    public void sort_two_terms_square_scenery_one_with_success() {
        //Arrange
        String expression = "(5 + a) ^ 2";

        String stepTwoValueExpected = "5^2 + 2 * 5 * a + a^2";
        String stepThreeValueExpected = "25 + 2 * 5 * a + a^2";
        String stepFourValueExpected = "25 + 10a + a^2";
        String stepFiveValueExpected = "a^2 + 10a + 25";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 5);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 4);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepFour = answer.getSteps().get(answer.getSteps().size() - 2);
        Step stepFive = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult1Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
        assertEquals(stepFourValueExpected, stepFour.getMathExpression());
        assertEquals(stepFourResultExpected, stepFour.getReason());
        assertEquals(stepFiveValueExpected, stepFive.getMathExpression());
        assertEquals(stepFiveResultExpected, stepFive.getReason());
    }

    @Test()
    public void sort_two_terms_square_scenery_two_with_success() {
        //Arrange
        String expression = "(2 - g) ^ 2";

        String stepTwoValueExpected = "2^2 - 2 * 2 * g + g^2";
        String stepThreeValueExpected = "4 - 2 * 2 * g + g^2";
        String stepFourValueExpected = "4 - 4g + g^2";
        String stepFiveValueExpected = "g^2 - 4g + 4";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 5);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 4);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepFour = answer.getSteps().get(answer.getSteps().size() - 2);
        Step stepFive = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult2Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult2Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
        assertEquals(stepFourValueExpected, stepFour.getMathExpression());
        assertEquals(stepFourResultExpected, stepFour.getReason());
        assertEquals(stepFiveValueExpected, stepFive.getMathExpression());
        assertEquals(stepFiveResultExpected, stepFive.getReason());
    }

    @Test()
    public void sort_two_terms_square_scenery_three_with_success() {
        //Arrange
        String expression = "(3b + 8a) ^ 2";

        String stepTwoValueExpected = "( 3b ) ^ 2 + 2 * 3b * 8a + ( 8a ) ^ 2";
        String stepThreeValueExpected = "9b^2 + 2 * 3b * 8a + 64a^2";
        String stepFourValueExpected = "9b^2 + 48ab + 64a^2";
        String stepFiveValueExpected = "9b^2 + 64a^2 + 48ab";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 5);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 4);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepFour = answer.getSteps().get(answer.getSteps().size() - 2);
        Step stepFive = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult1Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
        assertEquals(stepFourValueExpected, stepFour.getMathExpression());
        assertEquals(stepFourResultExpected, stepFour.getReason());
        assertEquals(stepFiveValueExpected, stepFive.getMathExpression());
        assertEquals(stepFiveResultExpected, stepFive.getReason());
    }

    @Test()
    public void sort_two_terms_square_scenery_four_with_success() {
        //Arrange
        String expression = "(-2g + 5a) ^ 2";

        String stepTwoValueExpected = "(-2g) ^ 2 + 2 * (-2g) * 5a + ( 5a ) ^ 2";
        String stepThreeValueExpected = "4g^2 + 2 * (-2g) * 5a + 25a^2";
        String stepFourValueExpected = "4g^2 - 20ag + 25a^2";
        String stepFiveValueExpected = "4g^2 + 25a^2 - 20ag";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 5);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 4);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepFour = answer.getSteps().get(answer.getSteps().size() - 2);
        Step stepFive = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult1Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
        assertEquals(stepFourValueExpected, stepFour.getMathExpression());
        assertEquals(stepFourResultExpected, stepFour.getReason());
        assertEquals(stepFiveValueExpected, stepFive.getMathExpression());
        assertEquals(stepFiveResultExpected, stepFive.getReason());
    }

    @Test()
    public void sort_two_terms_square_scenery_five_with_success() {
        //Arrange
        String expression = "(e^2 + f^2) ^ 2";

        String stepTwoValueExpected = "e^4 + 2 * e^2 * f^2 + f^4";
        String stepThreeValueExpected = "e^4 + 2e^2f^2 + f^4";
        String finalStepValueExpected = "e^4 + f^4 + 2e^2f^2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 4);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult1Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepFourResultExpected, stepThree.getReason());
        assertEquals(finalStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFiveResultExpected, finalStep.getReason());
    }

    @Test()
    public void sum_and_dif_sort_scenery_one_with_success() {
        //Arrange
        String expression = "(6 - n) * (6 + n)";

        String stepTwoValueExpected = "6^2 - n^2";
        String stepThreeValueExpected = "36 - n^2";
        String stepFourValueExpected = "-n^2 + 36";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 4);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 2);
        Step stepFour = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult3Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult3Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
        assertEquals(stepFourValueExpected, stepFour.getMathExpression());
        assertEquals(stepFiveResultExpected, stepFour.getReason());
    }

    @Test()
    public void sum_and_dif_sort_scenery_two_with_success() {
        //Arrange
        String expression = "(4 + u) * (4 - u)";

        String stepTwoValueExpected = "4^2 - u^2";
        String stepThreeValueExpected = "16 - u^2";
        String stepFourValueExpected = "-u^2 + 16";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 4);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 2);
        Step stepFour = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult3Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult3Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
        assertEquals(stepFourValueExpected, stepFour.getMathExpression());
        assertEquals(stepFiveResultExpected, stepFour.getReason());
    }

    @Test()
    public void sum_and_dif_sort_scenery_three_with_success() {
        //Arrange
        String expression = "(-4s + 2u) * (-4s - 2u)";

        String stepTwoValueExpected = "(-4s) ^ 2 + (-2u) ^ 2";
        String stepThreeValueExpected = "16s^2 + 4u^2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult3Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult3Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
    }

    @Test()
    public void sum_and_dif_sort_scenery_four_with_success() {
        //Arrange
        String expression = "(s^2 + u^2) * (s^2 - u^2)";

        String stepTwoValueExpected = "s^4 - u^4";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 2);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult3Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult3Expected, stepTwo.getReason());
    }

    @Test()
    public void sum_and_dif_sort_scenery_five_with_success() {
        //Arrange
        String expression = "(2x + 3y) * (2x - 3y)";

        String stepTwoValueExpected = "( 2x ) ^ 2 - ( 3y ) ^ 2";
        String stepThreeValueExpected = "4x^2 - 9y^2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult3Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult3Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
    }

    @Test()
    public void sort_two_terms_cube_scenery_one_with_success() {
        //Arrange
        String expression = "(2 + p) ^ 3";

        String stepTwoValueExpected = "2^3 + 3 * 2^2 * p + 3 * 2 * p^2 + p^3";
        String stepThreeValueExpected = "8 + 3 * 4 * p + 3 * 2 * p^2 + p^3";
        String stepFourValueExpected = "8 + 12p + 6p^2 + p^3";
        String stepFiveValueExpected = "p^3 + 6p^2 + 12p + 8";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 5);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 4);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepFour = answer.getSteps().get(answer.getSteps().size() - 2);
        Step stepFive = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult4Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult4Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
        assertEquals(stepFourValueExpected, stepFour.getMathExpression());
        assertEquals(stepFourResultExpected, stepFour.getReason());
        assertEquals(stepFiveValueExpected, stepFive.getMathExpression());
        assertEquals(stepFiveResultExpected, stepFive.getReason());
    }

    @Test()
    public void sort_two_terms_cube_scenery_two_with_success() {
        //Arrange
        String expression = "(k - l) ^ 3";

        String stepTwoValueExpected = "k^3 - 3 * k^2 * l + 3 * k * l^2 - l^3";
        String stepThreeValueExpected = "k^3 - 3k^2l + 3kl^2 - l^3";
        String stepFourValueExpected = "k^3 - l^3 + 3kl^2 - 3k^2l";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 4);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 2);
        Step stepFour = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult5Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult5Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepFourResultExpected, stepThree.getReason());
        assertEquals(stepFourValueExpected, stepFour.getMathExpression());
        assertEquals(stepFiveResultExpected, stepFour.getReason());
    }

    @Test()
    public void sort_two_terms_cube_scenery_three_with_success() {
        //Arrange
        String expression = "(-2s - 3y) ^ 3";

        String stepTwoValueExpected = "(-2s) ^ 3 - 3 * (-2s) ^ 2 * 3y + 3 * (-2s) * ( 3y ) ^ 2 - ( 3y ) ^ 3";
        String stepThreeValueExpected = "-8s^3 - 3 * 4s^2 * 3y + 3 * (-2s) * 9y^2 - 27y^3";
        String stepFourValueExpected = "-8s^3 - 36s^2y - 54sy^2 - 27y^3";
        String finalStepValueExpected = "-8s^3 - 27y^3 - 54sy^2 - 36s^2y";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 5);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 4);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepFour = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult5Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult5Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
        assertEquals(stepFourValueExpected, stepFour.getMathExpression());
        assertEquals(stepFourResultExpected, stepFour.getReason());
        assertEquals(finalStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFiveResultExpected, finalStep.getReason());
    }

    @Test()
    public void sort_two_terms_cube_scenery_four_with_success() {
        //Arrange
        String expression = "(h^2 - j^2) ^ 3";

        String stepTwoValueExpected = "h^5 - 3 * h^4 * j^2 + 3 * h^2 * j^4 - j^5";
        String stepThreeValueExpected = "h^5 - 3h^4j^2 + 3h^2j^4 - j^5";
        String finalStepValueExpected = "h^5 - j^5 + 3h^2j^4 - 3h^4j^2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 4);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepThree = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult5Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult5Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepFourResultExpected, stepThree.getReason());
        assertEquals(finalStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFiveResultExpected, finalStep.getReason());
    }
}
