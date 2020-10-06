package br.ifmath.compiler.domain.expertsystem.notableproduct.notableproducts;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class NotableProductsRuleIdentification implements IRule {

    private ThreeAddressCode sources;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        //Sempre será executado o passo de identificar o tipo de produto notável
        return true;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        this.sources = source.get(0);

        String type = this.identify();

        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(this.sources);
        List<Step> steps = new ArrayList<>();
        steps.add(new Step(codes, this.sources.toLaTeXNotation().trim(), this.sources.toMathNotation().trim(),
                "Identificação do tipo de produto notável a partir da equação inicial: " + type));

        return steps;
    }

    /**
     * Identifica qual o tipo de produto notável da equação inicial.
     * @return {@link String} indicando qual o nome do tipo de produto notável.
     */
    private String identify() {
        ExpandedQuadruple root = this.sources.findQuadrupleByResult(this.sources.getLeft());

        if (StringUtil.match(root.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            if (root.isPotentiation()) {

                ExpandedQuadruple innerQuadruple = this.sources.findQuadrupleByResult(root.getArgument1());

                //validação para equação do tipo (a +- b) ^ 2
                if (root.getArgument2().equals("2"))
                    return innerQuadruple.isPlus() ? "Quadrado da soma de dois termos." : "Quadrado da diferença de dois termos.";

                //validação para equação do tipo (a +- b) ^ 3
                if (root.getArgument2().equals("3"))
                    return innerQuadruple.isPlus() ? "Cubo da soma de dois termos." : "Cubo da diferença de dois termos.";


            } else if (root.isTimes() &&
                    StringUtil.match(root.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {

                ExpandedQuadruple leftInnerQuadruple = this.sources.findQuadrupleByResult(root.getArgument1());
                ExpandedQuadruple rightInnerQuadruple = this.sources.findQuadrupleByResult(root.getArgument2());

                String leftArgument1 = getInnerArgument(leftInnerQuadruple.getArgument1(), true);
                String leftArgument2 = getInnerArgument(leftInnerQuadruple.getArgument2(), false);
                String rightArgument1 = getInnerArgument(rightInnerQuadruple.getArgument1(), true);
                String rightArgument2 = getInnerArgument(rightInnerQuadruple.getArgument2(), false);

                //validação para equação do tipo (a + b) * (a - b)
                if (leftArgument1.equals(rightArgument1) && leftArgument2.equals(rightArgument2))
                    return "Produto da soma pela diferença de dois termos.";
            }
        }
        return "";
    }

    private String getInnerArgument(String iterationArgument, boolean isArgument1) {
        if (!StringUtil.match(iterationArgument, RegexPattern.TEMPORARY_VARIABLE.toString()))
            return iterationArgument;
        else {
            ExpandedQuadruple innerQuadruple = this.sources.findQuadrupleByResult(iterationArgument);
            return getInnerArgument((isArgument1) ? innerQuadruple.getArgument1() : innerQuadruple.getArgument2(), isArgument1);
        }
    }

}
