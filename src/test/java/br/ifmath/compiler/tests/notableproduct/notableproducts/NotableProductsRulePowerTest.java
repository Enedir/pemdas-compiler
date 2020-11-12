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

public class NotableProductsRulePowerTest {
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
    private String finalStepResultExpected;


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
        finalStepResultExpected = "Somando os números restantes.";
    }

    @Test()
    public void two_terms_square_power_scenery_one_with_success() {
        //Arrange
        String expression = "(3 + y) ^ 2";

        String stepTwoValueExpected = "(3  ) ^ 2 + 2 * 3 * y + (y  ) ^ 2";
        String stepThreeValueExpected = "9 + 2 * 3 * y + y^2";
        String stepFourValueExpected = "9 + 6y + y^2";
        String finalStepValueExpected = "y^2 + 6y + 9";


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
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult1Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
        assertEquals(stepFourValueExpected, stepFour.getMathExpression());
        assertEquals(stepFourResultExpected, stepFour.getReason());
        assertEquals(finalStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFiveResultExpected, finalStep.getReason());
    }

    @Test()
    public void two_terms_square_power_scenery_two_with_success() {
        //Arrange
        String expression = "(5 - 2) ^ 2";

        String stepTwoValueExpected = "(5  ) ^ 2 - 2 * 5 * 2 + (2  ) ^ 2";
        String stepThreeValueExpected = "25 - 2 * 5 * 2 + 4";
        String stepFourValueExpected = "25 - 20 + 4";
        String finalStepValueExpected = "9";


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
        assertEquals(stepOneResult2Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult2Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
        assertEquals(stepFourValueExpected, stepFour.getMathExpression());
        assertEquals(stepFourResultExpected, stepFour.getReason());
        assertEquals(finalStepValueExpected, finalStep.getMathExpression());
        assertEquals(finalStepResultExpected, finalStep.getReason());
    }

    @Test()
    public void two_terms_square_power_scenery_three_with_success() {
        //Arrange
        String expression = "(k + 4) ^ 2";

        String stepTwoValueExpected = "(k  ) ^ 2 + 2 * k * 4 + (4  ) ^ 2";
        String stepThreeValueExpected = "k^2 + 2 * k * 4 + 16";
        String finalStepValueExpected = "k^2 + 8k + 16";


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
        assertEquals(stepThreeResultExpected, stepThree.getReason());
        assertEquals(finalStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFourResultExpected, finalStep.getReason());
    }

    @Test()
    public void two_terms_square_power_scenery_four_with_success() {
        //Arrange
        String expression = "(-7 + x) ^ 2";

        String stepTwoValueExpected = "(-7) ^ 2 + 2 * (-7) * x + (x  ) ^ 2";
        String stepThreeValueExpected = "49 + 2 * (-7) * x + x^2";
        String stepFourValueExpected = "49 - 14x + x^2";
        String finalStepValueExpected = "x^2 - 14x + 49";


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
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult1Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
        assertEquals(stepFourValueExpected, stepFour.getMathExpression());
        assertEquals(stepFourResultExpected, stepFour.getReason());
        assertEquals(finalStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFiveResultExpected, finalStep.getReason());
    }

    @Test()
    public void sum_and_dif_power_scenery_one_with_success() {
        //Arrange
        String expression = "(q + 2) * (q - 2)";

        String stepTwoValueExpected = "(q  ) ^ 2 - (2  ) ^ 2";
        String finalStepValueExpected = "q^2 - 4";

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
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);


        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult3Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult3Expected, stepTwo.getReason());
        assertEquals(finalStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResultExpected, finalStep.getReason());

    }

    @Test()
    public void sum_and_dif_power_scenery_two_with_success() {
        //Arrange
        String expression = "(1 - 7) * (1 + 7)";

        String stepTwoValueExpected = "(1  ) ^ 2 - (7  ) ^ 2";
        String stepThreeValueExpected = "1 - 49";
        String finalStepValueExpected = "-48";

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
        assertEquals(stepOneResult3Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult3Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
        assertEquals(finalStepValueExpected, finalStep.getMathExpression());
        assertEquals(finalStepResultExpected, finalStep.getReason());
    }

    @Test()
    public void two_terms_cube_power_scenery_one_with_success() {
        //Arrange
        String expression = "(l - 3) ^ 3";

        String stepTwoValueExpected = "(l  ) ^ 3 - 3 * (l  ) ^ 2 * 3 + 3 * l * (3  ) ^ 2 - (3  ) ^ 3";
        String stepThreeValueExpected = "l^3 - 3 * l^2 * 3 + 3 * l * 9 - 27";
        String finalStepValueExpected = "l^3 - 9l^2 + 27l - 27";

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
        assertEquals(stepThreeResultExpected, stepThree.getReason());
        assertEquals(finalStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFourResultExpected, finalStep.getReason());
    }

    @Test()
    public void two_terms_cube_power_scenery_two_with_success() {
        //Arrange
        String expression = "(2 + 4) ^ 3";

        String stepTwoValueExpected = "(2  ) ^ 3 + 3 * (2  ) ^ 2 * 4 + 3 * 2 * (4  ) ^ 2 + (4  ) ^ 3";
        String stepThreeValueExpected = "8 + 3 * 4 * 4 + 3 * 2 * 16 + 64";
        String stepFourValueExpected = "8 + 48 + 96 + 64";
        String finalStepValueExpected = "216";

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
        assertEquals(stepOneResult4Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult4Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
        assertEquals(stepFourValueExpected, stepFour.getMathExpression());
        assertEquals(stepFourResultExpected, stepFour.getReason());
        assertEquals(finalStepValueExpected, finalStep.getMathExpression());
        assertEquals(finalStepResultExpected, finalStep.getReason());
    }

    @Test()
    public void two_terms_cube_power_scenery_three_with_success() {
        //Arrange
        String expression = "(o + x) ^ 3";

        String stepTwoValueExpected = "(o  ) ^ 3 + 3 * (o  ) ^ 2 * x + 3 * o * (x  ) ^ 2 + (x  ) ^ 3";
        String stepThreeValueExpected = "o^3 + 3 * o^2 * x + 3 * o * x^2 + x^3";
        String stepFourValueExpected = "o^3 + 3o^2x + 3ox^2 + x^3";
        String finalStepValueExpected = "o^3 + x^3 + 3ox^2 + 3o^2x";

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
        assertEquals(stepOneResult4Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult4Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
        assertEquals(stepFourValueExpected, stepFour.getMathExpression());
        assertEquals(stepFourResultExpected, stepFour.getReason());
        assertEquals(finalStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFiveResultExpected, finalStep.getReason());

    }

    @Test()
    public void two_terms_cube_power_scenery_four_with_success() {
        //Arrange
        String expression = "(-x + 3) ^ 3";

        String stepTwoValueExpected = "(-x) ^ 3 + 3 * (-x) ^ 2 * 3 + 3 * (-x) * (3  ) ^ 2 + (3  ) ^ 3";
        String stepThreeValueExpected = "(-x^3) + 3 * x^2 * 3 + 3 * (-x) * 9 + 27";
        String finalStepValueExpected = "(-x^3) + 9x^2 - 27x + 27";

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
        assertEquals(stepOneResult4Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult4Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
        assertEquals(finalStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFourResultExpected, finalStep.getReason());

    }

}
