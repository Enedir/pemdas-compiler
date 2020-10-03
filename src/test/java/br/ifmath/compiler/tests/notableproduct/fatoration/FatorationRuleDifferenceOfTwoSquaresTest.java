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

public class FatorationRuleDifferenceOfTwoSquaresTest {
    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String stepOneResult4Expected;
    private String stepTwoResult4Expected;
    private String stepThreeResult4Expected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new FatorationExpertSystem();
        String stepOneBaseResult = "Identificação do tipo de fatoração a partir da equação inicial: ";
        stepOneResult4Expected = stepOneBaseResult + "Diferença de dois quadrados.";
        stepTwoResult4Expected = "Escrevemos a expressão no formato &ascr;&sup2; &minus; &bscr;&sup2;, identificando os " +
                "elementos que estão elevados ao quadrado.";
        stepThreeResult4Expected = "Escrevemos a expressão como o produto da soma pela diferença de dois termos.";
    }

    //<editor-fold desc="Difference of Two Squares">
    @Test()
    public void identify_simple_difference_of_two_squares_scenery_one_with_success() {
        //Arrange
        String expression = "16 - a^2";

        String stepTwoValueExpected = "(4  ) ^ 2 - (a  ) ^ 2";
        String lastStepValueExpected = "(4 + a) * (4 - a)";

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
    public void identify_only_numbers_difference_of_two_squares_scenery_one_with_success() {
        //Arrange
        String expression = "25 - 49";

        String stepTwoValueExpected = "(5  ) ^ 2 - (7  ) ^ 2";
        String lastStepValueExpected = "(5 + 7) * (5 - 7)";

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
    public void identify_only_variables_difference_of_two_squares_scenery_one_with_success() {
        //Arrange
        String expression = "x^6 - x^4";

        String stepTwoValueExpected = "(x^3  ) ^ 2 - (x^2  ) ^ 2";
        String lastStepValueExpected = "(x^3 + x^2) * (x^3 - x^2)";

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
    public void identify_only_variables_difference_of_two_squares_scenery_two_with_success() {
        //Arrange
        String expression = "x^10 - x^16";

        String stepTwoValueExpected = "(x^5  ) ^ 2 - (x^8  ) ^ 2";
        String lastStepValueExpected = "(x^5 + x^8) * (x^5 - x^8)";

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
    public void identify_numbers_and_variables_difference_of_two_squares_scenery_one_with_success() {
        //Arrange
        String expression = "4x^2 - 9x^6";

        String stepTwoValueExpected = "(2x  ) ^ 2 - (3x^3  ) ^ 2";
        String lastStepValueExpected = "(2x + 3x^3) * (2x - 3x^3)";

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
    public void identify_numbers_and_variables_difference_of_two_squares_scenery_two_with_success() {
        //Arrange
        String expression = "81p^12 - 25p^8";

        String stepTwoValueExpected = "(9p^6  ) ^ 2 - (5p^4  ) ^ 2";
        String lastStepValueExpected = "(9p^6 + 5p^4) * (9p^6 - 5p^4)";

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

}
