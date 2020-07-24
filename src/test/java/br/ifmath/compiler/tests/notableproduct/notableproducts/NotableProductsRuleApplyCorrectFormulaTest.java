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

public class NotableProductsRuleApplyCorrectFormulaTest {
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
    public void apply_two_terms_square_formula_scenery_one_with_success() {
        //Arrange
        String expression = "(6 + x) ^ 2";

        String stepTwoValueExpected = "6^2 + 2 * 6 * x + x^2";
        String stepThreeValueExpected = "36 + 2 * 6 * x + x^2";
        String stepFourValueExpected = "36 + 12x + x^2";
        String finalStepValueExpected = "x^2 + 12x + 36";


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
    public void apply_two_terms_square_formula_scenery_two_with_success() {
        //Arrange
        String expression = "(b - c) ^ 2";

        String stepTwoValueExpected = "b^2 - 2 * b * c + c^2";
        String stepThreeValueExpected = "b^2 - 2bc + c^2";
        String finalStepValueExpected = "b^2 + c^2 - 2bc";

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
        assertEquals(stepOneResult2Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult2Expected, stepTwo.getReason());
        assertEquals(stepThreeValueExpected, stepThree.getMathExpression());
        assertEquals(stepFourResultExpected, stepThree.getReason());
        assertEquals(finalStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepFiveResultExpected, finalStep.getReason());
    }

    @Test()
    public void apply_two_terms_square_formula_scenery_three_with_success() {
        //Arrange
        String expression = "(1 - 3) ^ 2";

        String stepTwoValueExpected = "1^2 - 2 * 1 * 3 + 3^2";
        String stepThreeValueExpected = "1 - 2 * 1 * 3 + 9";
        String stepFourValueExpected = "1 - 6 + 9";
        String finalStepValueExpected = "4";

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
    public void apply_sum_and_dif_formula_scenery_one_with_success() {
        //Arrange
        String expression = "(4 - 2) * (4 + 2)";

        String stepTwoValueExpected = "4^2 - 2^2";
        String stepThreeValueExpected = "16 - 4";
        String finalStepValueExpected = "12";

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
    public void apply_sum_and_dif_formula_scenery_two_with_success() {
        //Arrange
        String expression = "(s - 3t) * (s + 3t)";

        String stepTwoValueExpected = "s^2 - ( 3t ) ^ 2";
        String finalStepValueExpected = "s^2 - 9t^2";
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
    public void apply_sum_and_dif_formula_scenery_three_with_success() {
        //Arrange
        String expression = "(a - 5b) * (a + 5b)";

        String stepTwoValueExpected = "a^2 - ( 5b ) ^ 2";
        String finalStepValueExpected = "a^2 - 25b^2";

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
    public void apply_two_terms_cube_formula_scenery_one_with_success() {
        //Arrange
        String expression = "(2 + x) ^ 3";

        String stepTwoValueExpected = "2^3 + 3 * 2^2 * x + 3 * 2 * x^2 + x^3";
        String stepThreeValueExpected = "8 + 3 * 4 * x + 3 * 2 * x^2 + x^3";
        String stepFourValueExpected = "8 + 12x + 6x^2 + x^3";
        String finalStepValueExpected = "x^3 + 6x^2 + 12x + 8";

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
    public void apply_two_terms_cube_formula_scenery_two_with_success() {
        //Arrange
        String expression = "(e - g) ^ 3";

        String stepTwoValueExpected = "e^3 - 3 * e^2 * g + 3 * e * g^2 - g^3";
        String stepThreeValueExpected = "e^3 - 3e^2g + 3eg^2 - g^3";
        String finalStepValueExpected = "e^3 - g^3 + 3eg^2 - 3e^2g";

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

    @Test()
    public void apply_two_terms_cube_formula_scenery_three_with_success() {
        //Arrange
        String expression = "(2 + 1) ^ 3";

        String stepTwoValueExpected = "2^3 + 3 * 2^2 * 1 + 3 * 2 * 1^2 + 1^3";
        String stepThreeValueExpected = "8 + 3 * 4 * 1 + 3 * 2 * 1 + 1";
        String stepFourValueExpected = "8 + 12 + 6 + 1";
        String finalStepValueExpected = "27";

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
}
