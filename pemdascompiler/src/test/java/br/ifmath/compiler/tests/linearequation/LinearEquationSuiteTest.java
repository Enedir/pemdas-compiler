package br.ifmath.compiler.tests.linearequation;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        LinearEquationRuleIsolateAndSumTermsTest.class,
        LinearEquationRuleDistributiveTest.class,
        LinearEquationRuleLeastCommonMultipleTest.class,
        LinearEquationRuleCrossMultiplicationTest.class,
        LinearEquationRuleShiftSignalTest.class,
        LinearEquationRuleGeneralWithCoeficientInVariableTest.class,
        LinearEquationRuleNegativeVariableTest.class,
        LinearEquationSucessCaseTest.class
})
public class LinearEquationSuiteTest {
}
