package br.ifmath.compiler.tests.notableproduct.notableproducts;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        NotableProductsRuleIdentificationTest.class,
        NotableProductsRuleApplyCorrectFormulaTest.class,
        NotableProductsRulePowerTest.class,
        NotableProductsRuleMultiplicationTest.class,
        NotableProductsRuleSortTest.class,
        NotableProductsRuleSumTest.class
})
public class NotableProductsSuiteTest {
}
