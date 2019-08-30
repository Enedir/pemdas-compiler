package br.ifmath.compiler.domain.expertsystem.polynomial;

import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.util.NumberUtil;

import java.util.List;

public class PolynomialRuleSumNumbers implements IRule {
    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return isThereNumbersToSum();
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException {
        return null;
    }


    private boolean isThereNumbersToSum(){
        return false;
    }
}
