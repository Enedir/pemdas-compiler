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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FatorationRulePerfectCubeTest {
    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String stepOneResult5PlusExpected;
    private String stepOneResult5MinusExpected;
    private String stepTwoResult5PlusExpected;
    private String stepTwoResult5MinusExpected;
    private String stepThreeResult5PlusExpected;
    private String stepThreeResult5MinusExpected;


    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new FatorationExpertSystem();
        String stepOneBaseResult = "Identificação do tipo de fatoração a partir da equação inicial: ";
        stepOneResult5PlusExpected = stepOneBaseResult + "Cubo perfeito (cubo da soma).";
        stepOneResult5MinusExpected = stepOneBaseResult + "Cubo perfeito (cubo da diferença).";
        stepTwoResult5PlusExpected = "Escrevemos a expressão no formato &ascr;&sup3; &plus; 3 &middot; &ascr;&sup2; &middot; &bscr; " +
                "&plus; 3 &middot; &ascr; &middot; &bscr;&sup2; &plus; &bscr;&sup3;, identificando os elementos que " +
                "estão elevados ao cubo, ao quadrado e os respectivos produtos.";
        stepTwoResult5MinusExpected = "Escrevemos a expressão no formato &ascr;&sup3; &minus; 3 &middot; &ascr;&sup2; " +
                "&middot; &bscr; &plus; 3 &middot; &ascr; &middot; &bscr;&sup2; &minus; &bscr;&sup3;, " +
                "identificando os elementos que estão elevados ao cubo, ao quadrado e os respectivos produtos.";
        stepThreeResult5PlusExpected = "Identificamos os elementos &ascr; e &bscr;, e escrevemos o resultado como o quadrado da " +
                "soma, no formato (&ascr; &plus; &bscr;)&sup3;.";
        stepThreeResult5MinusExpected = "Identificamos os elementos &ascr; e &bscr;, e escrevemos o resultado como o quadrado da " +
                "diferença, no formato (&ascr; &minus; &bscr;)&sup3;.";
    }

    //<editor-fold desc="Perfect Cube">
    @Test()
    public void identify_simple_terms_perfect_cube_scenery_one_with_success() {
        //Arrange
        String expression = "j^3 + 9j^2 + 27j + 27";

        String stepTwoValueExpected = "(j  ) ^ 3 + 3 * (j)^2 * 3 + 3 * j * (3)^2 + (3  ) ^ 3";
        String lastStepValueExpected = "(j + 3) ^ 3";

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
    public void identify_only_numbers_perfect_cube_scenery_one_with_success() {
        //Arrange
        String expression = "27 - 54 + 36 - 8";

        String stepTwoValueExpected = "(3  ) ^ 3 - 3 * (3)^2 * 2 + 3 * 3 * (2)^2 - (2  ) ^ 3";
        String lastStepValueExpected = "(3 - 2) ^ 3";

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

    @Test()
    public void identify_only_numbers_perfect_cube_scenery_two_with_success() {
        //Arrange
        String expression = "125 + 75 + 15 + 1";

        String stepTwoValueExpected = "(5  ) ^ 3 + 3 * (5)^2 * 1 + 3 * 5 * (1)^2 + (1  ) ^ 3";
        String lastStepValueExpected = "(5 + 1) ^ 3";

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
    public void identify_only_variables_perfect_cube_scenery_one_with_success() {
        //Arrange
        String expression = "y^6 - 3y^8 + 3y^10 - y^12";

        String stepTwoValueExpected = "(y^2  ) ^ 3 - 3 * (y^2)^2 * y^4 + 3 * y^2 * (y^4)^2 - (y^4  ) ^ 3";
        String lastStepValueExpected = "(y^2 - y^4) ^ 3";

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

    @Test()
    public void identify_only_variables_perfect_cube_scenery_two_with_success() {
        //Arrange
        String expression = "y^9 + 3y^7 + 3y^5 + y^3";

        String stepTwoValueExpected = "(y^3  ) ^ 3 + 3 * (y^3)^2 * y + 3 * y^3 * (y)^2 + (y  ) ^ 3";
        String lastStepValueExpected = "(y^3 + y) ^ 3";

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
    public void identify_variables_and_numbers_perfect_cube_scenery_one_with_success() {
        //Arrange
        String expression = "27x^3 - 54x^2 + 36x - 8";

        String stepTwoValueExpected = "(3x  ) ^ 3 - 3 * (3x)^2 * 2 + 3 * 3x * (2)^2 - (2  ) ^ 3";
        String lastStepValueExpected = "(3x - 2) ^ 3";

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

    @Test()
    public void identify_variables_and_numbers_perfect_cube_scenery_two_with_success() {
        //Arrange
        String expression = "64 + 48x^3 + 12x^6 + x^9";

        String stepTwoValueExpected = "(4  ) ^ 3 + 3 * (4)^2 * x^3 + 3 * 4 * (x^3)^2 + (x^3  ) ^ 3";
        String lastStepValueExpected = "(4 + x^3) ^ 3";

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
    public void identify_variables_and_numbers_perfect_cube_scenery_three_with_success() {
        //Arrange
        String expression = "8 + 36x^3 + 54x^6 + 27x^12";

        String stepTwoValueExpected = "(2  ) ^ 3 + 3 * (2)^2 * 3x^4 + 3 * 2 * (3x^4)^2 + (3x^4  ) ^ 3";
        String lastStepValueExpected = "(2 + 3x^4) ^ 3";

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
    public void identify_variables_and_numbers_perfect_cube_scenery_four_with_success() {
        //Arrange
        String expression = "-1 - 3x + 3x^2 - x^3";

        String stepTwoValueExpected = "(-1) ^ 3 - 3 * (-1) ^ 2 * x + 3 * (-1) * (x)^2 - (x  ) ^ 3";
        String lastStepValueExpected = "(-1 - x) ^ 3";

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

}
