package br.ifmath.compiler.tests;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.*;
import br.ifmath.compiler.domain.expertsystem.linearequation.LinearEquationExpertSystem;
import br.ifmath.compiler.domain.grammar.InvalidDistributiveOperationException;
import br.ifmath.compiler.domain.grammar.nonterminal.UnrecognizedStructureException;
import br.ifmath.compiler.infrastructure.compiler.UnrecognizedLexemeException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

/**
 * CASOS DE ERRO
 */
public class InvalidStructureErrorCaseTest {

    private ICompiler compiler;
    private IExpertSystem expertSystem;

    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new LinearEquationExpertSystem();
    }

    @Test()
    public void invalid_structure_should_error_case_scenery_one_with_success()  {
        //Arrange
        String expression = "[(a b)+(4/ 7)]^2/(3/5)+cos(x)=0";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidDistributiveOperationException.class));
        }
    }

    @Test()
    public void invalid_structure_should_error_case_scenery_two_with_success()  {
        //Arrange
        String expression = "ldg(2x;4 )=-7";

        // Act
        try {
           compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidDistributiveOperationException.class));
        }
    }

    @Test()
    public void invalid_structure_should_error_case_scenery_three_with_success()  {
        //Arrange
        String expression = "log 2x;4)=-7";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(UnrecognizedStructureException.class));
        }
    }

    @Test()
    public void invalid_structure_should_error_case_scenery_four_with_success()  {
        //Arrange
        String expression = "log(2x;4) -7";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidDistributiveOperationException.class));
        }
    }

    @Test()
    public void invalid_structure_should_error_case_scenery_five_with_success()  {
        //Arrange
        String expression = "log(2x;4=-7";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidDistributiveOperationException.class));
        }
    }

    @Test()
    public void invalid_structure_should_error_case_scenery_six_with_success()  {
        //Arrange
        String expression = "log(2x;4=-7";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidDistributiveOperationException.class));
        }
    }

    @Test()
    public void invalid_structure_should_error_case_scenery_seven_with_success()  {
        //Arrange
        String expression = "log(2x 4)=-7";

        // Act
        try {
            IAnswer answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidDistributiveOperationException.class));
        }
    }

    @Test()
    public void invalid_structure_should_error_case_scenery_eight_with_success() {
        //Arrange
        String expression = "-(a+b)+(4 7)=0";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(UnrecognizedStructureException.class));
        }
    }

    @Test()
    public void invalid_structure_should_error_case_scenery_nine_with_success()  {
        //Arrange
        String expression = "-(a b)+(4/7)=0";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(UnrecognizedStructureException.class));
        }
    }

    @Test()
    public void invalid_structure_should_error_case_scenery_teen_with_success()  {
        //Arrange
        String expression = "-(a+b)+(4%7)=0";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(UnrecognizedLexemeException.class));
        }
    }

    @Test()
    public void invalid_structure_should_error_case_scenery_eleven_with_success(){
        //Arrange
        String expression = "-(a+b)+(4, + 7)=0";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(UnrecognizedLexemeException.class));
        }
    }

    @Test()
    public void invalid_structure_should_error_case_scenery_twelve_with_success()  {
        //Arrange
        String expression = "log(2x;4,3)=-7";
        String lastStepValueExpected = "x = 2";

        // Act

        try {
             compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidDistributiveOperationException.class));
        }
    }

    @Test()
    public void invalid_structure_should_error_case_scenery_thirteen_with_success()  {
        //Arrange
        String expression = "log(2x;x)=-7";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(InvalidDistributiveOperationException.class));
        }
    }
}

