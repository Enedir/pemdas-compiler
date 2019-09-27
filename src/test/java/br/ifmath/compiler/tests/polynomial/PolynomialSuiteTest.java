package br.ifmath.compiler.tests.polynomial;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        PolynomialRuleMultiplyNumbersTest.class,
        PolynomialRuleNumbersPotentiationTest.class,
        PolynomialRuleSubstituteVariableTest.class,
        PolynomialRuleSumNumbersTest.class
})

public class PolynomialSuiteTest {
}
