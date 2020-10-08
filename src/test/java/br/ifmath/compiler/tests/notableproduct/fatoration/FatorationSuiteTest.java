package br.ifmath.compiler.tests.notableproduct.fatoration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        FatorationRuleCommonFactorTest.class,
        FatorationRuleDifferenceOfTwoSquaresTest.class,
        // FatorationRuleGroupmentTest.class,
        FatorationRuleIdentificationTest.class,
        FatorationRulePerfectCubeTest.class,
        FatorationRulePerfectPolynomialTest.class,
        FatorationRuleTwoBinomialProductTest.class
})
public class FatorationSuiteTest {
}
