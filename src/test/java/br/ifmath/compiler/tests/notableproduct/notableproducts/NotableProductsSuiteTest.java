package br.ifmath.compiler.tests.notableproduct.notableproducts;


import br.ifmath.compiler.domain.expertsystem.notableproduct.notableproducts.NotableProductsRuleApplyCorrectFormula;
import br.ifmath.compiler.domain.expertsystem.notableproduct.notableproducts.NotableProductsRuleIdentification;
import br.ifmath.compiler.domain.expertsystem.notableproduct.notableproducts.NotableProductsRuleMultiplication;
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
