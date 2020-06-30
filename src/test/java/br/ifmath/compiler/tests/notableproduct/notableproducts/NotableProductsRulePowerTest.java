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
//    private String stepFourResultExpected;
//    private String finalResultExplicationExpected;


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
//        stepFourResultExpected = "";
//        finalResultExplicationExpected = "";


    }

    @Test()
    public void two_terms_square_power_scenery_one_with_success() {
        //Arrange
        String expression = "(3 + y) ^ 2";

        String stepTwoValueExpected = "3^2 + 2 * 3 * y + y^2";
        String stepThreeValueExpected = "9 + 2 * 3 * y + y^2";

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
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult1Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
    }

    @Test()
    public void two_terms_square_power_scenery_two_with_success() {
        //Arrange
        String expression = "(5 - 2) ^ 2";

        String stepTwoValueExpected = "5^2 - 2 * 5 * 2 + 2^2";
        String stepThreeValueExpected = "25 - 2 * 5 * 2 + 4";

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
        assertEquals(stepOneResult2Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult2Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
    }

    @Test()
    public void two_terms_square_power_scenery_three_with_success() {
        //Arrange
        String expression = "(k + 4) ^ 2";

        String stepTwoValueExpected = "k^2 + 2 * k * 4 + 4^2";
        String stepThreeValueExpected = "k^2 + 2 * k * 4 + 16";

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
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult1Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
    }

    @Test()
    public void sum_and_dif_power_scenery_one_with_success() {
        //Arrange
        String expression = "(q + 2) * (q - 2)";

        String stepTwoValueExpected = "q^2 - 2^2";
        String stepThreeValueExpected = "q^2 - 4";

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
    public void sum_and_dif_power_scenery_two_with_success() {
        //Arrange
        String expression = "(1 - 7) * (1 + 7)";

        String stepTwoValueExpected = "1^2 - 7^2";
        String stepThreeValueExpected = "1 - 49";

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
    public void two_terms_cube_power_scenery_one_with_success() {
        //Arrange
        String expression = "(l - 3) ^ 3";

        String stepTwoValueExpected = "l^3 - 3 * l^2 * 3 + 3 * l * 3^2 - 3^3";
        String stepThreeValueExpected = "l^3 - 3 * l^2 * 3 + 3 * l * 9 - 27";

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
        assertEquals(stepOneResult5Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult5Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
    }

    @Test()
    public void two_terms_cube_power_scenery_two_with_success() {
        //Arrange
        String expression = "(2 + 4) ^ 3";

        String stepTwoValueExpected = "2^3 + 3 * 2^2 * 4 + 3 * 2 * 4^2 + 4^3";
        String stepThreeValueExpected = "8 + 3 * 4 * 4 + 3 * 2 * 16 + 64";

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
        assertEquals(stepOneResult4Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult4Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
    }

    @Test()
    public void two_terms_cube_power_scenery_three_with_success() {
        //Arrange
        String expression = "(5 + x) ^ 3";

        String stepTwoValueExpected = "5^3 + 3 * 5^2 * x + 3 * 5 * x^2 + x^3";
        String stepThreeValueExpected = "125 + 3 * 25 * x + 3 * 5 * x^2 + x^3";

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
        assertEquals(stepOneResult4Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult4Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepThreeResultExpected, stepThree.getReason());
    }

}
