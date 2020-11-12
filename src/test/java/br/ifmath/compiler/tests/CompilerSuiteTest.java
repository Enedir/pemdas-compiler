package br.ifmath.compiler.tests;

import br.ifmath.compiler.domain.expertsystem.notableproduct.notableproducts.NotableProductsRuleSum;
import br.ifmath.compiler.tests.linearequation.LinearEquationSuiteTest;
import br.ifmath.compiler.tests.notableproduct.fatoration.FatorationSuiteTest;
import br.ifmath.compiler.tests.notableproduct.notableproducts.NotableProductsSuiteTest;
import br.ifmath.compiler.tests.polynomial.PolynomialSuiteTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        InvalidStructureErrorCaseTest.class,
        LinearEquationSuiteTest.class,
        PolynomialSuiteTest.class,
        NotableProductsSuiteTest.class,
        FatorationSuiteTest.class
})
public class CompilerSuiteTest {
}
