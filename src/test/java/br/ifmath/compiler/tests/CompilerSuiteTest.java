package br.ifmath.compiler.tests;

import br.ifmath.compiler.tests.linearequation.LinearEquationSuiteTest;
import br.ifmath.compiler.tests.polynomial.numericValue.PolynomialSuiteTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        InvalidStructureErrorCaseTest.class,
        LinearEquationSuiteTest.class,
        PolynomialSuiteTest.class
})
public class CompilerSuiteTest {
}
