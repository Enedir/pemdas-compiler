package br.ifmath.compiler.tests.notableproduct.fatoration;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.*;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.FatorationExpertSystem;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FatorationRuleIdentificationTest {
    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String stepOneResult1Expected;
    private String stepOneResult2Expected;
    private String stepOneResult3Expected;
    private String stepOneResult4Expected;
    private String stepOneResult5PlusExpected;
    private String stepOneResult5MinusExpected;
    private String stepOneResult6Expected;
    private String stepTwoResult1Expected;
    private String stepTwoResult2Expected;
    private String stepTwoResult3PlusExpected;
    private String stepTwoResult4Expected;
    private String stepTwoResult5PlusExpected;
    private String stepTwoResult5MinusExpected;
    private String stepTwoResult6Expected;
    private String stepThreeResult2Expected;
    private String stepThreeResult3PlusExpected;
    private String stepThreeResult4Expected;
    private String stepThreeResult5PlusExpected;
    private String stepThreeResult5MinusExpected;
    private String stepThreeResult6Expected;


    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new FatorationExpertSystem();
        String stepOneBaseResult = "Identificação do tipo de fatoração a partir da equação inicial: ";
        stepOneResult1Expected = stepOneBaseResult + "Fator comum em evidência.";
        stepOneResult2Expected = stepOneBaseResult + "Agrupamento.\n\nNote que nesse caso temos um elemento em comum " +
                "nos dois primeiros termos e um elemento comum no terceiro e quarto termos.";
        stepOneResult3Expected = stepOneBaseResult + "Trinômio quadrado perfeito.\n\nNote que a expressão é formada " +
                "por três monômios em que o primeiro e o último termo são quadrados e o termo cental é o dobro do " +
                "produto entre o priemiro termo e o segundo termo.";
        stepOneResult4Expected = stepOneBaseResult + "Diferença de dois quadrados.";
        stepOneResult5PlusExpected = stepOneBaseResult + "Cubo perfeito (cubo da soma).";
        stepOneResult5MinusExpected = stepOneBaseResult + "Cubo perfeito (cubo da diferença).";
        stepOneResult6Expected = stepOneBaseResult + "Trinômio do segundo grau.\n\nNote que a expressão é um trinômio no formato ax^2 + bx + c.";


        stepTwoResult1Expected = "Verificamos o elemento que temos em comum e colocamos em evidência.";

        stepTwoResult2Expected = "Colocamos em evidência o elemento que temos em comum nos primeiros termos e somamos " +
                "ao elemento que possuímos em comum nos últimos termos.";
        stepThreeResult2Expected = "Escrevemos a expressão como o produto da soma de dois termos, sem alterar o " +
                "resultado final.";

        stepTwoResult3PlusExpected = "Escrevemos a expressão no formato a^2 + 2 * a * b + b^2, identificando os " +
                "elementos que estão elevados ao quadrado e os respectivos produtos.";
        stepThreeResult3PlusExpected = "Identificamos os elementos a e b e escrevemos o resultado como o quadrado da " +
                "soma, no formato (a + b)^2.";

        stepTwoResult4Expected = "Escrevemos a expressão no formato a^2 - b^2, identificando os elementos que estão " +
                "elevados ao quadrado.";
        stepThreeResult4Expected = "Escrevemos a expressão como o produto da soma pela diferença de dois termos.";

        stepTwoResult5PlusExpected = "Escrevemos a expressão no formato a^3 + 3 * a^2 * b + 3 * a * b^2 + b^3, " +
                "identificando os elementos que estão elevados ao cubo, ao quadrado e os respectivos produtos.";
        stepTwoResult5MinusExpected = "Escrevemos a expressão no formato a^3 - 3 * a^2 * b + 3 * a * b^2 - b^3, " +
                "identificando os elementos que estão elevados ao cubo, ao quadrado e os respectivos produtos.";
        stepThreeResult5PlusExpected = "Identificamos os elementos a e b e escrevemos o resultado como o quadrado da " +
                "soma, no formato (a + b)^3.";
        stepThreeResult5MinusExpected = "Identificamos os elementos a e b e escrevemos o resultado como o quadrado da " +
                "diferença, no formato (a - b)^3.";

        stepTwoResult6Expected = "Escrevemos a expressão no formato x^2 + (b/a)x + (c/a), identificando os elementos " +
                "que estão elevados ao quadrado e as respectivas divisões.";
        stepThreeResult6Expected = "Identificamos dois elementos x' e x'' tal que x' + x'' = -(b/a) e x' * x'' = c/a " +
                "ou utilizando a fórmula de Bháskara e escrevemos o resultado como um produto a * (x - x') * (x - x'').";

    }

    //<editor-fold desc="Common Factor">

    @Test()
    public void identify_simple_terms_common_factor_scenery_one_with_success() {
        //Arrange
        String expression = "2x + 5x";

        String lastStepValueExpected = "x * (2 + 5)";

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
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepTwoResult1Expected, finalStep.getReason());
    }

    @Test()
    public void identify_simple_terms_common_factor_scenery_two_with_success() {
        //Arrange
        String expression = "2a + 5a - 9a - a";

        String lastStepValueExpected = "a * (2 + 5 - 9 - 1)";

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
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepTwoResult1Expected, finalStep.getReason());
    }

    //</editor-fold>

    //<editor-fold desc="Groupment">

    @Test()
    public void identify_simple_terms_groupment_scenery_one_with_success() {
        //Arrange
        String expression = "3x + 9 + x^2 + 3x";

        String stepTwoValueExpected = "3 * (x + 3) + x * (x + 3)";
        String lastStepValueExpected = "(3 + x) * (x + 3)";

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
        assertEquals(stepOneResult2Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult2Expected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult2Expected, finalStep.getReason());
    }

    @Test()
    public void identify_simple_terms_groupment_scenery_two_with_success() {
        //Arrange
        String expression = "6 - 6x^2 + x^2 - x^4";

        String stepTwoValueExpected = "6 * (1 - x^2) + x^2 * (1 - x^2)";
        String lastStepValueExpected = "(6 + x^2) * (1 - x^2)";

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
        assertEquals(stepOneResult2Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult2Expected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult2Expected, finalStep.getReason());
    }

    @Test()
    public void identify_simple_terms_groupment_scenery_three_with_success() {
        //Arrange
        String expression = "4a^4 + 2a^2 - 6a^4 + 3a^2";

        String stepTwoValueExpected = "2a^2 * (2a^2 + 1) - 3a^2 * (2a^2 + 1)";
        String lastStepValueExpected = "(2a^2 - 3a^2) * (2a^2 + 1)";

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
        assertEquals(stepOneResult2Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult2Expected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult2Expected, finalStep.getReason());
    }

    //</editor-fold>

    //<editor-fold desc="Perfect Square Trinomial">
    @Test()
    public void identify_simple_perfect_square_trinomial_scenery_one_with_success() {
        //Arrange
        String expression = "x^2 + 6x + 9";

        String stepTwoValueExpected = "(x  ) ^ 2 + 2 * x * 3 + (3  ) ^ 2";
        String lastStepValueExpected = "(x + 3) ^ 2";

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
        assertEquals(stepTwoResult3PlusExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult3PlusExpected, finalStep.getReason());
    }

    @Test()
    public void identify_simple_perfect_square_trinomial_scenery_two_with_success() {
        //Arrange
        String expression = "36 + 12x^3 + x^6";

        String stepTwoValueExpected = "(6  ) ^ 2 + 2 * 6 * x^3 + (x^3  ) ^ 2";
        String lastStepValueExpected = "(6 + x^3) ^ 2";

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
        assertEquals(stepTwoResult3PlusExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult3PlusExpected, finalStep.getReason());
    }

    //</editor-fold>

    //<editor-fold desc="Difference of Two Squares">
    @Test()
    public void identify_simple_difference_of_two_squares_scenery_one_with_success() {
        //Arrange
        String expression = "25 - x^2";

        String stepTwoValueExpected = "(5  ) ^ 2 - (x  ) ^ 2";
        String lastStepValueExpected = "(5 + x) * (5 - x)";

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
        assertEquals(stepOneResult4Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult4Expected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult4Expected, finalStep.getReason());
    }

    @Test()
    public void identify_simple_difference_of_two_squares_scenery_two_with_success() {
        //Arrange
        String expression = "81 - 49";

        String stepTwoValueExpected = "(9  ) ^ 2 - (7  ) ^ 2";
        String lastStepValueExpected = "(9 + 7) * (9 - 7)";

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
        assertEquals(stepOneResult4Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult4Expected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult4Expected, finalStep.getReason());
    }
    //</editor-fold>

    //<editor-fold desc="Perfect Cube">
    @Test()
    public void identify_simple_terms_perfect_cube_scenery_one_with_success() {
        //Arrange
        String expression = "x^3 + 6x^2 + 12x + 8";

        String stepTwoValueExpected = "(x  ) ^ 3 + 3 * (x)^2 * 2 + 3 * x * (2)^2 + (2  ) ^ 3";
        String lastStepValueExpected = "(x + 2) ^ 3";

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
        assertEquals(stepOneResult5PlusExpected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult5PlusExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult5PlusExpected, finalStep.getReason());
    }

    @Test()
    public void identify_simple_terms_perfect_cube_scenery_two_with_success() {
        //Arrange
        String expression = "64 - 240 + 300 - 125";

        String stepTwoValueExpected = "(4  ) ^ 3 - 3 * (4)^2 * 5 + 3 * 4 * (5)^2 - (5  ) ^ 3";
        String lastStepValueExpected = "(4 - 5) ^ 3";

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
        assertEquals(stepOneResult5MinusExpected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult5MinusExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult5MinusExpected, finalStep.getReason());
    }

    //</editor-fold>

    //<editor-fold desc="Two Binomial Product">
    @Test()
    public void identify_simple_binomial_product_scenery_one_with_success() {
        //Arrange
        String expression = "x^2 - 5x + 6";

        String stepTwoValueExpected = "x^2 + (-5/1)x + (6/1)";
        String lastStepValueExpected = "1 * (x - 3) * (x - 2)";

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
        assertEquals(stepOneResult6Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult6Expected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult6Expected, finalStep.getReason());
    }

    @Test()
    public void identify_simple_binomial_product_scenery_two_with_success() {
        //Arrange
        String expression = "x^2 + 7x + 10";

        String stepTwoValueExpected = "x^2 + (7/1)x + (10/1)";
        String lastStepValueExpected = "1 * (x + 2) * (x + 5)";

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
        assertEquals(stepOneResult6Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult6Expected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult6Expected, finalStep.getReason());
    }

    @Test()
    public void identify_simple_binomial_product_scenery_three_with_success() {
        //Arrange
        String expression = "2x^2 + 4x - 16";

        String stepTwoValueExpected = "x^2 + (4/2)x + (-16/2)";
        String lastStepValueExpected = "2 * (x - 2) * (x + 4)";

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
        assertEquals(stepOneResult6Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult6Expected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult6Expected, finalStep.getReason());
    }

    //</editor-fold>
}
