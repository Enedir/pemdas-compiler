package br.ifmath.compiler.tests.polynomial;


import br.ifmath.compiler.tests.polynomial.addAndSub.PolynomialAddAndSubRuleGroupSimilarTermsTest;
import br.ifmath.compiler.tests.polynomial.addAndSub.PolynomialAddAndSubRuleShiftSignTest;
import br.ifmath.compiler.tests.polynomial.numericValue.PolynomialRuleMultiplyNumbersTest;
import br.ifmath.compiler.tests.polynomial.numericValue.PolynomialRuleNumbersPotentiationTest;
import br.ifmath.compiler.tests.polynomial.numericValue.PolynomialRuleSubstituteVariableTest;
import br.ifmath.compiler.tests.polynomial.numericValue.PolynomialRuleSumNumbersTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        PolynomialRuleMultiplyNumbersTest.class,
        PolynomialRuleNumbersPotentiationTest.class,
        PolynomialRuleSubstituteVariableTest.class,
        PolynomialRuleSumNumbersTest.class,
        PolynomialAddAndSubRuleShiftSignTest.class,
        PolynomialAddAndSubRuleGroupSimilarTermsTest.class
})

public class PolynomialSuiteTest {
}
