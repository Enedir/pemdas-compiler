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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class FatorationRuleCommonFactorTest {
    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String stepOneResult1Expected;
    private String stepTwoResult1Expected;


    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new FatorationExpertSystem();
        String stepOneBaseResult = "Identificação do tipo de fatoração a partir da equação inicial: ";
        stepOneResult1Expected = stepOneBaseResult + "Fator comum em evidência.";
        stepTwoResult1Expected = "Verificamos o elemento que temos em comum e colocamos em evidência.";


    }

    @Test()
    public void identify_simple_terms_common_factor_scenery_one_with_success() {
        //Arrange
        String expression = "x^2 + 5x";

        String lastStepValueExpected = "x * (x + 5)";

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
        String expression = "2j^2 + 12j^5 - 8";

        String lastStepValueExpected = "2 * (j^2 + 6j^5 - 4)";

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
    public void identify_number_common_factor_scenery_one_with_success() {
        //Arrange
        String expression = "2 + 4 - 8 - 12";

        String lastStepValueExpected = "2 * (1 + 2 - 4 - 6)";

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
    public void identify_number_common_factor_scenery_two_with_success() {
        //Arrange
        String expression = "3x^3 + 3 - 6x - 27x^9";

        String lastStepValueExpected = "3 * (x^3 + 1 - 2x - 9x^9)";

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
    public void identify_number_common_factor_scenery_three_with_success() {
        //Arrange
        String expression = "2x + 8x - 4";

        String lastStepValueExpected = "2 * (x + 4x - 2)";

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
    public void identify_number_common_factor_scenery_four_with_success() {
        //Arrange
        String expression = "25 - 35x^2";

        String lastStepValueExpected = "5 * (5 - 7x^2)";

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
    public void identify_number_common_factor_scenery_five_with_success() {
        //Arrange
        String expression = "27x^5 - 54y + 12x^2";

        String lastStepValueExpected = "3 * (9x^5 - 18y + 4x^2)";

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
    public void identify_monomial_common_factor_scenery_one_with_success() {
        //Arrange
        String expression = "4x^2 + 2x - 12x^3 - 8x^4";

        String lastStepValueExpected = "2x * (2x + 1 - 6x^2 - 4x^3)";

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
    public void identify_monomial_common_factor_scenery_two_with_success() {
        //Arrange
        String expression = "-3x - 9x - 24x^4 + 18x^2";

        String lastStepValueExpected = "3x * (-1 - 3 - 8x^3 + 6x)";

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
    public void identify_monomial_common_factor_scenery_three_with_success() {
        //Arrange
        String expression = "-4x + 15x^6 - 28x^2 + 48x^3";

        String lastStepValueExpected = "x * (-4 + 15x^5 - 28x + 48x^2)";

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
    public void identify_monomial_with_exponent_common_factor_scenery_one_with_success() {
        //Arrange
        String expression = "-30z^4 + 16z^2 - 4z^8 + 10z^6";

        String lastStepValueExpected = "2z^2 * (-15z^2 + 8 - 2z^6 + 5z^4)";

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
    public void identify_monomial_with_exponent_common_factor_scenery_two_with_success() {
        //Arrange
        String expression = "a^7 + 5a^4 - 4a^8 + 15a^5";

        String lastStepValueExpected = "a^4 * (a^3 + 5 - 4a^4 + 15a)";

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
    public void identify_multiple_patterns_common_factor_scenery_one_with_success() {
        //Arrange
        String expression = "3x^3 + 6x^6 - 3x - 27x^9";

        String lastStepValueExpected = "3x * (x^2 + 2x^5 - 1 - 9x^8)";

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
    public void identify_multiple_patterns_common_factor_scenery_two_with_success() {
        //Arrange
        String expression = "8x^8 - 2x^2 - 4x^4 + 6x^6";

        String lastStepValueExpected = "2x^2 * (4x^6 - 1 - 2x^2 + 3x^4)";

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
    public void identify_multiple_patterns_common_factor_scenery_three_with_success() {
        //Arrange
        String expression = "3y^5 + 6y^3 + 12y^6";

        String lastStepValueExpected = "3y^3 * (y^2 + 2 + 4y^3)";

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


}
