package br.ifmath.compiler.tests.notableproduct.fatoration;

import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.commonfactor.FatorationRuleCommonFactor;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        FatorationRuleCommonFactorTest.class,
        FatorationRuleDifferenceOfTwoSqauresTest.class,
        FatorationRuleGroupmentTest.class,
        FatorationRuleIdentificationTest.class,
        FatorationRulePerfectCubeTest.class,
        FatorationRulePerfectPolynomialTest.class,
        FatorationRuleTwoBinomialProductTest.class
})
public class FatorationSuiteTest {
}
