package br.ifmath.compiler.domain.expertsystem.notableproduct.notableproducts;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class NotableProductsRuleSum implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return isThereOnlyPlusSigns(source);
    }


    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        this.source = source.get(0);

        ExpandedQuadruple root = this.source.findQuadrupleByResult(this.source.getLeft());
        this.applySum(root);

        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        List<Step> steps = new ArrayList<>();
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Somando os números restantes."));

        return steps;
    }

    private void applySum(ExpandedQuadruple root) {
        root.setArgument1(String.valueOf(sumNumbers(root.getResult(), false)));
        root.setOperator("");
        root.setArgument2("");
    }

    private int sumNumbers(String param, boolean lastOperationIsMinus) {
        int sum = 0;

        /*
          verifica se entre a operacao ha uma variavel temporaria, e abre a mesma, para poder obter os numeros contidos.
          caso seja a soma entre numeros, entrara no else.
         */

        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = this.source.findQuadrupleByResult(param);

            if (expandedQuadruple.isNegative()) {
                sum -= sumNumbers(expandedQuadruple.getArgument1(), false);
            } else {
                sum += sumNumbers(expandedQuadruple.getArgument1(), lastOperationIsMinus);
                sum += sumNumbers(expandedQuadruple.getArgument2(), expandedQuadruple.isMinus());
            }
        } else {
            /*
              entra no if caso seja uma subtracao, e no else caso seja uma soma
             */
            if (lastOperationIsMinus)
                sum -= Integer.parseInt(param);
            else
                sum += Integer.parseInt(param);
        }

        return sum;
    }

    private boolean isThereOnlyPlusSigns(List<ThreeAddressCode> source) {
        for (ExpandedQuadruple expandedQuadruple : source.get(0).getExpandedQuadruples()) {
            if (!expandedQuadruple.isPlusOrMinus() || StringUtil.isVariable(expandedQuadruple.getArgument1())
                    || StringUtil.isVariable(expandedQuadruple.getArgument2()))
                return false;
        }
        return true;
    }

}
