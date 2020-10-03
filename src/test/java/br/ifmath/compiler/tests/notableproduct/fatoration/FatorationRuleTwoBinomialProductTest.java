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

public class FatorationRuleTwoBinomialProductTest {
    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String stepOneResult6Expected;
    private String stepTwoResult6Expected;
    private String stepThreeResult6Expected;


    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new FatorationExpertSystem();
        String stepOneBaseResult = "Identificação do tipo de fatoração a partir da equação inicial: ";
        stepOneResult6Expected = stepOneBaseResult + "Trinômio do segundo grau. " +
                "Note que a expressão é um trinômio no formato &ascr;&xscr;&sup2; &plus; &bscr;&xscr; &plus; &cscr;.";
        stepTwoResult6Expected = "Escrevemos a expressão no formato &xscr;&sup2; &plus; (&bscr;&sol;&ascr;)&xscr; &plus; " +
                "(&cscr;&sol;&ascr;), identificando os elementos que estão elevados ao quadrado e as respectivas divisões.";
        stepThreeResult6Expected = "Identificamos dois elementos &xscr;&apos; e &xscr;&apos;&apos; tal que &xscr;&apos; &plus; " +
                "&xscr;&apos;&apos; &equals; &minus;(&bscr;&sol;&ascr;) e &xscr;&apos; &middot; &xscr;&apos;&apos; " +
                "&equals; &cscr;&sol;&ascr; ou utilizando a fórmula de Bháskara e escrevemos o resultado como um produto " +
                "&ascr; &middot; (&xscr; &minus; &xscr;&apos;) &middot; (&xscr; &minus; &xscr;&apos;&apos;).";

    }

    //<editor-fold desc="Two Binomial Product">
    @Test()
    public void identify_simple_binomial_product_scenery_one_with_success() {
        //Arrange
        String expression = "x^2 - 12x + 20";

        String stepTwoValueExpected = "x^2 + (-12/1)x + (20/1)";
        String lastStepValueExpected = "1 * (x - 10) * (x - 2)";

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
        String expression = "2x^2 - 16x - 18";

        String stepTwoValueExpected = "x^2 + (-16/2)x + (-18/2)";
        String lastStepValueExpected = "2 * (x - 9) * (x + 1)";

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
        String expression = "3x^2 - 15x + 12";

        String stepTwoValueExpected = "x^2 + (-15/3)x + (12/3)";
        String lastStepValueExpected = "3 * (x - 4) * (x - 1)";

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
