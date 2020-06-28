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
    private String stepTwoResult1Expected;
    private String stepTwoResult2Expected;
    private String stepTwoResult3Expected;
    private String stepTwoResult4Expected;
    private String stepTwoResult5Expected;
//    private String stepThreeResultExpected;
//    private String stepFourResultExpected;
//    private String finalResultExplicationExpected;


    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new NotableProductsExpertSystem();
        String stepTwoBaseResult = "Identificação do tipo de produto notável: ";
        stepTwoResult1Expected = stepTwoBaseResult + "Quadrado da soma de dois termos.";
        stepTwoResult2Expected = stepTwoBaseResult + "Quadrado da diferença de dois termos.";
        stepTwoResult3Expected = stepTwoBaseResult + "Produto da soma pela diferença de dois termos.";
        stepTwoResult4Expected = stepTwoBaseResult + "Cubo da soma de dois termos.";
        stepTwoResult5Expected = stepTwoBaseResult + "Cubo da diferença de dois termos.";
//        stepThreeResultExpected = "";
//        stepFourResultExpected = "";
//        finalResultExplicationExpected = "";


    }

    @Test()
    public void identify_two_terms_square_scenery_one_with_success() {
        //Arrange
        String expression = "(x + 2)^2";

        String stepTwoValueExpected = "x^2 + 2 * x * 2 + 2^2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult1Expected, stepTwo.getReason());
    }

    @Test()
    public void identify_two_terms_square_scenery_two_with_success() {
        //Arrange
        String expression = "(6 - 2)^2";

        String stepTwoValueExpected = "6^2 - 2 * 6 * 2 + 2^2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult2Expected, stepTwo.getReason());
    }

    @Test()
    public void identify_two_terms_square_scenery_three_with_success() {
        //Arrange
        String expression = "(x + y)^2";

        String stepTwoValueExpected = "x^2 + 2 * x * y + y^2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult1Expected, stepTwo.getReason());
    }

    @Test()
    public void identify_sum_and_dif_product_scenery_one_with_success() {
        //Arrange
        String expression = "(2 + y) * (2 - y)";

        String stepTwoValueExpected = "2^2 - y^2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult3Expected, stepTwo.getReason());
    }

    @Test()
    public void identify_sum_and_dif_product_scenery_two_with_success() {
        //Arrange
        String expression = "(3 + 1) * (3 - 1)";

        String stepTwoValueExpected = "3^2 - 1^2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult3Expected, stepTwo.getReason());
    }

    @Test()
    public void identify_sum_and_dif_product_scenery_three_with_success() {
        //Arrange
        String expression = "(z + x) * (z - x)";

        String stepTwoValueExpected = "z^2 - x^2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult3Expected, stepTwo.getReason());
    }

    @Test()
    public void identify_two_terms_cube_scenery_one_with_success() {
        //Arrange
        String expression = "(i - 5)^3";

        String stepTwoValueExpected = "i^3 - 3 * i^2 * 5 + 3 * i * 5^2 - 5^3";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult5Expected, stepTwo.getReason());
    }

    @Test()
    public void identify_two_terms_cube_scenery_two_with_success() {
        //Arrange
        String expression = "(4 - 3)^3";

        String stepTwoValueExpected = "4^3 - 3 * 4^2 * 3 + 3 * 4 * 3^2 - 3^3";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult5Expected, stepTwo.getReason());
    }

    @Test()
    public void identify_two_terms_cube_scenery_three_with_success() {
        //Arrange
        String expression = "(a + b)^3";

        String stepTwoValueExpected = "a^3 + 3 * a^2 * b + 3 * a * b^2 + b^3";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult4Expected, stepTwo.getReason());
    }

}
