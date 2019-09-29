package br.ifmath.compiler.tests.linearequation;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.*;
import br.ifmath.compiler.domain.expertsystem.linearequation.LinearEquationExpertSystem;
import br.ifmath.compiler.domain.grammar.InvalidDistributiveOperationException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class LinearEquationSucessCaseTest {

    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String finalResultExplicationExpected;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new LinearEquationExpertSystem();
        finalResultExplicationExpected = "Resultado final.";
    }

    @Test()
    public void linear_equation_should_sucess_case_scenery_one_with_success()  {
        //Arrange
        String expression = "x = 1";

        String lastStepValueExpected = "x = 1";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void  linear_equation_should_sucess_case_scenery_two_with_fail()  {
        //Arrange
        String expression = "x = y";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidAlgebraicExpressionException.class));
        }
    }

    @Test()
    public void  linear_equation_should_sucess_case_scenery_three_with_fail()  {
        //Arrange
        String expression = "-(a+b)+(4,7/7)=0";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidAlgebraicExpressionException.class));
        }
    }

    @Test()
    public void  linear_equation_should_sucess_case_scenery_four_with_fail()  {
        //Arrange
        String expression = "[(a+b)+(4/7)]/(3/5)+cos(x + 5)=0";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidDistributiveOperationException.class));
        }
    }

    @Test()
    public void  linear_equation_should_sucess_case_scenery_five_with_fail()  {
        //Arrange
        String expression = "cos(-x+5)^2=(7+g)";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidDistributiveOperationException.class));
        }
    }

    @Test()
    public void  linear_equation_should_sucess_case_scenery_six_with_fail() {
        //Arrange
        String expression = "cos([-x+5]/[4 + x])^2=(7+g)";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidDistributiveOperationException.class));
        }
    }


    @Test()
    public void  linear_equation_should_sucess_case_scenery_seven_with_fail()  {
        //Arrange
        String expression = "a={[((a+ b)*3)-c]/4}";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidAlgebraicExpressionException.class));
        }
    }

    @Test()
    public void  linear_equation_should_sucess_case_scenery_eight_with_fail()  {
        //Arrange
        String expression = "a={[((a+ b)*3)-c]/4}";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidAlgebraicExpressionException.class));
        }
    }

    @Test()
    public void  linear_equation_should_sucess_case_scenery_nine_with_fail() {
        //Arrange
        String expression = "raiz(2x;4)=(7+g)";

        String lastStepValueExpected = "x = 1";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidDistributiveOperationException.class));
        }
    }

    @Test()
    public void  linear_equation_should_sucess_case_scenery_teen_with_fail()  {
        //Arrange
        String expression = "log(2x;4)=-7";

        String lastStepValueExpected = "2x log 4 =  -7";

        // Act
        IAnswer answer = null;
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidDistributiveOperationException.class));
        }
    }

    @Test()
    public void  linear_equation_should_sucess_case_scenery_eleven_with_fail()  {
        //Arrange
        String expression = "log10(raizq(3x+2))=-7";
        String lastStepValueExpected = "3x + 2 raizq 2 log10 10 =  -7";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidDistributiveOperationException.class));
        }
    }

    @Test()
    public void  linear_equation_should_sucess_case_scenery_twelve_with_fail() {
        //Arrange
        String expression = "[(a+b)+(4/7)]^2/(3/5)+cos(x)=0";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidDistributiveOperationException.class));
        }
    }

    @Test()
    public void linear_equation_should_sucess_case_scenery_thirteen_with_success() {
        //Arrange
        String expression = "x^2 + 3x = -5,4";

        String lastStepValueExpected = "x ^ 2 + 3x =  -5,4";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), "Equação inicial.");
    }

    @Test()
    public void linear_equation_should_sucess_case_scenery_fourteen_with_success() {
        //Arrange
        String expression = "2x + 3 = -7";

        String lastStepValueExpected = "x = 2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }


    @Test()
    public void linear_equation_should_sucess_case_scenery_fiveteen_with_success()  {
        //Arrange
        String expression = "2x + 8 + x + 2 = 2x - 5 - x + 7";

        String lastStepValueExpected = "x =  -4";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_sucess_case_scenery_sixteen_with_success() {
        //Arrange
        String expression = "x - (3 + ( 4 - x))  = -x + 2";

        String lastStepValueExpected = "x = 3";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_sucess_case_scenery_seventeen_with_success() {
        //Arrange
        String expression = "7x=-x";

        String lastStepValueExpected = "x = 0";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_sucess_case_scenery_eighteen_with_success() {
        //Arrange
        String expression = "2x*(3 - 1)=0";

        String lastStepValueExpected = "x = 0";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

    @Test()
    public void linear_equation_should_sucess_case_scenery_nineteen_with_success() {
        //Arrange
        String expression = "2x(3 - 1)=0";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidDistributiveOperationException.class));
        }
    }

    @Test()
    public void linear_equation_should_sucess_case_scenery_twenty_with_success() {
        //Arrange
        String expression = "2(3 - 1)=0";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidDistributiveOperationException.class));
        }
    }

    @Test()
    public void linear_equation_should_sucess_case_scenery_twenty_one_with_success() {
        //Arrange
        String expression = "x(3 - 1)=0";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidDistributiveOperationException.class));
        }
    }
    
    @Test()
    public void linear_equation_should_sucess_case_scenery_twenty_two_with_success() {
        //Arrange
        String expression = "2y + 3 = -7";

        String lastStepValueExpected = "y = 2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // Assert
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(finalStep.getMathExpression(), lastStepValueExpected);
        assertEquals(finalStep.getReason(), finalResultExplicationExpected);
    }

}
