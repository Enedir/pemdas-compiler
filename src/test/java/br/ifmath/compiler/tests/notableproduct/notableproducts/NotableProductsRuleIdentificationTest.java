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

public class NotableProductsRuleIdentificationTest {
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
    public void identify_two_terms_square_scenery_one_with_success() {
        //Arrange
        String expression = "(x + 2) ^ 2";

        String stepTwoValueExpected = "x^2 + 2 * x * 2 + 2^2";
        String stepThreeValueExpected = "x^2 + 2 * x * 2 + 4";
        String finalStepValueExpected = "x^2 + 4x + 4";


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
    public void identify_two_terms_square_scenery_two_with_success() {
        //Arrange
        String expression = "(6 - 2) ^ 2";

        String stepTwoValueExpected = "6^2 - 2 * 6 * 2 + 2^2";
        String stepThreeValueExpected = "36 - 2 * 6 * 2 + 4";
        String stepFourValueExpected = "36 - 24 + 4";
        String finalStepValueExpected = "16";


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
    public void identify_two_terms_square_scenery_three_with_success() {
        //Arrange
        String expression = "(x + y) ^ 2";

        String stepTwoValueExpected = "x^2 + 2 * x * y + y^2";
        String stepThreeValueExpected = "x^2 + 2xy + y^2";
        String finalStepValueExpected = "x^2 + y^2 + 2xy";

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
    public void identify_two_terms_square_scenery_four_with_success() {
        //Arrange
        String expression = "(2x + y) ^ 2";

        String stepTwoValueExpected = "( 2x ) ^ 2 + 2 * 2x * y + y^2";
        String stepThreeValueExpected = "4x^2 + 2 * 2x * y + y^2";
        String stepFourValueExpected = "4x^2 + 4xy + y^2";
        String finalStepValueExpected = "4x^2 + y^2 + 4xy";

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
    public void identify_sum_and_dif_product_scenery_one_with_success() {
        //Arrange
        String expression = "(2 + y) * (2 - y)";

        String stepTwoValueExpected = "2^2 - y^2";
        String stepThreeValueExpected = "4 - y^2";
        String finalStepValueExpected = "-y^2 + 4";

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
        assertEquals(stepFiveResultExpected, finalStep.getReason());
    }

    @Test()
    public void identify_sum_and_dif_product_scenery_two_with_success() {
        //Arrange
        String expression = "(3 + 1) * (3 - 1)";

        String stepTwoValueExpected = "3^2 - 1^2";
        String stepThreeValueExpected = "9 - 1";
        String finalStepValueExpected = "8";

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
    public void identify_sum_and_dif_product_scenery_three_with_success() {
        //Arrange
        String expression = "(z + x) * (z - x)";

        String finalStepValueExpected = "z^2 - x^2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);


        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult3Expected, stepOne.getReason());
        assertEquals(finalStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepTwoResult3Expected, finalStep.getReason());
    }

    //TODO fazer um exemplo com um monomio de soma e diferença, e ao cubo

    @Test()
    public void identify_two_terms_cube_scenery_one_with_success() {
        //Arrange
        String expression = "(i - 5) ^ 3";

        String stepTwoValueExpected = "i^3 - 3 * i^2 * 5 + 3 * i * 5^2 - 5^3";
        String stepThreeValueExpected = "i^3 - 3 * i^2 * 5 + 3 * i * 25 - 125";
        String finalStepValueExpected = "i^3 - 15i^2 + 75i - 125";

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
    public void identify_two_terms_cube_scenery_two_with_success() {
        //Arrange
        String expression = "(4 - 3) ^ 3";

        String stepTwoValueExpected = "4^3 - 3 * 4^2 * 3 + 3 * 4 * 3^2 - 3^3";
        String stepThreeValueExpected = "64 - 3 * 16 * 3 + 3 * 4 * 9 - 27";
        String stepFourValueExpected = "64 - 144 + 108 - 27";
        String finalStepValueExpected = "1";

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
        assertEquals(finalStepResultExpected, finalStep.getReason());
    }

    @Test()
    public void identify_two_terms_cube_scenery_three_with_success() {
        //Arrange
        String expression = "(a + b) ^ 3";

        String stepTwoValueExpected = "a^3 + 3 * a^2 * b + 3 * a * b^2 + b^3";
        String stepThreeValueExpected = "a^3 + 3a^2b + 3ab^2 + b^3";
        String finalStepValueExpected = "a^3 + b^3 + 3ab^2 + 3a^2b";

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
        assertEquals(stepFourResultExpected, stepThree.getReason());
        assertEquals(finalStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFiveResultExpected, finalStep.getReason());
    }

}
