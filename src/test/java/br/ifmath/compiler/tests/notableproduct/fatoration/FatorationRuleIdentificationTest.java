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

public class FatorationRuleIdentificationTest {
    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String stepOneResult1Expected;
    private String stepOneResult2Expected;
    private String stepOneResult3Expected;
    private String stepOneResult4Expected;
    private String stepOneResult5Expected;
    private String stepTwoResultExpected;
    private String stepThreeResultExpected;
    private String stepFourResultExpected;
    private String finalResultExplicationExpected;


    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new FatorationExpertSystem();
        String stepOneBaseResult = "Identificação do tipo de fatoração a partir da equação inicial: ";
        stepOneResult1Expected = stepOneBaseResult + "Fator comum em evidência.";
        stepOneResult2Expected = stepOneBaseResult + "Quadrado da diferença de dois termos.";
        stepOneResult3Expected = stepOneBaseResult + "Produto da soma pela diferença de dois termos.";
        stepOneResult4Expected = stepOneBaseResult + "Cubo da soma de dois termos.";
        stepOneResult5Expected = stepOneBaseResult + "Cubo da diferença de dois termos.";

        stepTwoResultExpected = "Verificamos o elemento que temos em comum e colocamos em evidência.";
        stepThreeResultExpected = "Removendo os parênteses dos polinômios.";
        stepFourResultExpected = "Agrupando os termos semelhantes.";
        finalResultExplicationExpected = "Soma dos termos semelhantes.";
    }

    @Test()
    public void identify_common_factor_scenery_one_with_success() {
        //Arrange
        String expression = "2x + xy";

        String lastStepValueExpected = "x * (2 + y)";

        // Act)
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
        assertEquals(finalResultExplicationExpected, finalStep.getReason());
    }

}
