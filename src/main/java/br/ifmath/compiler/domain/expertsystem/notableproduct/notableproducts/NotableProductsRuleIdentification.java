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

    private String identify() {
        ExpandedQuadruple root = this.sources.findQuadrupleByResult(this.sources.getLeft());
        if (StringUtil.match(root.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            if (root.isPotentiation()) {

                ExpandedQuadruple innerQuadruple = this.sources.findQuadrupleByResult(root.getArgument1());

                if (root.getArgument2().equals("2"))
                    return innerQuadruple.isPlus() ? "Quadrado da soma de dois termos." : "Quadrado da diferença de dois termos.";

                if (root.getArgument2().equals("3"))
                    return innerQuadruple.isPlus() ? "Cubo da soma de dois termos." : "Cubo da diferença de dois termos.";


            } else if (root.isTimes() &&
                    StringUtil.match(root.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {

                ExpandedQuadruple leftInnerQuadruple = this.sources.findQuadrupleByResult(root.getArgument1());
                ExpandedQuadruple rightInnerQuadruple = this.sources.findQuadrupleByResult(root.getArgument2());

                if (leftInnerQuadruple.getArgument1().equals(rightInnerQuadruple.getArgument1()) &&
                        leftInnerQuadruple.getArgument2().equals(rightInnerQuadruple.getArgument2()))
                    return "Produto da soma pela diferença de dois termos.";
            }
        }
        return "";
    }

}