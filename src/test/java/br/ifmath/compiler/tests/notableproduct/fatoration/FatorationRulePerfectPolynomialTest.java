package br.ifmath.compiler.tests.notableproduct.fatoration;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.AnswerType;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.FatorationExpertSystem;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FatorationRulePerfectPolynomialTest {
    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String stepOneResult3Expected;
    private String stepTwoResult3PlusExpected;
    private String stepTwoResult3MinusExpected;
    private String stepThreeResult3PlusExpected;
    private String stepThreeResult3MinusExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new FatorationExpertSystem();
        String stepOneBaseResult = "Identificação do tipo de fatoração a partir da equação inicial: ";
        stepOneResult3Expected = stepOneBaseResult + "Trinômio quadrado perfeito. Note que a expressão é formada " +
                "por três monômios em que o primeiro e o último termo são quadrados e o termo cental é o dobro do " +
                "produto entre o primeiro termo e o segundo termo.";
        stepTwoResult3PlusExpected = "Escrevemos a expressão no formato &ascr;&sup2; &plus; 2 &middot; &ascr; &middot; &bscr; " +
                "&plus; &bscr;&sup2;, identificando os elementos que estão elevados ao quadrado e os respectivos produtos.";
        stepTwoResult3MinusExpected = "Escrevemos a expressão no formato &ascr;&sup2; &minus; 2 &middot; &ascr; &middot; &bscr; " +
                "&plus; &bscr;&sup2;, identificando os elementos que estão elevados ao quadrado e os respectivos produtos.";
        stepThreeResult3PlusExpected = "Identificamos os elementos &ascr; e &bscr;, e escrevemos o resultado como o quadrado da " +
                "soma, no formato (&ascr; &plus; &bscr;)&sup2;.";
        stepThreeResult3MinusExpected = "Identificamos os elementos &ascr; e &bscr;, e escrevemos o resultado como o quadrado da " +
                "diferença, no formato (&ascr; &minus; &bscr;)&sup2;.";
    }


    //<editor-fold desc="Perfect Square Trinomial">
    @Test()
    public void identify_simple_perfect_square_trinomial_scenery_one_with_success() {
        //Arrange
        String expression = "z^2 + 4z + 4";

        String stepTwoValueExpected = "(z  ) ^ 2 + 2 * z * 2 + (2  ) ^ 2";
        String lastStepValueExpected = "(z + 2) ^ 2";

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
        String expression = "25 + 10x + x^2";

        String stepTwoValueExpected = "(5  ) ^ 2 + 2 * 5 * x + (x  ) ^ 2";
        String lastStepValueExpected = "(5 + x) ^ 2";

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
    public void identify_only_numbers_perfect_square_trinomial_scenery_one_with_success() {
        //Arrange
        String expression = "25 + 40 + 16";

        String stepTwoValueExpected = "(5  ) ^ 2 + 2 * 5 * 4 + (4  ) ^ 2";
        String lastStepValueExpected = "(5 + 4) ^ 2";

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
    public void identify_only_numbers_perfect_square_trinomial_scenery_two_with_success() {
        //Arrange
        String expression = "36 - 84 + 49";

        String stepTwoValueExpected = "(6  ) ^ 2 - 2 * 6 * 7 + (7  ) ^ 2";
        String lastStepValueExpected = "(6 - 7) ^ 2";

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
        assertEquals(stepTwoResult3MinusExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult3MinusExpected, finalStep.getReason());
    }

    @Test()
    public void identify_only_variables_perfect_square_trinomial_scenery_one_with_success() {
        //Arrange
        String expression = "a^4 - 2a^3 + a^2";

        String stepTwoValueExpected = "(a^2  ) ^ 2 - 2 * a^2 * a + (a  ) ^ 2";
        String lastStepValueExpected = "(a^2 - a) ^ 2";

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
        assertEquals(stepTwoResult3MinusExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult3MinusExpected, finalStep.getReason());
    }

    @Test()
    public void identify_only_variables_perfect_square_trinomial_scenery_two_with_success() {
        //Arrange
        String expression = "a^8 - 2a^10 + a^12";

        String stepTwoValueExpected = "(a^4  ) ^ 2 - 2 * a^4 * a^6 + (a^6  ) ^ 2";
        String lastStepValueExpected = "(a^4 - a^6) ^ 2";

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
        assertEquals(stepTwoResult3MinusExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult3MinusExpected, finalStep.getReason());
    }

    @Test()
    public void identify_variables_and_numbers_perfect_square_trinomial_scenery_one_with_success() {
        //Arrange
        String expression = "4b^2 - 20b + 25";

        String stepTwoValueExpected = "(2b  ) ^ 2 - 2 * 2b * 5 + (5  ) ^ 2";
        String lastStepValueExpected = "(2b - 5) ^ 2";

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
        assertEquals(stepTwoResult3MinusExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult3MinusExpected, finalStep.getReason());
    }

    @Test()
    public void identify_variables_and_numbers_perfect_square_trinomial_scenery_two_with_success() {
        //Arrange
        String expression = "64 + 64t^3 + 16t^6";

        String stepTwoValueExpected = "(8  ) ^ 2 + 2 * 8 * 4t^3 + (4t^3  ) ^ 2";
        String lastStepValueExpected = "(8 + 4t^3) ^ 2";

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
    public void identify_variables_and_numbers_perfect_square_trinomial_scenery_three_with_success() {
        //Arrange
        String expression = "25g^8 + 40g^5 + 16g^2";

        String stepTwoValueExpected = "(5g^4  ) ^ 2 + 2 * 5g^4 * 4g + (4g  ) ^ 2";
        String lastStepValueExpected = "(5g^4 + 4g) ^ 2";

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
    public void identify_variables_and_numbers_perfect_square_trinomial_scenery_four_with_success() {
        //Arrange
        String expression = "y^2 - 22y + 121";

        String stepTwoValueExpected = "(y  ) ^ 2 - 2 * y * 11 + (11  ) ^ 2";
        String lastStepValueExpected = "(y - 11) ^ 2";

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
        assertEquals(stepTwoResult3MinusExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult3MinusExpected, finalStep.getReason());
    }

    @Test()
    public void identify_variables_and_numbers_perfect_square_trinomial_scenery_five_with_success() {
        //Arrange
        String expression = "36 - 12x + x^2";

        String stepTwoValueExpected = "(6  ) ^ 2 - 2 * 6 * x + (x  ) ^ 2";
        String lastStepValueExpected = "(6 - x) ^ 2";

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
        assertEquals(stepTwoResult3MinusExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult3MinusExpected, finalStep.getReason());
    }

    //</editor-fold>

}
