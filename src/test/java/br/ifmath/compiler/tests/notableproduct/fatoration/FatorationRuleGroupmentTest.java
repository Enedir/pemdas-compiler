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

public class FatorationRuleGroupmentTest {
    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String stepOneResult2Expected;
    private String stepTwoResult2Expected;
    private String stepThreeResult2Expected;


    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new FatorationExpertSystem();
        String stepOneBaseResult = "Identificação do tipo de fatoração a partir da equação inicial: ";
        stepOneResult2Expected = stepOneBaseResult + "Agrupamento. Note que nesse caso temos um elemento em comum " +
                "nos dois primeiros termos e um elemento comum no terceiro e quarto termos.";
        stepTwoResult2Expected = "Colocamos em evidência o elemento que temos em comum nos primeiros termos e somamos " +
                "ao elemento que possuímos em comum nos últimos termos.";
        stepThreeResult2Expected = "Escrevemos a expressão como o produto da soma de dois termos, sem alterar o " +
                "resultado final.";


    }


    //<editor-fold desc="Groupment">

    @Test()
    public void identify_simple_terms_groupment_scenery_one_with_success() {
        //Arrange
        String expression = "2 + 6 + 3 + 9";

        String stepTwoValueExpected = "2 * (1 + 3) + 3 * (1 + 3)";
        String lastStepValueExpected = "(2 + 3) * (1 + 3)";

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
        String expression = "3y^2 + 6y^3 - y + 2y^2";

        String stepTwoValueExpected = "3y^2 * (1 + 2y) - y * (1 + 2y)";
        String lastStepValueExpected = "(3y^2 - y) * (1 + 2y)";

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

}
