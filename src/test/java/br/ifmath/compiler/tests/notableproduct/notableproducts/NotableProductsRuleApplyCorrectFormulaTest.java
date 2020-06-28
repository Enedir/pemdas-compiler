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
}
