package br.ifmath.compiler.tests;

import br.ifmath.compiler.tests.linearequation.LinearEquationSuiteTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        InvalidStructureErrorCaseTest.class,
        LinearEquationSuiteTest.class
})
public class CompilerSuiteTest {
}
