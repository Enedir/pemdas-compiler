package br.ifmath.compiler.tests.linearequation;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.*;
import br.ifmath.compiler.domain.expertsystem.linearequation.LinearEquationExpertSystem;
import br.ifmath.compiler.domain.grammar.nonterminal.UnrecognizedStructureException;
import br.ifmath.compiler.infrastructure.compiler.UnrecognizedLexemeException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class LinearEquationRuleIsolateAndSumTermsTest {

    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String isolateAndSumExplicationExpected;
    private String finalResultExplicationExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new LinearEquationExpertSystem();
        isolateAndSumExplicationExpected = "Os termos semelhantes foram isolados. As variáveis foram movidas para a esquerda da igualdade e as constantes foram movidas para a direita. Ao trocar a posição de um termo perante a igualdade, é necessário mudar o seu sinal, aplicando a operação inversa. Neste caso, quando o termo mudou de posição, + passou a ser – e – passou a ser +.";
        finalResultExplicationExpected = "Resultado final.";
    }

    @Test()
    public void  linear_equation_should_isolate_and_sum_term_scenery_one_with_success(){
        //Arrange
        String expression = "x + 3 = 7";

        int positionTwo = 1;

        String stepTwoValueExpected = "x =  -3 + 7";
        String lastStepValueExpected = "x = 4";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(positionTwo);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), isolateAndSumExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_isolate_and_sum_term_scenery_two_with_success()  {
        //Arrange
        String expression = "-x + 3 = 7";

        int positionTwo = 1;

        String stepTwoValueExpected = " -x =  -3 + 7";
        String lastStepValueExpected = "x =  -4";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(positionTwo);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), isolateAndSumExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_isolate_and_sum_term_scenery_three_with_success()  {
        //Arrange
        String expression = "x + 8 + x + 2 = 2x - 5 - x + 7";

        int positionTwo = 1;

        String stepTwoValueExpected = "x + x - 2x + x =  -8 - 2 - 5 + 7";
        String lastStepValueExpected = "x =  -8";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // Assert
        Step stepTwo = answer.getSteps().get(positionTwo);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), isolateAndSumExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_isolate_and_sum_term_scenery_four_with_success() {
        //Arrange
        String expression = "25x - 10 + 100 + 8x = 10x + 20 + 20x + 30x";

        int positionTwo = 1;

        String stepTwoValueExpected = "25x + 8x - 10x - 20x - 30x = 10 - 100 + 20";
        String lastStepValueExpected = "x = 70 / 27";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepTwo = answer.getSteps().get(positionTwo);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(stepTwo.getMathExpression(), stepTwoValueExpected);
        assertEquals(stepTwo.getReason(), isolateAndSumExplicationExpected);
        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_isolate_and_sum_term_scenery_one_with_failed()  {
        //Arrange
        String expression = "x = x + 7";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            // Assert
            assertThat(e, instanceOf(InvalidAlgebraicExpressionException.class));
        }
    }

    @Test()
    public void  linear_equation_should_isolate_and_sum_term_scenery_two_with_failed() {
        //Arrange
        String expression = "x = x - 7";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            // Assert
            assertThat(e, instanceOf(InvalidAlgebraicExpressionException.class));
        }
    }

    @Test()
    public void  linear_equation_should_isolate_and_sum_term_scenery_three_with_failed()  {
        //Arrange
        String expression = "x + 8 = 2x - x + 7";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            // Assert
            assertThat(e, instanceOf(InvalidAlgebraicExpressionException.class));
        }
    }
}
